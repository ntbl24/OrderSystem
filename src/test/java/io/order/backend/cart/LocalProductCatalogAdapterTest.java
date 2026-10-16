package io.order.backend.cart;

import io.order.backend.cart.infrastructure.local.LocalProductCatalogAdapter;
import io.order.backend.product.application.port.in.ProductCatalogQuery;
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
class LocalProductCatalogAdapterTest {
    @Mock ProductCatalogQuery catalog;
    @InjectMocks LocalProductCatalogAdapter adapter;

    @Test
    void findById_whenPresent_shouldMapToCartOwnedSnapshot() {
        when(catalog.findById("p")).thenReturn(Optional.of(
                new ProductCatalogQuery.Snapshot("p", "seller", "Title", "SKU", BigDecimal.TEN, false)));

        var snapshot = adapter.findById("p").orElseThrow();

        assertThat(snapshot.id()).isEqualTo("p");
        assertThat(snapshot.title()).isEqualTo("Title");
        assertThat(snapshot.price()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void findById_whenMissing_shouldRemainEmpty() {
        when(catalog.findById("missing")).thenReturn(Optional.empty());
        assertThat(adapter.findById("missing")).isEmpty();
    }
}
