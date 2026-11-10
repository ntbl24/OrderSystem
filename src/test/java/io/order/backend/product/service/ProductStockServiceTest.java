package io.order.backend.product.service;

import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import io.order.backend.product.application.usecase.ProductStockService;
import io.order.backend.product.application.port.in.ProductStockUseCase.Line;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
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
        Product product = Product.restore(ProductId.of("a"), true, "Product a", "Category", "Description",
                new BigDecimal("100"), 10, "SKU-a", "seller", 0, null);
        when(products.lockById("a")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.reserve(Map.of("a", 1)))
                .isInstanceOf(AppException.class).hasMessage("Product not found");
        verify(products, never()).save(any());
    }

    @Test
    void reserve_whenMissing_shouldPreserveNotFoundError() {
        when(products.lockById("a")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.reserve(Map.of("a", 1)))
                .isInstanceOfSatisfying(AppException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
                    assertThat(exception).hasMessage("Product not found");
                });
        verify(products, never()).save(any());
    }

    @Test
    void reserve_whenStockInsufficient_shouldNotSave() {
        when(products.lockById("a")).thenReturn(Optional.of(product("a")));
        assertThatThrownBy(() -> service.reserve(Map.of("a", 11)))
                .isInstanceOf(AppException.class).hasMessage("Insufficient stock");
        verify(products, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({"CONSUMED,8,0", "RELEASED,10,0", "RETURNED,12,2"})
    void settle_whenValidTarget_shouldApplyStockMutation(String target, int stock, int reserved) {
        Product product = product("a");
        product.reserve(2);
        when(products.lockById(ProductId.of("a").toString())).thenReturn(Optional.of(product));

        service.settle(List.of(new Line(ProductId.of("a"), 2)), target);

        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getReserved()).isEqualTo(reserved);
        verify(products).save(product);
    }

    @Test
    void settle_whenRepeatedProductLines_shouldLockEachIdOnceInSortedOrder() {
        Product a = product("a");
        Product b = product("b");
        a.reserve(2);
        b.reserve(1);
        when(products.lockById(ProductId.of("a").toString())).thenReturn(Optional.of(a));
        when(products.lockById(ProductId.of("b").toString())).thenReturn(Optional.of(b));

        service.settle(List.of(new Line(ProductId.of("b"), 1), new Line(ProductId.of("a"), 1),
                new Line(ProductId.of("a"), 1)), "RELEASED");

        var ordered = inOrder(products);
        ordered.verify(products).lockById(ProductId.of("a").toString());
        ordered.verify(products).lockById(ProductId.of("b").toString());
        verify(products, times(1)).lockById(ProductId.of("a").toString());
        assertThat(a.getReserved()).isZero();
        assertThat(b.getReserved()).isZero();
    }

    private Product product(String id) {
        return Product.restore(ProductId.of(id), false, "Product " + id, "Category", "Description",
                new BigDecimal("100"), 10, "SKU-" + id, "seller", 0, null);
    }
}
