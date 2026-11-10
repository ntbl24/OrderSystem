package com.abug.product.service;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.application.usecase.ProductCommandService;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {
    @Mock
    ProductRepository products;

    @InjectMocks
    ProductCommandService service;

    @Test
    void deleteProduct_whenNotExists_shouldThrowProductNotFound() {
        ProductId id = ProductId.generate();
        when(products.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteProduct(id))
                .isInstanceOfSatisfying(AppException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND));
        verify(products, never()).deleteById(any(ProductId.class));
    }

    @Test
    void deleteProduct_whenReservedStock_shouldThrowIllegalStateException() {
        ProductId id = ProductId.generate();
        Product product = Product.restore(id, false, "Product", "Category", "Description",
                java.math.BigDecimal.TEN, 10, "SKU", "seller", 2, null);

        when(products.existsById(id)).thenReturn(true);
        when(products.lockById(id)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.deleteProduct(id))
                .isInstanceOfSatisfying(AppException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                    assertThat(exception).hasMessage("Product has reserved stock");
                });
        verify(products, never()).deleteById(any(ProductId.class));
    }

    @Test
    void deleteProduct_whenValid_shouldDelete() {
        ProductId id = ProductId.generate();
        Product product = Product.restore(id, false, "Product", "Category", "Description",
                java.math.BigDecimal.TEN, 10, "SKU", "seller", 0, null);

        when(products.existsById(id)).thenReturn(true);
        when(products.lockById(id)).thenReturn(Optional.of(product));

        service.deleteProduct(id);

        verify(products).deleteById(id);
    }
}
