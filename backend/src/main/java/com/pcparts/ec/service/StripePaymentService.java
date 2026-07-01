package com.pcparts.ec.service;

import com.pcparts.ec.dto.CheckoutResponse;
import com.pcparts.ec.exception.BadRequestException;
import com.pcparts.ec.model.Order;
import com.pcparts.ec.model.OrderStatus;
import com.pcparts.ec.model.Product;
import com.pcparts.ec.repository.CartItemRepository;
import com.pcparts.ec.repository.OrderRepository;
import com.pcparts.ec.repository.ProductRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @Value("${app.cors.allowed-origin}")
    private String frontendOrigin;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public CheckoutResponse createCheckoutSession(Long userId, String shippingAddress) {
        Order order = orderService.createOrderFromCart(userId, shippingAddress);

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendOrigin + "/orders/" + order.getId() + "?success=true")
                .setCancelUrl(frontendOrigin + "/cart?canceled=true")
                .putMetadata("orderId", String.valueOf(order.getId()));

        for (var item : order.getItems()) {
            long unitAmount = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(100)).longValueExact();
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("jpy")
                                            .setUnitAmount(unitAmount)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.getProductName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        try {
            Session session = Session.create(paramsBuilder.build());
            order.setStripeSessionId(session.getId());
            orderRepository.save(order);
            return CheckoutResponse.builder()
                    .orderId(order.getId())
                    .checkoutUrl(session.getUrl())
                    .sessionId(session.getId())
                    .build();
        } catch (StripeException e) {
            throw new BadRequestException("Stripeセッションの作成に失敗しました: " + e.getMessage());
        }
    }

    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            throw new BadRequestException("Webhook署名の検証に失敗しました");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            deserializer.getObject().ifPresent(obj -> {
                Session session = (Session) obj;
                markOrderAsPaid(session.getId());
            });
        }
    }

    private void markOrderAsPaid(String stripeSessionId) {
        orderRepository.findByStripeSessionId(stripeSessionId).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.PAID);
                for (var item : order.getItems()) {
                    Product product = item.getProduct();
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                    productRepository.save(product);
                }
                orderRepository.save(order);
                cartItemRepository.deleteByUser_Id(order.getUser().getId());
            }
        });
    }
}
