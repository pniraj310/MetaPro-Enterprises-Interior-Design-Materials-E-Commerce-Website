package com.metapro.payment.controller;

import com.metapro.payment.dto.*;
import com.metapro.payment.model.Payment;
import com.metapro.payment.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payments/create-order
     * Calculates server amount, validates items, creates Razorpay order, returns checkout details
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            PaymentResponse response = paymentService.createPaymentOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating order: " + e.getMessage()));
        }
    }

    /**
     * POST /api/payments/verify
     * Cryptographically verifies HMAC SHA-256 signature
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequest request) {
        try {
            Payment payment = paymentService.verifyPayment(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment signature verified successfully.",
                    "payment", payment
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Verification failure: " + e.getMessage()));
        }
    }

    /**
     * POST /api/payments/webhook
     * Handles Razorpay webhook events with idempotency
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestHeader(value = "X-Razorpay-Event-Id", required = false) String eventId) {
        try {
            JSONObject json = new JSONObject(rawPayload);
            String eventType = json.optString("event");
            String rzpOrderId = null;
            String rzpPaymentId = null;

            if (json.has("payload") && json.getJSONObject("payload").has("payment")) {
                JSONObject entity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                rzpOrderId = entity.optString("order_id");
                rzpPaymentId = entity.optString("id");
            }

            paymentService.processWebhook(rawPayload, signature, eventId, eventType, rzpOrderId, rzpPaymentId);
            return ResponseEntity.ok(Map.of("received", true));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Webhook processing error"));
        }
    }

    /**
     * GET /api/payments/{id}
     * Returns individual payment status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable String id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payment not found"));
        }
    }
}
