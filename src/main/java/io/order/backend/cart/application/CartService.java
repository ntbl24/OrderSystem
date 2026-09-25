package io.order.backend.cart.application;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartItem;
import io.order.backend.cart.dto.CartDTO;
import io.order.backend.cart.dto.CartItemDTO;
import io.order.backend.cart.infrastructure.CartRepository;
import io.order.backend.cart.mapper.CartMapper;
import io.order.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Transactional 
    public CartDTO createCart(CartDTO cartDTO){
        Cart cart = cartMapper.toEntity(cartDTO);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional 
    public CartDTO updateCart(String id, CartDTO cartDTO){
        Cart existingCart = cartRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + id));
        
        Cart updatedCart = cartMapper.toEntity(cartDTO);
        updatedCart.setId(existingCart.getId());
        updatedCart = cartRepository.save(updatedCart);
        return cartMapper.toDto(updatedCart);
    }

    @Transactional 
    public void deleteCart(String id) {

        if(!cartRepository.existsById(id)){
            throw new ResourceNotFoundException("Cart not found with id: " + id);
        }
        
        cartRepository.deleteById(id);
    }

    public Optional<CartDTO> getCartById(String id){
        return cartRepository.findById(id)
            .map(cartMapper::toDto);
    }

    public Optional<CartDTO> getCartByUserId(String userId){
        return cartRepository.findByUserId(userId)
            .map(cartMapper::toDto);
    }

    @Transactional 
    public CartDTO addItemToCart(String userId, CartItemDTO item){

        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                return newCart;
            });
        CartItem cartItem = new CartItem();
        cartItem.setProductId(item.getProductId());
        cartItem.setProductName(item.getProductName());
        cartItem.setQuantity(item.getQuantity());
        cartItem.setUnitPrice(item.getUnitPrice());
        cartItem.setSubTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        cart.getItems().add(cartItem);
        updateCartTotals(cart);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
        
    }

    @Transactional 
    public CartDTO updateItemQuantity(String userId, String productId, int quantity){
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with userId: " + userId));

        if(quantity <= 0){
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        } else {
            cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                        item.setQuantity(quantity);
                        item.setSubTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    }
                );
        }
        updateCartTotals(cart);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional 
    public CartDTO removeItemFromCart(String userId, String productId){
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with userId: " + userId));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        updateCartTotals(cart);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional 
    public void clearCart(String userId){
        cartRepository.deleteByUserId(userId);
    }

    private void updateCartTotals(Cart cart){
        cart.setSubTotal(cart.getItems().stream()
            .map(CartItem::getSubTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        cart.setTax(cart.getSubTotal().multiply(BigDecimal.valueOf(0.08)));

        if(cart.getSubTotal().compareTo(BigDecimal.valueOf(50)) > 0){
            cart.setShippingCost(BigDecimal.ZERO);
        }
        else{
            cart.setShippingCost(BigDecimal.valueOf(5.99));
        }

        cart.setTotal(cart.getSubTotal()
        .add(cart.getTax())
        .add(cart.getShippingCost())
        .subtract(cart.getDiscount()));
    }

}
