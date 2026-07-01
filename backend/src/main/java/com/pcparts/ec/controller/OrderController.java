package com.pcparts.ec.controller;

import com.pcparts.ec.dto.OrderResponse;
import com.pcparts.ec.security.UserPrincipal;
import com.pcparts.ec.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return orderService.getOrdersForUser(principal.getId());
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return orderService.getOrderForUser(principal.getId(), id);
    }
}
