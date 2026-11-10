package io.order.backend.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import io.order.backend.product.application.port.in.CreateProductUseCase;
import io.order.backend.product.application.port.in.ProductCommandUseCase;
import io.order.backend.product.application.port.in.ProductQueryUseCase;
import io.order.backend.product.application.port.in.UpdateProductUseCase;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import io.order.backend.product.infrastructure.web.controller.ProductController;
import io.order.backend.product.infrastructure.web.dto.ProductRequest;
import io.order.backend.product.infrastructure.web.dto.ProductResponse;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    CreateProductUseCase creates;
    @Mock
    UpdateProductUseCase updates;
    @Mock
    ProductCommandUseCase commands;
    @Mock
    ProductQueryUseCase queries;

    ProductController controller;
    Product product;
    ProductId productId;
    ProductRequest request;

    @BeforeEach
    void setUp() {
        controller = new ProductController(creates, updates, commands, queries);
        productId = ProductId.generate();
        product = new Product(productId, "Test Title", "Electronics", "Desc",
                new BigDecimal("100000"), 10, "SKU-1", "seller-1");
        request = ProductRequest.builder()
                .title("Test Title")
                .category("Electronics")
                .description("Desc")
                .price(new BigDecimal("100000"))
                .stock(10)
                .sku("SKU-1")
                .sellerId("seller-1")
                .build();
    }

    @Test
    void createProduct_mapsRequestAndReturnsResponse() {
        when(creates.createProduct(any())).thenReturn(product);

        var response = controller.createProduct(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().id()).isEqualTo(productId.toString());
        assertThat(response.getBody().getData().title()).isEqualTo("Test Title");
        verify(creates).createProduct(argThat(command ->
                command.title().equals("Test Title")
                        && command.sku().equals("SKU-1")
                        && command.price().compareTo(new BigDecimal("100000")) == 0));
    }

    @Test
    void updateProduct_usesTypedIdAndRequestCommand() {
        when(updates.updateProduct(eq(productId), any())).thenReturn(product);

        var response = controller.updateProduct(productId.toString(), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().id()).isEqualTo(productId.toString());
        verify(updates).updateProduct(eq(productId), argThat(command -> command.sku().equals("SKU-1")));
    }

    @Test
    void deleteProduct_delegatesTypedIdAndReturnsSuccess() {
        var response = controller.deleteProduct(productId.toString());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        verify(commands).deleteProduct(productId);
    }

    @Test
    void getProductById_mapsQueryResult() {
        when(queries.getProductById(productId)).thenReturn(product);

        var response = controller.getProductById(productId.toString());

        assertThat(response.getBody().getData().id()).isEqualTo(productId.toString());
        verify(queries).getProductById(productId);
    }

    @Test
    void getProductBySku_mapsQueryResult() {
        when(queries.getProductBySku("SKU-1")).thenReturn(product);

        var response = controller.getProductBySku("SKU-1");

        assertThat(response.getBody().getData().sku()).isEqualTo("SKU-1");
        verify(queries).getProductBySku("SKU-1");
    }

    @Test
    void getAllProducts_mapsQueryResults() {
        when(queries.getAllProducts()).thenReturn(List.of(product));

        var response = controller.getAllProducts();

        assertThat(response.getBody().getData()).containsExactly(ProductResponse.from(product));
        verify(queries).getAllProducts();
    }

    @Test
    void getProductsByCategory_delegatesCategory() {
        when(queries.getProductsByCategory("Electronics")).thenReturn(List.of(product));

        var response = controller.getProductsByCategory("Electronics");

        assertThat(response.getBody().getData()).containsExactly(ProductResponse.from(product));
        verify(queries).getProductsByCategory("Electronics");
    }

    @Test
    void getAllProductsPageable_mapsPageResult() {
        var pageable = PageRequest.of(0, 10);
        when(queries.getAllProducts(pageable)).thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        var response = controller.getAllProducts(pageable);

        assertThat(response.getBody().getData().getContent()).containsExactly(ProductResponse.from(product));
        verify(queries).getAllProducts(pageable);
    }

    @Test
    void updateStock_usesTypedIdAndQuantity() {
        var response = controller.updateStock(productId.toString(), 15);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        verify(updates).updateStock(productId, 15);
    }
}
