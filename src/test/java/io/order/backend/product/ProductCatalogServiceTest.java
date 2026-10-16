package io.order.backend.product;

import io.order.backend.product.application.ProductCatalogService;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
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
        Product product = new Product();
        product.setId("p");
        product.setSellerId("seller");
        product.setTitle("Original");
        product.setSku("SKU");
        product.setPrice(BigDecimal.TEN);
        when(products.findById("p")).thenReturn(Optional.of(product));

        var snapshot = service.findById("p").orElseThrow();
        product.setTitle("Changed");

        assertThat(snapshot.id()).isEqualTo("p");
        assertThat(snapshot.sellerId()).isEqualTo("seller");
        assertThat(snapshot.title()).isEqualTo("Original");
        assertThat(snapshot.sku()).isEqualTo("SKU");
        assertThat(snapshot.price()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(snapshot.deleted()).isFalse();
    }

    @Test
    void findById_whenMissing_shouldReturnEmpty() {
        when(products.findById("missing")).thenReturn(Optional.empty());
        assertThat(service.findById("missing")).isEmpty();
    }
}
