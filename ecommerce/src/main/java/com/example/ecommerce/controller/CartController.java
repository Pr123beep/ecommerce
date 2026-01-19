package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cs;

    @PostMapping("/add")
    public CartItem add(@Valid @RequestBody AddToCartRequest req) {
        return cs.add(req);
    }

    @GetMapping("/{userId}")
    public List<CartItemResponse> get(@PathVariable String userId) {
        return cs.getCart(userId);
    }

    @DeleteMapping("/{userId}/clear")
    public Map<String, String> clear(@PathVariable String userId) {
        cs.clear(userId);
        return Map.of("message", "Cart cleared successfully");
    }
}
