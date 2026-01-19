package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddToCartRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String productId;
    @Min(1)
    private Integer quantity;
}
