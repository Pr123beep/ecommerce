package com.example.ecommerce.client;

import com.example.ecommerce.dto.PaymentWebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class MockPaymentServiceClient {
    private final RestTemplate rt;

    @Value("${mock-payment.webhook-url}")
    private String webhookUrl;

    @Value("${mock-payment.delay-ms}")
    private long delayMs;

    @Async
    public void triggerWebhookAfterDelay(String orderId, String paymentId, String status) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ignored) {}

        PaymentWebhookRequest req = new PaymentWebhookRequest();
        req.setOrderId(orderId);
        req.setPaymentId(paymentId);
        req.setStatus(status);

        rt.postForEntity(webhookUrl, req, String.class);
    }
}
