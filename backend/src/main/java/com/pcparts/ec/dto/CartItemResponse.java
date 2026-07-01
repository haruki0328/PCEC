package com.pcparts.ec.dto;

import com.pcparts.ec.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal subtotal;

    public static CartItemResponse from(CartItem cartItem) {
        BigDecimal subtotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .product(ProductResponse.from(cartItem.getProduct()))
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
