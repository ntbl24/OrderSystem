package io.order.backend.product;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.usecase.ProductStockService;
import io.order.backend.product.application.port.in.ProductStockUseCase.Line;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockServiceTest {
    @Mock ProductRepository products;
    @InjectMocks ProductStockService service;

    @Test
    void reserve_whenProductsAvailable_shouldLockSortedAndReturnSnapshots() {
        Product a = product("a");
        Product b = product("b");
        when(products.lockById("a")).thenReturn(Optional.of(a));
        when(products.lockById("b")).thenReturn(Optional.of(b));

        var snapshots = service.reserve(Map.of("b", 3, "a", 2));

        var ordered = inOrder(products);
        ordered.verify(products).lockById("a");
        ordered.verify(products).lockById("b");
        ordered.verify(products).save(a);
        ordered.verify(products).save(b);
        assertThat(a.getReserved()).isEqualTo(2);
        assertThat(b.getReserved()).isEqualTo(3);
        assertThat(snapshots.get("a").sellerId()).isEqualTo("seller");
        assertThat(snapshots.get("a").price()).isEqualByComparingTo("100");
        assertThat(snapshots.get("a").deleted()).isFalse();
        assertThatThrownBy(() -> snapshots.clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void reserve_whenDeletedEvenWithInvalidData_shouldRejectAsUnavailable() {
        Product product = new Product();
        product.setDeleted(true);
        when(products.lockById("a")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.reserve(Map.of("a", 1)))
                .isInstanceOf(IllegalStateException.class).hasMessage("Product unavailable");
        verify(products, never()).save(any());
    }

    @Test
    void reserve_whenMissing_shouldPreserveNotFoundError() {
        when(products.lockById("a")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.reserve(Map.of("a", 1)))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("Product not found");
        verify(products, never()).save(any());
    }

    @Test
    void reserve_whenStockInsufficient_shouldNotSave() {
        when(products.lockById("a")).thenReturn(Optional.of(product("a")));
        assertThatThrownBy(() -> service.reserve(Map.of("a", 11)))
                .isInstanceOf(IllegalStateException.class).hasMessage("Insufficient stock");
        verify(products, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({"CONSUMED,8,0", "RELEASED,10,0", "RETURNED,12,2"})
    void settle_whenValidTarget_shouldApplyStockMutation(String target, int stock, int reserved) {
        Product product = product("a");
        product.setReserved(2);
        when(products.lockById("a")).thenReturn(Optional.of(product));

        service.settle(List.of(new Line("a", 2)), target);

        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getReserved()).isEqualTo(reserved);
        verify(products).save(product);
    }

    @Test
    void settle_whenRepeatedProductLines_shouldLockEachIdOnceInSortedOrder() {
        Product a = product("a");
        Product b = product("b");
        a.setReserved(2);
        b.setReserved(1);
        when(products.lockById("a")).thenReturn(Optional.of(a));
        when(products.lockById("b")).thenReturn(Optional.of(b));

        service.settle(List.of(new Line("b", 1), new Line("a", 1), new Line("a", 1)), "RELEASED");

        var ordered = inOrder(products);
        ordered.verify(products).lockById("a");
        ordered.verify(products).lockById("b");
        verify(products, times(1)).lockById("a");
        assertThat(a.getReserved()).isZero();
        assertThat(b.getReserved()).isZero();
    }

    private Product product(String id) {
        Product product = new Product();
        product.setId(java.util.UUID.nameUUIDFromBytes(id.getBytes()).toString());
        product.setSellerId("seller");
        product.setTitle("Product " + id);
        product.setSku("SKU-" + id);
        product.setPrice(new BigDecimal("100"));
        product.setStock(10);
        return product;
    }
}
