package io.order.backend.inventory;

import io.order.backend.inventory.application.port.in.InventoryUseCase;
import io.order.backend.inventory.infrastructure.local.ProductStockAdapter;
import io.order.backend.product.application.port.in.ProductStockUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockAdapterTest {
    @Mock ProductStockUseCase products;
    @InjectMocks ProductStockAdapter adapter;

    @Test
    void reserve_productSnapshot_mapsToInventoryContract() {
        when(products.reserve(Map.of("p", 2))).thenReturn(Map.of("p", new ProductStockUseCase.Snapshot(
                "p", "seller", "Product", "sku", BigDecimal.TEN, false)));

        assertThat(adapter.reserve(Map.of("p", 2))).containsEntry("p", new InventoryUseCase.Snapshot(
                "p", "seller", "Product", "sku", BigDecimal.TEN, false));
    }

    @Test
    void settle_lines_mapsToProductCommand() {
        adapter.settle(List.of(new InventoryUseCase.Line("p", 3)), "RELEASED");

        verify(products).settle(List.of(new ProductStockUseCase.Line("p", 3)), "RELEASED");
    }
}
