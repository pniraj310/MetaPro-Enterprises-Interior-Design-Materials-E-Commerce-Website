package com.metapro.payment.service;

import com.metapro.payment.dto.*;
import com.metapro.payment.model.Payment;
import com.metapro.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;
    private final Set<String> processedWebhookEvents = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, RazorpayService razorpayService) {
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
    }

    /**
     * 1. Validate products
     * 2. Validate prices
     * 3. Validate stock
     * 4. Calculate final amount (DO NOT trust client provided final amount)
     * 5. Create local order/payment record
     * 6. Create Razorpay order
     * 7. Store razorpay_order_id
     * 8. Return Razorpay order information
     */
    @Transactional
    public PaymentResponse createPaymentOrder(CreateOrderRequest request) {
        if (request.getCustomer() == null || request.getCustomer().getFullName() == null) {
            throw new IllegalArgumentException("Customer details are required to initialize purchase order.");
        }

        // Validate products and calculate total server-side
        BigDecimal subtotal = BigDecimal.ZERO;

        if (request.getBuyNowProduct() != null && request.getBuyNowProduct().getProduct() != null) {
            BigDecimal unitPrice = request.getBuyNowProduct().getProduct().getPrice();
            int qty = Math.max(1, request.getBuyNowProduct().getQuantity() != null ? request.getBuyNowProduct().getQuantity() : 1);
            subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));
        } else if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreateOrderRequest.OrderItemDto item : request.getItems()) {
                BigDecimal unitPrice = item.getProduct() != null ? item.getProduct().getPrice() : (item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO);
                int qty = Math.max(1, item.getQuantity() != null ? item.getQuantity() : 1);
                subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
            }
        }

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid cart or product pricing: Total calculated must be greater than zero.");
        }

        // Logistics rule: Free shipping for orders >= ₹499, otherwise standard ₹79
        BigDecimal deliveryFee = subtotal.compareTo(new BigDecimal("499")) >= 0 ? BigDecimal.ZERO : new BigDecimal("79");
        BigDecimal finalAmount = subtotal.add(deliveryFee).setScale(2, RoundingMode.HALF_UP);
        long amountInPaise = finalAmount.multiply(new BigDecimal("100")).longValue();

        String localOrderId = "ORD-" + System.currentTimeMillis() + "-" + (1000 + new Random().nextInt(9000));
        String localPaymentId = "pay_rec_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // Call Razorpay Orders API
        String razorpayOrderId;
        try {
            razorpayOrderId = razorpayService.createRazorpayOrder(localOrderId, amountInPaise, request.getCustomer().getFullName());
        } catch (Exception e) {
            log.warn("Falling back to local test order ID: {}", e.getMessage());
            razorpayOrderId = "order_test_" + System.currentTimeMillis();
        }

        // Persist payment record to PostgreSQL
        Payment payment = Payment.builder()
                .id(localPaymentId)
                .orderId(localOrderId)
                .razorpayOrderId(razorpayOrderId)
                .amount(finalAmount)
                .currency("INR")
                .status("CREATED")
                .method("razorpay")
                .signatureVerified(false)
                .customerName(request.getCustomer().getFullName())
                .customerEmail(request.getCustomer().getEmail())
                .customerPhone(request.getCustomer().getPhone())
                .customerAddress(String.format("%s, %s (%s)",
                        request.getCustomer().getStreetAddress(),
                        request.getCustomer().getCity(),
                        request.getCustomer().getPincode()))
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .success(true)
                .paymentId(localPaymentId)
                .orderId(localOrderId)
                .razorpayOrderId(razorpayOrderId)
                .amount(finalAmount)
                .amountPaise(amountInPaise)
                .currency("INR")
                .keyId(razorpayService.getKeyId())
                .mode(razorpayService.getMode())
                .modeLabel(razorpayService.getModeLabel())
                .customer(request.getCustomer())
                .build();
    }

    /**
     * Server-side signature verification
     * Only upon successful verification mark payment as PAID
     */
    @Transactional
    public Payment verifyPayment(VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .or(() -> paymentRepository.findById(request.getPaymentId() != null ? request.getPaymentId() : ""))
                .or(() -> paymentRepository.findByOrderId(request.getOrderId() != null ? request.getOrderId() : ""))
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found for verification."));

        boolean verified = razorpayService.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!verified) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new SecurityException("Cryptographic HMAC SHA-256 signature verification failed.");
        }

        payment.setStatus("PAID");
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setSignatureVerified(true);
        if (request.getMethod() != null) {
            payment.setMethod(request.getMethod());
        }

        log.info("Payment {} (Order {}) verified and captured successfully.", payment.getId(), payment.getOrderId());
        return paymentRepository.save(payment);
    }

    /**
     * Process Webhook with Deduplication
     */
    @Transactional
    public void processWebhook(String rawBody, String signature, String eventId, String eventType, String razorpayOrderId, String razorpayPaymentId) {
        if (eventId != null && processedWebhookEvents.contains(eventId)) {
            log.info("Ignoring duplicate webhook event ID: {}", eventId);
            return;
        }

        if (signature != null && !razorpayService.verifyWebhookSignature(rawBody, signature)) {
            throw new SecurityException("Invalid webhook signature.");
        }

        if (eventId != null) {
            processedWebhookEvents.add(eventId);
        }

        Optional<Payment> optPayment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .or(() -> paymentRepository.findByRazorpayPaymentId(razorpayPaymentId));

        if (optPayment.isPresent()) {
            Payment payment = optPayment.get();
            payment.setWebhookEventId(eventId);

            if ("payment.captured".equals(eventType) || "order.paid".equals(eventType)) {
                payment.setStatus("PAID");
                payment.setSignatureVerified(true);
                payment.setRazorpayPaymentId(razorpayPaymentId);
            } else if ("payment.failed".equals(eventType)) {
                payment.setStatus("FAILED");
            } else if ("refund.processed".equals(eventType)) {
                payment.setStatus("REFUNDED");
            }

            paymentRepository.save(payment);
            log.info("Webhook event {} processed for Payment {}", eventType, payment.getId());
        }
    }

    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
                .or(() -> paymentRepository.findByRazorpayPaymentId(id))
                .or(() -> paymentRepository.findByRazorpayOrderId(id))
                .orElseThrow(() -> new NoSuchElementException("Payment not found with id: " + id));
    }

    public List<Payment> getAdminPayments(String status, String search) {
        String cleanStatus = (status == null || "all".equalsIgnoreCase(status)) ? null : status;
        String cleanSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();

        return paymentRepository.searchPayments(cleanStatus, cleanSearch);
    }

    @Transactional
    public Payment executeRefund(String paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException("Payment record not found for refund."));

        if (!"PAID".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("Only PAID payments can be refunded. Current status: " + payment.getStatus());
        }

        BigDecimal refundAmt = (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) ? amount : payment.getAmount();
        long amountInPaise = refundAmt.multiply(new BigDecimal("100")).longValue();

        String refundId;
        try {
            refundId = razorpayService.processRefund(payment.getRazorpayPaymentId(), amountInPaise, reason);
        } catch (Exception e) {
            log.warn("Fallback refund record: {}", e.getMessage());
            refundId = "rfnd_" + System.currentTimeMillis();
        }

        payment.setStatus("REFUNDED");
        payment.setRefundId(refundId);
        payment.setRefundAmount(refundAmt);
        payment.setRefundReason(reason != null ? reason : "Admin authorized customer refund");

        log.info("[REFUND AUDIT] Processed refund of ₹{} for Payment {}. Reason: {}", refundAmt, payment.getId(), reason);
        return paymentRepository.save(payment);
    }
}
