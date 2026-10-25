package io.order.backend.product;

import io.order.backend.product.application.port.in.CreateProductCommand;
import io.order.backend.product.application.port.in.ProductCommand;
import io.order.backend.product.application.port.in.UpdateProductCommand;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.application.usecase.ProductService;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import io.order.backend.product.infrastructure.web.dto.ProductRequest;
import io.order.backend.product.infrastructure.web.dto.ProductResponse;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    ProductRepository products;

    ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(products);
    }

    @Test
    void createProduct_whenValidRequest_shouldMapCommandAndResponseWithoutTrustingRequestId() {
        ProductRequest request = new ProductRequest();
        request.setId(ProductId.generate().value().toString());
        request.setTitle("Title");
        request.setCategory("Category");
        request.setDescription("Description");
        request.setPrice(new BigDecimal("100"));
        request.setStock(10);
        request.setSku("SKU");
        request.setSellerId("seller");
        ProductId generated = ProductId.generate();
        when(products.save(any())).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            assertThat(product.getId()).isNull();
            product.setId(generated);
            return product;
        });

        ProductResponse response = ProductResponse.from(service.createProduct(request.toCreateCommand()));

        assertThat(response.id()).isEqualTo(generated.value().toString());
        assertThat(response.title()).isEqualTo("Title");
        assertThat(response.sellerId()).isEqualTo("seller");
        assertThat(response.stock()).isEqualTo(10);
        assertThat(response.price()).isEqualByComparingTo("100");
    }

    @Test
    void updateProduct_whenReserved_shouldPreserveIdentityVersionAndReservation() {
        Product original = existing();
        when(products.lockById(original.getId())).thenReturn(Optional.of(original));
        when(products.save(original)).thenReturn(original);

        Product updated = service.updateProduct(original.getId(), updateCommand("seller", 12));

        assertThat(updated).isSameAs(original);
        assertThat(updated.getVersion()).isEqualTo(7L);
        assertThat(updated.getReserved()).isEqualTo(3);
        assertThat(updated.getStock()).isEqualTo(12);
        assertThat(updated.getTitle()).isEqualTo("Updated");
    }

    @Test
    void updateProduct_whenReservedAndSellerChanges_shouldRejectWithoutSaving() {
        Product original = existing();
        ProductId id = original.getId();
        when(products.lockById(id)).thenReturn(Optional.of(original));

        assertThatThrownBy(() -> service.updateProduct(id, updateCommand("other", 12)))
                .isInstanceOf(IllegalStateException.class).hasMessage("Cannot change seller with reserved stock");
        assertThat(original.getSellerId()).isEqualTo("seller");
        verify(products, never()).save(any());
    }

    @Test
    void updateStock_whenBelowReserved_shouldRejectAndKeepStockUnchanged() {
        Product original = existing();
        ProductId id = original.getId();
        when(products.lockById(id)).thenReturn(Optional.of(original));

        assertThatThrownBy(() -> service.updateStock(id, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThat(original.getStock()).isEqualTo(10);
        verify(products, never()).save(any());
    }

    @Test
    void deleteProduct_whenReserved_shouldNotDelete() {
        Product original = existing();
        ProductId id = original.getId();
        when(products.existsById(id)).thenReturn(true);
        when(products.lockById(id)).thenReturn(Optional.of(original));

        assertThatThrownBy(() -> service.deleteProduct(id)).isInstanceOf(IllegalStateException.class);
        verify(products, never()).deleteById(any(ProductId.class));
    }

    private Product existing() {
        Product product = new Product();
        product.setId(ProductId.generate());
        product.setSellerId("seller");
        product.setPrice(new BigDecimal("100"));
        product.setStock(10);
        product.setReserved(3);
        product.setVersion(7L);
        return product;
    }

    private UpdateProductCommand updateCommand(String seller, int stock) {
        return new UpdateProductCommand("Updated", "Category", "Description", new BigDecimal("200"), stock, "SKU", seller);
    }
}
