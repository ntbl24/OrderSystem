package io.order.backend.product;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.in.UpdateProductCommand;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.application.usecase.UpdateProductService;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
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
    void updateProduct_whenNotFound_shouldThrowResourceNotFoundException() {
        ProductId id = ProductId.generate();
        UpdateProductCommand command = new UpdateProductCommand("Title", "Cat", "Desc",
                new BigDecimal("100"), 10, "SKU", "seller");
        when(products.lockById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProduct(id, command))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(products, never()).save(any());
    }

    @Test
    void updateProduct_whenValid_shouldUpdateAndSave() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        product.setTitle("Old Title");
        product.setSellerId("seller");
        product.setPrice(new BigDecimal("100"));
        product.setStock(5);

        UpdateProductCommand command = new UpdateProductCommand("New Title", "Cat", "Desc",
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
        Product product = new Product();
        product.setId(id);
        product.setPrice(new BigDecimal("100"));
        product.setSellerId("seller");
        product.setStock(5);
        product.setReserved(2);

        when(products.lockById(id)).thenReturn(Optional.of(product));
        when(products.save(product)).thenReturn(product);

        service.updateStock(id, 8);

        assertThat(product.getStock()).isEqualTo(8);
        verify(products).save(product);
    }

    @Test
    void updateStock_whenQuantityBelowReserved_shouldThrowException() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        product.setPrice(new BigDecimal("100"));
        product.setSellerId("seller");
        product.setStock(5);
        product.setReserved(4);

        when(products.lockById(id)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.updateStock(id, 2))
                .isInstanceOf(IllegalArgumentException.class);
        verify(products, never()).save(any());
    }
}
