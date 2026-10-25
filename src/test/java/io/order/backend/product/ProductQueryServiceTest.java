package io.order.backend.product;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.application.usecase.ProductQueryService;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {
    @Mock
    ProductRepository products;

    @InjectMocks
    ProductQueryService service;

    @Test
    void getProductById_whenFound_shouldReturnProduct() {
        ProductId id = ProductId.generate();
        Product product = new Product();
        product.setId(id);
        when(products.findById(id)).thenReturn(Optional.of(product));

        Product result = service.getProductById(id);

        assertThat(result).isSameAs(product);
    }

    @Test
    void getProductById_whenNotFound_shouldThrowResourceNotFoundException() {
        ProductId id = ProductId.generate();
        when(products.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProductById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getProductBySku_whenFound_shouldReturnProduct() {
        Product product = new Product();
        product.setSku("SKU-100");
        when(products.findBySku("SKU-100")).thenReturn(Optional.of(product));

        Product result = service.getProductBySku("SKU-100");

        assertThat(result).isSameAs(product);
    }

    @Test
    void getAllProducts_shouldDelegateToRepository() {
        Product product = new Product();
        when(products.findAll()).thenReturn(List.of(product));

        List<Product> result = service.getAllProducts();

        assertThat(result).containsExactly(product);
    }

    @Test
    void getAllProductsPageable_shouldDelegateToRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(new Product()));
        when(products.findAll(pageRequest)).thenReturn(page);

        Page<Product> result = service.getAllProducts(pageRequest);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getProductsByCategory_shouldDelegateToRepository() {
        Product product = new Product();
        product.setCategory("Books");
        when(products.findByCategory("Books")).thenReturn(List.of(product));

        List<Product> result = service.getProductsByCategory("Books");

        assertThat(result).containsExactly(product);
    }
}
