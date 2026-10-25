package io.order.backend.product;

import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProductDomainTest {

    @Test
    void create_whenValidParameters_shouldInitializeAndValidate() {
        Product product = Product.create("Laptop", "Electronics", "High-end laptop",
                new BigDecimal("15000000"), 10, "SKU-LAP-1", "seller-1");

        assertThat(product.getId()).isNotNull();
        assertThat(product.getTitle()).isEqualTo("Laptop");
        assertThat(product.getPrice()).isEqualByComparingTo("15000000");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getReserved()).isZero();
    }

    @Test
    void validate_whenInvalidValues_shouldThrowIllegalArgumentException() {
        Product product = new Product();
        product.setPrice(new BigDecimal("-100"));
        assertThatThrownBy(product::validate).isInstanceOf(IllegalArgumentException.class);

        product.setPrice(new BigDecimal("100.50")); // fractional VND
        product.setSellerId("seller");
        assertThatThrownBy(product::validate).isInstanceOf(IllegalArgumentException.class);

        product.setPrice(new BigDecimal("100"));
        product.setStock(5);
        product.setReserved(6); // reserved > stock
        assertThatThrownBy(product::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void inventoryOperations_reserveReleaseConsumeRestock_shouldUpdateCorrectly() {
        Product product = Product.create("Phone", "Electronics", "Smartphone",
                new BigDecimal("5000000"), 10, "SKU-PH-1", "seller-1");

        product.reserve(3);
        assertThat(product.getReserved()).isEqualTo(3);
        assertThat(product.getStock()).isEqualTo(10);

        assertThatThrownBy(() -> product.reserve(8)).isInstanceOf(IllegalStateException.class);

        product.release(1);
        assertThat(product.getReserved()).isEqualTo(2);

        product.consume(2);
        assertThat(product.getReserved()).isZero();
        assertThat(product.getStock()).isEqualTo(8);

        product.restock(5);
        assertThat(product.getStock()).isEqualTo(13);
    }

    @Test
    void replaceDetails_whenValid_shouldUpdateFields() {
        Product product = Product.create("Old", "Cat1", "Desc1",
                new BigDecimal("100000"), 5, "SKU-1", "seller-1");

        product.replaceDetails("New", "Cat2", "Desc2", new BigDecimal("200000"), 8, "SKU-2", "seller-1");

        assertThat(product.getTitle()).isEqualTo("New");
        assertThat(product.getCategory()).isEqualTo("Cat2");
        assertThat(product.getPrice()).isEqualByComparingTo("200000");
        assertThat(product.getStock()).isEqualTo(8);
    }

    @Test
    void replaceDetails_whenReservedAndSellerChanged_shouldThrowIllegalStateException() {
        Product product = Product.create("Item", "Cat", "Desc",
                new BigDecimal("100000"), 5, "SKU-1", "seller-1");
        product.reserve(2);

        assertThatThrownBy(() -> product.replaceDetails("Item", "Cat", "Desc",
                new BigDecimal("100000"), 5, "SKU-1", "seller-2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot change seller with reserved stock");
    }

    @Test
    void updateStock_whenLessThanReserved_shouldThrowIllegalArgumentException() {
        Product product = Product.create("Item", "Cat", "Desc",
                new BigDecimal("100000"), 5, "SKU-1", "seller-1");
        product.reserve(3);

        assertThatThrownBy(() -> product.updateStock(2))
                .isInstanceOf(IllegalArgumentException.class);

        product.updateStock(4);
        assertThat(product.getStock()).isEqualTo(4);
    }
}
