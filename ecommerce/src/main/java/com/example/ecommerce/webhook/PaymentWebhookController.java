package com.example.ecommerce.webhook;

import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final PaymentService ps;

    @PostMapping("/payment")
    public Map<String, String> payment(@Valid @RequestBody PaymentWebhookRequest req) {
        ps.handleWebhook(req);
        return Map.of("message", "Webhook processed");
    }
}
