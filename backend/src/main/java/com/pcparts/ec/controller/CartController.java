package com.pcparts.ec.controller;

import com.pcparts.ec.dto.CartItemRequest;
import com.pcparts.ec.dto.CartItemResponse;
import com.pcparts.ec.security.UserPrincipal;
import com.pcparts.ec.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return cartService.getCart(principal.getId());
    }

    @PostMapping("/items")
    public CartItemResponse addOrUpdateItem(@AuthenticationPrincipal UserPrincipal principal,
                                             @Valid @RequestBody CartItemRequest request) {
        return cartService.addOrUpdateItem(principal.getId(), request);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal UserPrincipal principal,
                                            @PathVariable Long cartItemId) {
        cartService.removeItem(principal.getId(), cartItemId);
        return ResponseEntity.noContent().build();
    }
}
