package io.order.backend.product.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from ProductEntity e where e.id = :id")
    Optional<ProductEntity> lockById(@Param("id") UUID id);

    Optional<ProductEntity> findBySkuAndDeletedFalse(String sku);

    boolean existsBySkuAndDeletedFalse(String sku);

    List<ProductEntity> findTop100ByDeletedFalseOrderById();

    List<ProductEntity> findTop100ByCategoryAndDeletedFalseOrderById(String category);

    Page<ProductEntity> findByDeletedFalse(Pageable page);
}
