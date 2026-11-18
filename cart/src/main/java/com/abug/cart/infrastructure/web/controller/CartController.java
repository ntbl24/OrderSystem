package com.abug.cart.infrastructure.web.controller;

import com.abug.cart.application.port.in.CartCommandUseCase;
import com.abug.cart.application.port.in.CartQueryUseCase;
import com.abug.cart.application.port.in.CreateCartUseCase;
import com.abug.cart.application.port.in.UpdateCartUseCase;
import com.abug.cart.common.exception.AppException;
import com.abug.cart.common.exception.ErrorCode;
import com.abug.cart.common.response.ApiResponse;
import com.abug.cart.domain.CartId;
import com.abug.cart.domain.ProductId;
import com.abug.cart.domain.UserId;
import com.abug.cart.infrastructure.web.dto.CartResponse;
import com.abug.cart.infrastructure.web.dto.CreateCartItemRequest;
import com.abug.cart.infrastructure.web.dto.CreateCartRequest;
import com.abug.cart.infrastructure.web.dto.UpdateCartRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartCommandUseCase cartUseCase;
    private final CreateCartUseCase createCartUseCase;
    private final UpdateCartUseCase updateCartUseCase;
    private final CartQueryUseCase cartQueryUseCase;

    @GetMapping("/{id}")
    public ApiResponse<CartResponse> getCart(@PathVariable String id) {
        return ApiResponse.success(CartResponse.from(cartQueryUseCase.getCartById(CartId.of(id))));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<CartResponse> getUserCart(@PathVariable String userId) {
        return ApiResponse.success(CartResponse.from(cartQueryUseCase.getCartByUserId(UserId.of(userId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND))));
    }

    @PostMapping
    public ApiResponse<CartResponse> createCart(@Valid @RequestBody CreateCartRequest request) {
        return ApiResponse.success(
                CartResponse.from(createCartUseCase.createCart(request.toCommand())));
    }

    @PutMapping("/{id}")
    public ApiResponse<CartResponse> updateCart(@PathVariable String id,
                                                               @Valid @RequestBody UpdateCartRequest request) {
        return ApiResponse.success(
                CartResponse.from(updateCartUseCase.updateCart(CartId.of(id), request.toCommand())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCart(@PathVariable String id) {
        cartUseCase.deleteCart(CartId.of(id));
        return ApiResponse.success(null);
    }

    @PostMapping("/user/{userId}/items")
    public ApiResponse<CartResponse> addItemToCart(@PathVariable String userId,
                                                                 @Valid @RequestBody CreateCartItemRequest request) {
        return ApiResponse.success(CartResponse.from(
                updateCartUseCase.addItemToCart(UserId.of(userId), request.toCartItemData())));
    }

    @PutMapping("/user/{userId}/items/{productId}")
    public ApiResponse<CartResponse> updateItemQuantity(@PathVariable String userId,
            @PathVariable String productId, @RequestParam int quantity) {
        return ApiResponse.success(CartResponse.from(updateCartUseCase.updateItemQuantity(
                UserId.of(userId), ProductId.of(productId), quantity)));
    }

    @DeleteMapping("/user/{userId}/items/{productId}")
    public ApiResponse<CartResponse> removeItemFromCart(@PathVariable String userId,
                                                                       @PathVariable String productId) {
        return ApiResponse.success(CartResponse.from(updateCartUseCase.removeItemFromCart(
                UserId.of(userId), ProductId.of(productId))));
    }

    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> clearCart(@PathVariable String userId) {
        cartUseCase.clearCart(UserId.of(userId));
        return ApiResponse.success(null);
    }
}
