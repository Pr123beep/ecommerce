package com.example.ecommerce.service;

import com.example.ecommerce.client.MockPaymentServiceClient;
import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.NotFoundException;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository pyr;
    private final OrderService os;
    private final MockPaymentServiceClient mockClient;

    public Payment createPayment(PaymentRequest req) {
        os.ensureCreated(req.getOrderId());

        Payment existing = pyr.findByOrderId(req.getOrderId()).orElse(null);
        if (existing != null && "SUCCESS".equals(existing.getStatus()))
            throw new BadRequestException("Payment already SUCCESS for order: " + req.getOrderId());

        String payId = "pay_mock_" + UUID.randomUUID();

        Payment p = (existing == null) ? Payment.builder().orderId(req.getOrderId()).build() : existing;
        p.setAmount(req.getAmount());
        p.setStatus("PENDING");
        p.setPaymentId(payId);
        p.setCreatedAt(Instant.now());
        p = pyr.save(p);

        mockClient.triggerWebhookAfterDelay(req.getOrderId(), payId, "SUCCESS");

        return p;
    }

    public void handleWebhook(PaymentWebhookRequest req) {
        Payment p = pyr.findByOrderId(req.getOrderId())
                .orElseThrow(() -> new NotFoundException("Payment not found for order: " + req.getOrderId()));

        if ("SUCCESS".equalsIgnoreCase(req.getStatus())) {
            p.setStatus("SUCCESS");
            if (req.getPaymentId() != null) p.setPaymentId(req.getPaymentId());
            pyr.save(p);
            os.markPaid(req.getOrderId());
        } else if ("FAILED".equalsIgnoreCase(req.getStatus())) {
            p.setStatus("FAILED");
            if (req.getPaymentId() != null) p.setPaymentId(req.getPaymentId());
            pyr.save(p);
            os.markFailed(req.getOrderId());
        } else {
            throw new BadRequestException("Invalid webhook status: " + req.getStatus());
        }
    }
}
