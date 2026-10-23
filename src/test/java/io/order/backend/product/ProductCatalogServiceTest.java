package io.order.backend.product;

import io.order.backend.product.application.usecase.ProductCatalogService;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServiceTest {
    @Mock ProductRepository products;
    @InjectMocks ProductCatalogService service;

    @Test
    void findById_whenPresent_shouldReturnDetachedImmutableSnapshot() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        product.setSellerId("seller");
        product.setTitle("Original");
        product.setSku("SKU");
        product.setPrice(BigDecimal.TEN);
        when(products.findById(id)).thenReturn(Optional.of(product));

        var snapshot = service.findById(id).orElseThrow();
        product.setTitle("Changed");

        assertThat(snapshot.id()).isEqualTo(id);
        assertThat(snapshot.sellerId()).isEqualTo("seller");
        assertThat(snapshot.title()).isEqualTo("Original");
        assertThat(snapshot.sku()).isEqualTo("SKU");
        assertThat(snapshot.price()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(snapshot.deleted()).isFalse();
    }

    @Test
    void findById_whenMissing_shouldReturnEmpty() {
        ProductId id = ProductId.generate();
        when(products.findById(id)).thenReturn(Optional.empty());
        assertThat(service.findById(id)).isEmpty();
    }
    @Test
    void findByIds_whenRepeatedIds_shouldUseBatchRepositoryAndReturnImmutableSnapshots() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        product.setTitle("Original");
        var ids = java.util.List.of(id, id);
        when(products.findAllById(ids)).thenReturn(java.util.List.of(product));

        var result = service.findByIds(ids);
        product.setTitle("Changed");

        assertThat(result).hasSize(1);
        assertThat(result.get(id).title()).isEqualTo("Original");
        org.mockito.Mockito.verify(products).findAllById(ids);
        org.mockito.Mockito.verify(products, org.mockito.Mockito.never()).findById(org.mockito.ArgumentMatchers.any(ProductId.class));
        org.assertj.core.api.Assertions.assertThatThrownBy(result::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
