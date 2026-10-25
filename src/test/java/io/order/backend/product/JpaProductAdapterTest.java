package io.order.backend.product;

import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import io.order.backend.product.infrastructure.persistence.JpaProductAdapter;
import io.order.backend.product.infrastructure.persistence.ProductEntity;
import io.order.backend.product.infrastructure.persistence.ProductJpaRepository;
import io.order.backend.product.infrastructure.persistence.ProductPersistenceMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaProductAdapterTest {
    @Mock ProductJpaRepository repository;
    @InjectMocks JpaProductAdapter adapter;

    @Test
    void findAllById_whenRepeatedIdsAndDeletedRows_shouldBatchDistinctUUIDsAndFilterDeleted() {
        ProductId visible = ProductId.generate();
        ProductId deleted = ProductId.generate();
        ProductEntity activeEntity = entity(visible);
        ProductEntity deletedEntity = entity(deleted);
        deletedEntity.deleted = true;
        when(repository.findAllById(List.of(visible.value(), deleted.value())))
                .thenReturn(List.of(activeEntity, deletedEntity));

        List<Product> found = adapter.findAllById(List.of(visible, visible, deleted));

        assertThat(found).extracting(Product::getId).containsExactly(visible);
        verify(repository).findAllById(List.of(visible.value(), deleted.value()));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAllById_whenEmpty_shouldAvoidDatabaseQuery() {
        assertThat(adapter.findAllById(List.of())).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void lockById_whenReservedProductExists_shouldPreserveInventoryAndVersionThroughMapping() {
        ProductId id = ProductId.generate();
        ProductEntity stored = entity(id);
        stored.stock = 10;
        stored.reserved = 4;
        stored.version = 9L;
        when(repository.lockById(id.value())).thenReturn(Optional.of(stored));

        Product locked = adapter.lockById(id).orElseThrow();
        locked.consume(2);
        ProductEntity copied = new ProductEntity();
        ProductPersistenceMapper.copy(locked, copied);

        assertThat(copied.id).isEqualTo(stored.id);
        assertThat(copied.stock).isEqualTo(8);
        assertThat(copied.reserved).isEqualTo(2);
        assertThat(copied.version).isEqualTo(9L);
        assertThat(copied.price).isEqualByComparingTo("100");
    }

    @Test
    void save_whenNew_shouldPersistAssignedIdAndReturnVersion() {
        Product product = new Product();
        product.setId(ProductId.generate());
        product.setTitle("New Product");
        product.setPrice(new BigDecimal("100"));
        product.setStock(10);
        product.setSku("SKU-1");
        product.setSellerId("seller");

        when(repository.findById(product.getId().value())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            assertThat(entity.createdAt).isNotNull();
            assertThat(entity.updatedAt).isNotNull();
            entity.version = 0L;
            return entity;
        });

        Product saved = adapter.save(product);
        assertThat(saved.getId()).isEqualTo(product.getId());
        assertThat(saved.getVersion()).isZero();
        assertThat(saved.getTitle()).isEqualTo("New Product");
    }

    @Test
    void save_whenStaleVersion_shouldThrowOptimisticLockException() {
        Product product = new Product();
        product.setId(ProductId.generate());
        product.setVersion(1L);

        ProductEntity entity = entity(product.getId());
        entity.version = 2L;

        when(repository.findById(product.getId().value())).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> adapter.save(product))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    private ProductEntity entity(ProductId id) {
        ProductEntity entity = new ProductEntity();
        entity.id = id.value();
        entity.price = new BigDecimal("100");
        return entity;
    }
}
