package com.metapro.payment.controller;

import com.metapro.payment.dto.RefundRequest;
import com.metapro.payment.model.Payment;
import com.metapro.payment.service.PaymentService;
import com.metapro.payment.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final RazorpayService razorpayService;

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    @Autowired
    public AdminPaymentController(PaymentService paymentService, RazorpayService razorpayService) {
        this.paymentService = paymentService;
        this.razorpayService = razorpayService;
    }

    /**
     * GET /api/admin/payments
     * Returns payments list with filters (all, paid, pending, failed, refunded) and search
     */
    @GetMapping("/payments")
    public ResponseEntity<?> getPayments(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false) String search) {

        List<Payment> payments = paymentService.getAdminPayments(status, search);

        BigDecimal totalPaid = payments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of(
                "payments", payments,
                "totalCount", payments.size(),
                "totalPaidAmount", totalPaid,
                "currency", "INR"
        ));
    }

    /**
     * POST /api/admin/payments/{id}/refund
     * Initiates server-side refund through Razorpay API
     */
    @PostMapping("/payments/{id}/refund")
    public ResponseEntity<?> processRefund(
            @PathVariable String id,
            @RequestBody(required = false) RefundRequest request) {
        try {
            BigDecimal amount = request != null ? request.getAmount() : null;
            String reason = request != null ? request.getReason() : "Admin authorized refund";

            Payment payment = paymentService.executeRefund(id, amount, reason);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Refund processed successfully.",
                    "refundId", payment.getRefundId(),
                    "payment", payment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/payment-settings
     * Provides gateway and webhook status without exposing private secret keys
     */
    @GetMapping("/payment-settings")
    public ResponseEntity<?> getPaymentSettings() {
        String keyId = razorpayService.getKeyId();
        boolean isConfigured = keyId != null && !keyId.contains("YOUR_KEY_ID") && !keyId.isEmpty();
        boolean isWebhookConfigured = webhookSecret != null && !webhookSecret.contains("YOUR_WEBHOOK_SECRET") && !webhookSecret.isEmpty();

        String maskedKey = isConfigured
                ? keyId.substring(0, Math.min(8, keyId.length())) + "••••••••"
                : "Not configured (Using Sandbox)";

        return ResponseEntity.ok(Map.of(
                "provider", "Razorpay",
                "mode", razorpayService.getMode(),
                "modeLabel", razorpayService.getModeLabel(),
                "isKeyConfigured", isConfigured,
                "isWebhookConfigured", isWebhookConfigured,
                "keyId", maskedKey,
                "currency", "INR",
                "gatewayStatus", isConfigured ? "Configured (Active)" : "Test Mode (Sandbox active)",
                "webhookUrl", "/api/payments/webhook"
        ));
    }
}
