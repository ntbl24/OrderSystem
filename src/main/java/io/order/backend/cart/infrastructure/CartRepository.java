package io.order.backend.cart.infrastructure;


import java.util.Optional;

import io.order.backend.cart.domain.Cart;

public interface CartRepository{
    Optional<Cart> findByUserId(String userId);
    void deleteByUserId(String userId);
    Cart save(Cart cart);  
    boolean existsById(String id);
    Optional<Cart> findById(String id);
    void deleteById(String id);    
}
