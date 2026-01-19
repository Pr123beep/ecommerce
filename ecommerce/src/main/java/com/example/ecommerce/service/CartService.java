package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cr;
    private final ProductService ps;

    public CartItem add(AddToCartRequest req) {
        Product p = ps.get(req.getProductId());
        if (req.getQuantity() <= 0) throw new BadRequestException("Quantity must be >= 1");

        CartItem ci = cr.findByUserIdAndProductId(req.getUserId(), req.getProductId())
                .orElse(CartItem.builder().userId(req.getUserId()).productId(req.getProductId()).quantity(0).build());

        int newQty = ci.getQuantity() + req.getQuantity();
        if (p.getStock() < newQty) throw new BadRequestException("Not enough stock to add. Available: " + p.getStock());

        ci.setQuantity(newQty);
        return cr.save(ci);
    }

    public List<CartItemResponse> getCart(String userId) {
        List<CartItem> items = cr.findByUserId(userId);
        return items.stream().map(ci -> {
            Product p = ps.get(ci.getProductId());
            return CartItemResponse.builder()
                    .id(ci.getId())
                    .productId(ci.getProductId())
                    .quantity(ci.getQuantity())
                    .product(CartItemResponse.ProductMini.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .price(p.getPrice())
                            .build())
                    .build();
        }).toList();
    }

    public List<CartItem> rawCart(String userId) {
        return cr.findByUserId(userId);
    }

    public void clear(String userId) {
        cr.deleteByUserId(userId);
    }
}
