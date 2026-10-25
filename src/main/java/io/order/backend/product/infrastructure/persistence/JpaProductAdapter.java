package io.order.backend.product.infrastructure.persistence;

import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaProductAdapter implements ProductRepository {
    private final ProductJpaRepository repository;

    @Override
    public Optional<Product> findById(ProductId id) {
        return repository.findById(id.value())
                .filter(e -> !e.isDeleted())
                .map(ProductPersistenceMapper::domain);
    }

    @Override
    public boolean existsById(ProductId id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(ProductId id) {
        repository.findById(id.value()).ifPresent(e -> e.delete());
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = repository.findById(product.getId().value()).orElseGet(ProductEntity::new);
        if (entity.getVersion() != null && !Objects.equals(entity.getVersion(), product.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(ProductEntity.class, product.getId().value());
        }
        ProductPersistenceMapper.copy(product, entity);
        return ProductPersistenceMapper.domain(repository.saveAndFlush(entity));
    }

    @Override
    public Optional<Product> lockById(ProductId id) {
        return repository.lockById(id.value()).map(ProductPersistenceMapper::domain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return repository.existsBySkuAndDeletedFalse(sku);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return repository.findBySkuAndDeletedFalse(sku).map(ProductPersistenceMapper::domain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findTop100ByDeletedFalseOrderById().stream()
                .map(ProductPersistenceMapper::domain)
                .toList();
    }

    @Override
    public Page<Product> findAll(Pageable page) {
        return repository.findByDeletedFalse(
                PageRequest.of(page.getPageNumber(), Math.min(100, page.getPageSize())))
                .map(ProductPersistenceMapper::domain);
    }

    @Override
    public List<Product> findByCategory(String category) {
        return repository.findTop100ByCategoryAndDeletedFalseOrderById(category).stream()
                .map(ProductPersistenceMapper::domain)
                .toList();
    }

    @Override
    public List<Product> findAllById(Collection<ProductId> ids) {
        if (ids.isEmpty()) return List.of();
        List<UUID> uuidList = ids.stream().map(ProductId::value).distinct().toList();
        return repository.findAllById(uuidList).stream()
                .filter(entity -> !entity.isDeleted())
                .map(ProductPersistenceMapper::domain)
                .toList();
    }
}
