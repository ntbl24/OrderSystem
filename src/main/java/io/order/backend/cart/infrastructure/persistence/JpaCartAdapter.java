package cart.infrastructure.persistence;

import io.order.backend.cart.application.port.out.CartRepository;

import io.order.backend.cart.domain.*;
import io.order.backend.cart.infrastructure.persistence.*;
import io.order.backend.cart.mapper.CartMapper;

import java.util.*;

import io.order.backend.user.domain.UserId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaCartAdapter implements CartRepository {
    private final CartJpaRepository repository;
    private final CartMapper cartMapper;

    @Override 
    public Optional<Cart> findById(CartId id) {
        return repository.findById(id.value())
            .map(cartMapper::toDomain);
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public Cart save(Cart d) {
        if (d.getId() == null)
            d.setId(UUID.randomUUID().toString());
        var e = repository.findById(d.getId()).orElseGet(CartEntity::new);
        if (e.version != null && !Objects.equals(e.version, d.getVersion()))
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(CartEntity.class, d.getId());
        if (d.getCreatedAt() == null)
            d.setCreatedAt(java.time.LocalDateTime.now());
        d.setUpdatedAt(java.time.LocalDateTime.now());
        CartPersistenceMapper.copy(d, e);
        return CartPersistenceMapper.domain(repository.saveAndFlush(e));
    }

    public Optional<Cart> findByUserId(UserId userId) {
        return repository.findByUserId(userId.value()).map(cartMapper::toDomain);
    }

    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }
}
