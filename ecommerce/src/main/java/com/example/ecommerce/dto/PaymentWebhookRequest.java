package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentWebhookRequest {
    @NotBlank
    private String orderId;
    @NotBlank
    private String status; // SUCCESS or FAILED
    private String paymentId;
}
