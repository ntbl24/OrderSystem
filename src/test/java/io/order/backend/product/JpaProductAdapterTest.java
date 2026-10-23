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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaProductAdapterTest {
    @Mock ProductJpaRepository repository;
    @InjectMocks JpaProductAdapter adapter;

    @Test
    void findAllById_whenRepeatedIdsAndDeletedRows_shouldBatchDistinctStringIdsAndFilterDeleted() {
        ProductId visible = ProductId.generate();
        ProductId deleted = ProductId.generate();
        ProductEntity activeEntity = entity(visible);
        ProductEntity deletedEntity = entity(deleted);
        deletedEntity.deleted = true;
        when(repository.findAllById(List.of(visible.value().toString(), deleted.value().toString())))
                .thenReturn(List.of(activeEntity, deletedEntity));

        List<Product> found = adapter.findAllById(List.of(visible, visible, deleted));

        assertThat(found).extracting(Product::getId).containsExactly(visible);
        verify(repository).findAllById(List.of(visible.value().toString(), deleted.value().toString()));
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
        when(repository.lockById(id.value().toString())).thenReturn(Optional.of(stored));

        Product locked = adapter.lockById(id.value().toString()).orElseThrow();
        locked.consume(2);
        ProductEntity copied = new ProductEntity();
        ProductPersistenceMapper.copy(locked, copied);

        assertThat(copied.id).isEqualTo(stored.id);
        assertThat(copied.stock).isEqualTo(8);
        assertThat(copied.reserved).isEqualTo(2);
        assertThat(copied.version).isEqualTo(9L);
        assertThat(copied.price).isEqualByComparingTo("100");
    }

    private ProductEntity entity(ProductId id) {
        ProductEntity entity = new ProductEntity();
        entity.id = id.value().toString();
        entity.price = new BigDecimal("100");
        return entity;
    }
}
