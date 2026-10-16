package io.order.backend.cart.application.port.in;

import io.order.backend.cart.dto.CartDTO;
import io.order.backend.cart.dto.CartItemDTO;
import java.util.Optional;

public interface CartUseCase {
    CartDTO createCart(CartDTO dto);
    CartDTO updateCart(String id, CartDTO dto);
    void deleteCart(String id);
    Optional<CartDTO> getCartById(String id);
    Optional<CartDTO> getCartByUserId(String id);
    CartDTO addItemToCart(String userId, CartItemDTO item);
    CartDTO updateItemQuantity(String userId, String productId, int quantity);
    CartDTO removeItemFromCart(String userId, String productId);
    void clearCart(String userId);
}
