package com.abug.product.service;

import com.abug.common.exception.AppException;
import com.abug.product.application.port.in.command.ProductCommand;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.application.usecase.CreateProductService;
import com.abug.product.domain.Product;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {
    @Mock
    ProductRepository products;

    @InjectMocks
    CreateProductService service;


    @Test
    void createProduct_whenSkuExists_shouldThrowException() {
        ProductCommand command = new ProductCommand(
            "Title", "Cat", "Desc", new BigDecimal("100"), 10, "SKU-EXISTS", "seller"
        );
        when(products.existsBySku("SKU-EXISTS")).thenReturn(true);

        assertThatThrownBy(() -> service.createProduct(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product with SKU SKU-EXISTS already exists");

        verify(products, never()).save(any());
    }

    @Test
    void createProduct_whenValid_shouldSaveAndReturnProduct() {
        ProductCommand command = new ProductCommand(
            "Title", "Cat", "Desc", new BigDecimal("100"), 10, "SKU-NEW", "seller"
        );
        when(products.existsBySku("SKU-NEW")).thenReturn(false);
        when(products.save(any())).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            return p;
        });

        Product created = service.createProduct(command);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Title");
        assertThat(created.getSku()).isEqualTo("SKU-NEW");
        verify(products).save(any(Product.class));
    }
}
