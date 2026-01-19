package com.example.ecommerce.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private String id;
    private String userId;
    private Double totalAmount;
    private String status;
    private List<Item> items;
    private PaymentMini payment;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Item {
        private String productId;
        private Integer quantity;
        private Double price;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentMini {
        private String id;
        private String status;
        private Double amount;
        private String paymentId;
    }
}
