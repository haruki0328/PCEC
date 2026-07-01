package com.pcparts.ec.service;

import com.pcparts.ec.dto.OrderResponse;
import com.pcparts.ec.exception.BadRequestException;
import com.pcparts.ec.exception.ResourceNotFoundException;
import com.pcparts.ec.model.*;
import com.pcparts.ec.repository.CartItemRepository;
import com.pcparts.ec.repository.OrderRepository;
import com.pcparts.ec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrderFromCart(Long userId, String shippingAddress) {
        List<CartItem> cartItems = cartItemRepository.findByUser_Id(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("カートが空です");
        }

        User user = userRepository.getReferenceById(userId);
        BigDecimal total = BigDecimal.ZERO;

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .shippingAddress(shippingAddress)
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new BadRequestException(product.getName() + "の在庫が不足しています");
            }
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .priceAtPurchase(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();
            order.getItems().add(orderItem);
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getOrderForUser(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("注文が見つかりません: " + orderId));
        return OrderResponse.from(order);
    }
}
