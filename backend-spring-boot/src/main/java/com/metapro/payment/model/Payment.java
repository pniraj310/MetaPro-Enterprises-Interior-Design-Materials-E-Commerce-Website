package com.metapro.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_order_id", columnList = "order_id"),
    @Index(name = "idx_payments_razorpay_order_id", columnList = "razorpay_order_id"),
    @Index(name = "idx_payments_razorpay_payment_id", columnList = "razorpay_payment_id"),
    @Index(name = "idx_payments_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "order_id", length = 64, nullable = false)
    private String orderId;

    @Column(name = "razorpay_order_id", length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 10, nullable = false)
    private String currency;

    @Column(name = "status", length = 30, nullable = false)
    private String status; // CREATED, AUTHORIZED, PAID, FAILED, REFUNDED

    @Column(name = "method", length = 50)
    private String method; // upi, card, netbanking, etc.

    @Column(name = "signature_verified", nullable = false)
    private Boolean signatureVerified = false;

    @Column(name = "webhook_event_id", length = 100)
    private String webhookEventId;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "customer_phone", length = 50)
    private String customerPhone;

    @Column(name = "customer_address", columnDefinition = "TEXT")
    private String customerAddress;

    @Column(name = "refund_id", length = 100)
    private String refundId;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", columnDefinition = "TEXT")
    private String refundReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
