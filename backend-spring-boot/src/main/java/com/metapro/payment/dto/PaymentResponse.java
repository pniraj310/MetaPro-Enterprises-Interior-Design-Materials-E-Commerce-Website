package com.metapro.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private boolean success;
    private String paymentId;
    private String orderId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private Long amountPaise;
    private String currency;
    private String keyId;
    private String mode;
    private String modeLabel;
    private String message;
    private Object customer;
}
