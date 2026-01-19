package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentRequest {
    @NotBlank
    private String orderId;
    @Min(1)
    private Double amount;
}
