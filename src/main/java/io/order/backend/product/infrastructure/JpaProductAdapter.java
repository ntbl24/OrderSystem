package io.order.backend.product.infrastructure;

import io.order.backend.product.application.port.out.ProductRepository;

import io.order.backend.product.domain.*;
import io.order.backend.product.infrastructure.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaProductAdapter implements ProductRepository {
    private final ProductJpaRepository repository;

    public Optional<Product> findById(String id) {
        return repository.findById(id).filter(e -> !e.deleted).map(ProductPersistenceMapper::domain);
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public void deleteById(String id) {
        repository.findById(id).ifPresent(e -> e.deleted = true);
    }

    public Product save(Product d) {
        if (d.getId() == null)
            d.setId(UUID.randomUUID().toString());
        var e = repository.findById(d.getId()).orElseGet(ProductEntity::new);
        if (e.version != null && !Objects.equals(e.version, d.getVersion()))
            throw new ObjectOptimisticLockingFailureException(ProductEntity.class, d.getId());
        if (d.getCreatedAt() == null)
            d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        ProductPersistenceMapper.copy(d, e);
        return ProductPersistenceMapper.domain(repository.saveAndFlush(e));
    }

    public Optional<Product> lockById(String id) {
        return repository.lockById(id).map(ProductPersistenceMapper::domain);
    }

    public boolean existsBySku(String sku) {
        return repository.existsBySkuAndDeletedFalse(sku);
    }

    public Optional<Product> findBySku(String sku) {
        return repository.findBySkuAndDeletedFalse(sku).map(ProductPersistenceMapper::domain);
    }

    public List<Product> findAll() {
        return repository.findTop100ByDeletedFalseOrderById().stream().map(ProductPersistenceMapper::domain).toList();
    }

    public Page<Product> findAll(Pageable page) {
        return repository.findByDeletedFalse(
                PageRequest.of(page.getPageNumber(), Math.min(100, page.getPageSize())))
                .map(ProductPersistenceMapper::domain);
    }

    public List<Product> findByCategory(String category) {
        return repository.findTop100ByCategoryAndDeletedFalseOrderById(category).stream()
                .map(ProductPersistenceMapper::domain).toList();
    }
}
