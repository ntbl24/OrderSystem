package io.order.backend.cart.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.order.backend.cart.application.CartService;
import io.order.backend.cart.dto.CartDTO;
import io.order.backend.cart.dto.CartItemDTO;
import io.order.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("api/v1/cart")
@RequiredArgsConstructor 
public class CartController {
    private final CartService cartService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CartDTO>> createCart(@Valid @RequestBody CartDTO cartDTO){
        ApiResponse<CartDTO> response = ApiResponse.success(cartService.createCart(cartDTO));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCart(@PathVariable String id, @Valid @RequestBody CartDTO cartDTO){
        ApiResponse<CartDTO> response = ApiResponse.success(cartService.updateCart(id, cartDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@PathVariable String id){
        cartService.deleteCart(id);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok(response);
    }

    @PostMapping ("/user/{userId}/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(@PathVariable String userId, @Valid @RequestBody CartItemDTO itemDTO){
        ApiResponse<CartDTO> response = ApiResponse.success(cartService.addItemToCart(userId, itemDTO));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{userId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateItemQuantity(@PathVariable String userId, @PathVariable String productId, @RequestParam int quantity){
        ApiResponse<CartDTO> response = ApiResponse.success(cartService.updateItemQuantity(userId, productId, quantity));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItemFromCart(@PathVariable String userId, @PathVariable String productId){
        ApiResponse<CartDTO> response = ApiResponse.success(cartService.removeItemFromCart(userId, productId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable String userId){
        cartService.clearCart(userId);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok(response);
    }
}
