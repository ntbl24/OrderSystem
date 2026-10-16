package io.order.backend.cart.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface CartJpaRepository extends JpaRepository<CartEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from CartEntity e where e.id = :id")
    Optional<CartEntity> lockById(@org.springframework.data.repository.query.Param("id") String id);

    Optional<CartEntity> findByUserId(String userId);

    void deleteByUserId(String userId);
}
