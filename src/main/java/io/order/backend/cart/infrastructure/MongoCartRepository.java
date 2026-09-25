package io.order.backend.cart.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.order.backend.cart.domain.Cart;
import io.order.backend.common.infrastructure.BaseMongoRepository;

@Repository 
public interface MongoCartRepository extends BaseMongoRepository<Cart>, CartRepository {
    Optional<Cart> findByUserId(String userId);
    void deleteByUserId(String userId);  
}
