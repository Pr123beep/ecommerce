package com.example.ecommerce.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItemResponse {
    private String id;
    private String productId;
    private Integer quantity;
    private ProductMini product;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProductMini {
        private String id;
        private String name;
        private Double price;
    }
}
