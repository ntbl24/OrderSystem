package io.order.backend.product.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from ProductEntity e where e.id = :id")
    Optional<ProductEntity> lockById(@org.springframework.data.repository.query.Param("id") String id);

    Optional<ProductEntity> findBySkuAndDeletedFalse(String sku);

    boolean existsBySkuAndDeletedFalse(String sku);

    List<ProductEntity> findTop100ByDeletedFalseOrderById();

    List<ProductEntity> findTop100ByCategoryAndDeletedFalseOrderById(String category);

    Page<ProductEntity> findByDeletedFalse(Pageable page);
}
