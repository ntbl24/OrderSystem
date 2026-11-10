package com.abug.product.service;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.product.application.port.in.ProductCommand;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.application.usecase.UpdateProductService;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {
    @Mock
    ProductRepository products;

    @InjectMocks
    UpdateProductService service;

    @Test
    void updateProduct_whenNotFound_shouldThrowProductNotFound() {
        ProductId id = ProductId.generate();
        ProductCommand command = new ProductCommand("Title", "Cat", "Desc",
                new BigDecimal("100"), 10, "SKU", "seller");
        when(products.lockById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProduct(id, command))
                .isInstanceOfSatisfying(AppException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND));
        verify(products, never()).save(any());
    }

    @Test
    void updateProduct_whenValid_shouldUpdateAndSave() {
        ProductId id = ProductId.generate();
        Product product = Product.restore(id, false, "Old Title", "Category", "Description",
                new BigDecimal("100"), 5, "SKU", "seller", 0, 3L);

        ProductCommand command = new ProductCommand("New Title", "Cat", "Desc",
                new BigDecimal("150"), 10, "SKU-UPDATED", "seller");

        when(products.lockById(id)).thenReturn(Optional.of(product));
        when(products.save(product)).thenReturn(product);

        Product updated = service.updateProduct(id, command);

        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getPrice()).isEqualByComparingTo("150");
        assertThat(updated.getStock()).isEqualTo(10);
        verify(products).save(product);
    }

    @Test
    void updateStock_whenValid_shouldUpdateStockAndSave() {
        ProductId id = ProductId.generate();
        Product product = Product.restore(id, false, "Title", "Category", "Description",
                new BigDecimal("100"), 5, "SKU", "seller", 2, 3L);

        when(products.lockById(id)).thenReturn(Optional.of(product));
        when(products.save(product)).thenReturn(product);

        service.updateStock(id, 8);

        assertThat(product.getStock()).isEqualTo(8);
        verify(products).save(product);
    }

    @Test
    void updateStock_whenQuantityBelowReserved_shouldThrowException() {
        ProductId id = ProductId.generate();
        Product product = Product.restore(id, false, "Title", "Category", "Description",
                new BigDecimal("100"), 5, "SKU", "seller", 4, 3L);

        when(products.lockById(id)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.updateStock(id, 2))
                .isInstanceOf(AppException.class);
        verify(products, never()).save(any());
    }
}
