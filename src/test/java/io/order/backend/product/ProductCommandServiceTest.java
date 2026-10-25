package io.order.backend.product;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.application.usecase.ProductCommandService;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
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
    void deleteProduct_whenNotExists_shouldThrowResourceNotFoundException() {
        ProductId id = ProductId.generate();
        when(products.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteProduct(id))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(products, never()).deleteById(any(ProductId.class));
    }

    @Test
    void deleteProduct_whenReservedStock_shouldThrowIllegalStateException() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        product.setReserved(2);

        when(products.existsById(id)).thenReturn(true);
        when(products.lockById(id)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.deleteProduct(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Product has reserved stock");
        verify(products, never()).deleteById(any(ProductId.class));
    }

    @Test
    void deleteProduct_whenValid_shouldDelete() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        product.setReserved(0);

        when(products.existsById(id)).thenReturn(true);
        when(products.lockById(id)).thenReturn(Optional.of(product));

        service.deleteProduct(id);

        verify(products).deleteById(id);
    }
}
