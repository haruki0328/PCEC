package com.pcparts.ec.dto;

import com.pcparts.ec.model.Order;
import com.pcparts.ec.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private List<OrderItemResponse> items;
    private Instant createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .items(order.getItems().stream().map(OrderItemResponse::from).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }
}
