package com.pcparts.ec.dto;

import com.pcparts.ec.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private BigDecimal priceAtPurchase;
    private Integer quantity;

    public static OrderItemResponse from(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .priceAtPurchase(item.getPriceAtPurchase())
                .quantity(item.getQuantity())
                .build();
    }
}
