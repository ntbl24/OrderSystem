package io.order.backend.product;

import io.order.backend.product.application.port.in.CreateProductUseCase;
import io.order.backend.product.application.port.in.ProductCommandUseCase;
import io.order.backend.product.application.port.in.ProductQueryUseCase;
import io.order.backend.product.application.port.in.UpdateProductUseCase;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import io.order.backend.product.infrastructure.web.controller.ProductController;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    MockMvc mvc;
    Product product;
    ProductId productId;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ProductController(creates, updates, commands, queries)).build();
        productId = ProductId.generate();
        product = new Product();
        product.setId(productId);
        product.setTitle("Test Title");
        product.setCategory("Electronics");
        product.setDescription("Desc");
        product.setPrice(new BigDecimal("100000"));
        product.setStock(10);
        product.setSku("SKU-1");
        product.setSellerId("seller-1");
    }

    @Test
    void createProduct_whenValid_shouldReturnCreatedProductResponse() throws Exception {
        when(creates.createProduct(any())).thenReturn(product);

        String json = """
                {
                    "title": "Test Title",
                    "category": "Electronics",
                    "description": "Desc",
                    "price": 100000,
                    "stock": 10,
                    "sku": "SKU-1",
                    "sellerId": "seller-1"
                }
                """;

        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId.value().toString()))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.price").value(100000));

        verify(creates).createProduct(any());
    }

    @Test
    void updateProduct_whenValid_shouldReturnUpdatedResponse() throws Exception {
        when(updates.updateProduct(eq(productId), any())).thenReturn(product);

        String json = """
                {
                    "title": "Test Title",
                    "category": "Electronics",
                    "description": "Desc",
                    "price": 100000,
                    "stock": 10,
                    "sku": "SKU-1",
                    "sellerId": "seller-1"
                }
                """;

        mvc.perform(put("/api/v1/products/{id}", productId.value().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId.value().toString()));

        verify(updates).updateProduct(eq(productId), any());
    }

    @Test
    void deleteProduct_shouldInvokeCommandUseCase() throws Exception {
        mvc.perform(delete("/api/v1/products/{id}", productId.value().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(commands).deleteProduct(productId);
    }

    @Test
    void getProductById_shouldReturnProductResponse() throws Exception {
        when(queries.getProductById(productId)).thenReturn(product);

        mvc.perform(get("/api/v1/products/{id}", productId.value().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId.value().toString()))
                .andExpect(jsonPath("$.data.title").value("Test Title"));

        verify(queries).getProductById(productId);
    }

    @Test
    void getProductBySku_shouldReturnProductResponse() throws Exception {
        when(queries.getProductBySku("SKU-1")).thenReturn(product);

        mvc.perform(get("/api/v1/products/sku/{sku}", "SKU-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sku").value("SKU-1"));

        verify(queries).getProductBySku("SKU-1");
    }

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        when(queries.getAllProducts()).thenReturn(List.of(product));

        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(productId.value().toString()));

        verify(queries).getAllProducts();
    }

    @Test
    void updateStock_shouldInvokeUpdateStockUseCase() throws Exception {
        mvc.perform(put("/api/v1/products/{id}/stock", productId.value().toString())
                .param("quantity", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(updates).updateStock(productId, 15);
    }
}
