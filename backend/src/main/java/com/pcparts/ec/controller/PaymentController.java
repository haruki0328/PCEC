package com.pcparts.ec.controller;

import com.pcparts.ec.dto.CheckoutRequest;
import com.pcparts.ec.dto.CheckoutResponse;
import com.pcparts.ec.security.UserPrincipal;
import com.pcparts.ec.service.StripePaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripePaymentService stripePaymentService;

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@AuthenticationPrincipal UserPrincipal principal,
                                      @Valid @RequestBody CheckoutRequest request) {
        return stripePaymentService.createCheckoutSession(principal.getId(), request.getShippingAddress());
    }

    @PostMapping("/webhook")
    public void webhook(HttpServletRequest request) throws IOException {
        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String sigHeader = request.getHeader("Stripe-Signature");
        stripePaymentService.handleWebhookEvent(payload, sigHeader);
    }
}
