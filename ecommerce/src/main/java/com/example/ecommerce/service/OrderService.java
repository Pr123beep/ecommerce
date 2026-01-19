package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.NotFoundException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository or;
    private final OrderItemRepository oir;
    private final PaymentRepository pyr;
    private final CartService cs;
    private final ProductService ps;

    public List<OrderResponse> getOrdersByUser(String userId) {
        return or.findByUserId(userId).stream()
                .map(o -> getOrder(o.getId()))
                .toList();
    }

    public OrderResponse createFromCart(String userId) {
        List<CartItem> cart = cs.rawCart(userId);
        if (cart.isEmpty()) throw new BadRequestException("Cart is empty");

        double total = 0.0;

        for (CartItem ci : cart) {
            Product p = ps.get(ci.getProductId());
            if (p.getStock() < ci.getQuantity()) throw new BadRequestException("Insufficient stock for product: " + p.getId());
            total += p.getPrice() * ci.getQuantity();
        }

        Order o = Order.builder()
                .userId(userId)
                .totalAmount(total)
                .status("CREATED")
                .createdAt(Instant.now())
                .build();
        o = or.save(o);

        for (CartItem ci : cart) {
            Product p = ps.get(ci.getProductId());
            OrderItem oi = OrderItem.builder()
                    .orderId(o.getId())
                    .productId(p.getId())
                    .quantity(ci.getQuantity())
                    .price(p.getPrice())
                    .build();
            oir.save(oi);
            ps.reduceStock(p.getId(), ci.getQuantity());
        }

        cs.clear(userId);

        return getOrder(o.getId());
    }

    public OrderResponse getOrder(String orderId) {
        Order o = or.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        List<OrderItem> items = oir.findByOrderId(orderId);
        Payment pay = pyr.findByOrderId(orderId).orElse(null);

        return OrderResponse.builder()
                .id(o.getId())
                .userId(o.getUserId())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .items(items.stream().map(x -> OrderResponse.Item.builder()
                        .productId(x.getProductId())
                        .quantity(x.getQuantity())
                        .price(x.getPrice())
                        .build()).toList())
                .payment(pay == null ? null : OrderResponse.PaymentMini.builder()
                        .id(pay.getId())
                        .status(pay.getStatus())
                        .amount(pay.getAmount())
                        .paymentId(pay.getPaymentId())
                        .build())
                .build();
    }

    public void markPaid(String orderId) {
        Order o = or.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        o.setStatus("PAID");
        or.save(o);
    }

    public void markFailed(String orderId) {
        Order o = or.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        o.setStatus("FAILED");
        or.save(o);
    }

    public void ensureCreated(String orderId) {
        Order o = or.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        if (!"CREATED".equals(o.getStatus())) throw new BadRequestException("Order status must be CREATED to pay. Current: " + o.getStatus());
    }
}
