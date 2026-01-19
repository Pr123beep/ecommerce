package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService os;

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        return os.createFromCart(req.getUserId());
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> history(@PathVariable String userId) {
        return os.getOrdersByUser(userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable String orderId) {
        return os.getOrder(orderId);
    }


}
