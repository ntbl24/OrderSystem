package io.order.backend.cart.infrastructure.web.controller;

import io.order.backend.cart.application.port.in.CartCommandUseCase;
import io.order.backend.cart.application.port.in.CartQueryUseCase;
import io.order.backend.cart.application.port.in.CreateCartUseCase;
import io.order.backend.cart.application.port.in.UpdateCartUseCase;
import io.order.backend.cart.domain.CartId;
import io.order.backend.cart.infrastructure.web.dto.CartResponse;
import io.order.backend.cart.infrastructure.web.dto.CreateCartItemRequest;
import io.order.backend.cart.infrastructure.web.dto.CreateCartRequest;
import io.order.backend.cart.infrastructure.web.dto.UpdateCartRequest;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.common.response.ApiResponse;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/cart")
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
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> createCart(@Valid @RequestBody CreateCartRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(
                CartResponse.from(createCartUseCase.createCart(request.toCommand()))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCart(@PathVariable String id,
                                                               @Valid @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                CartResponse.from(updateCartUseCase.updateCart(CartId.of(id), request.toCommand()))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@PathVariable String id) {
        cartUseCase.deleteCart(CartId.of(id));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/user/{userId}/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(@PathVariable String userId,
                                                                 @Valid @RequestBody CreateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(CartResponse.from(
                updateCartUseCase.addItemToCart(UserId.of(userId), request.toCartItemData()))));
    }

    @PutMapping("/user/{userId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(@PathVariable String userId,
            @PathVariable String productId, @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.success(CartResponse.from(updateCartUseCase.updateItemQuantity(
                UserId.of(userId), ProductId.of(productId), quantity))));
    }

    @DeleteMapping("/user/{userId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(@PathVariable String userId,
                                                                       @PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success(CartResponse.from(updateCartUseCase.removeItemFromCart(
                UserId.of(userId), ProductId.of(productId)))));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable String userId) {
        cartUseCase.clearCart(UserId.of(userId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
