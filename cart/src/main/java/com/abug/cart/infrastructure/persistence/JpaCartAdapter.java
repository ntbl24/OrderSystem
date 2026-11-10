package com.abug.cart.infrastructure.persistence;

import com.abug.cart.application.port.out.CartRepository;
import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.cart.domain.UserId;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaCartAdapter implements CartRepository {
    private final CartJpaRepository repository;

    public Optional<Cart> findById(CartId id) {
        return repository.findById(id.value()).map(CartPersistenceMapper::domain);
    }

    public boolean existsById(CartId id) {
        return repository.existsById(id.value());
    }

    public void deleteById(CartId id) {
        repository.deleteById(id.value());
    }

    public Cart save(Cart cart) {
        CartEntity entity = repository.findById(cart.getId().value())
            .orElseGet(()-> new CartEntity(cart.getId().value(),cart.getUserId().value(),cart.getSubTotal(),cart.getShippingCost(),cart.getTotal(),cart.getTax(),cart.getCouponCode(),cart.getDiscount()));
        if (!Objects.equals(entity.getVersion(), cart.getVersion()))
            throw new ObjectOptimisticLockingFailureException(CartEntity.class, cart.getId().value());
        CartPersistenceMapper.copy(cart, entity);
        return CartPersistenceMapper.domain(repository.saveAndFlush(entity));
    }

    public Optional<Cart> findByUserId(UserId userId) {
        return repository.findByUserId(userId.value()).map(CartPersistenceMapper::domain);
    }

    public void deleteByUserId(UserId userId) {
        repository.deleteByUserId(userId.value());
        repository.flush();
    }
}
