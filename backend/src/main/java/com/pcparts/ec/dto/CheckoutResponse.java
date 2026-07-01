package com.pcparts.ec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CheckoutResponse {
    private Long orderId;
    private String checkoutUrl;
    private String sessionId;
}
