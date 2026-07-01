package com.pcparts.ec.service;

import com.pcparts.ec.dto.CartItemRequest;
import com.pcparts.ec.dto.CartItemResponse;
import com.pcparts.ec.exception.BadRequestException;
import com.pcparts.ec.exception.ResourceNotFoundException;
import com.pcparts.ec.model.CartItem;
import com.pcparts.ec.model.Product;
import com.pcparts.ec.model.User;
import com.pcparts.ec.repository.CartItemRepository;
import com.pcparts.ec.repository.ProductRepository;
import com.pcparts.ec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItemResponse> getCart(Long userId) {
        return cartItemRepository.findByUser_Id(userId).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    public CartItemResponse addOrUpdateItem(Long userId, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + request.getProductId()));

        if (request.getQuantity() > product.getStockQuantity()) {
            throw new BadRequestException("在庫数を超えています");
        }

        CartItem cartItem = cartItemRepository.findByUser_IdAndProduct_Id(userId, request.getProductId())
                .orElseGet(() -> {
                    User user = userRepository.getReferenceById(userId);
                    return CartItem.builder().user(user).product(product).quantity(0).build();
                });

        cartItem.setQuantity(request.getQuantity());
        return CartItemResponse.from(cartItemRepository.save(cartItem));
    }

    public void removeItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("カート内商品が見つかりません: " + cartItemId));
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new BadRequestException("このカート商品を削除する権限がありません");
        }
        cartItemRepository.delete(cartItem);
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUser_Id(userId);
    }
}
