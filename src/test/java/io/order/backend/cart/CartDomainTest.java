package io.order.backend.cart;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartItem;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import java.math.BigDecimal;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CartDomainTest {
    @Test
    void mutations_whenExistingItem_shouldRecalculateTotalsAndKeepItemsImmutable() {
        Cart cart = Cart.create(UserId.generate());
        ProductId id = ProductId.generate();
        cart.addItem(id, "Product", 2, BigDecimal.TEN);
        cart.addOrUpdateItem(id, "New title", 3, new BigDecimal("20"));
        assertThat(cart.getTotal()).isEqualByComparingTo("60");
        assertThat(cart.getItems().getFirst().getSubTotal()).isEqualByComparingTo("60");
        assertThat(cart.getItems().getFirst().getProductName()).isEqualTo("New title");
        assertThatThrownBy(() -> cart.getItems().clear()).isInstanceOf(UnsupportedOperationException.class);
        cart.removeItem(id);
        assertThat(cart.getTotal()).isZero();
    }

    @Test
    void mutations_whenLimitsExceeded_shouldRejectWithoutChangingCart() {
        Cart cart = Cart.create(UserId.generate());
        ProductId id = ProductId.generate();
        cart.addItem(id, "Product", 100000, BigDecimal.ONE);
        assertThatThrownBy(() -> cart.addItem(id, "Product", 1, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cart.addOrUpdateItem(id, "Product", 0, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(100000);
        IntStream.range(1, 100).forEach(i -> cart.addItem(ProductId.generate(), "Product", 1, BigDecimal.ONE));
        assertThatThrownBy(() -> cart.addItem(ProductId.generate(), "Product", 1, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(cart.getItems()).hasSize(100);
        assertThatThrownBy(() -> new CartItem(id, "Product", 1, new BigDecimal("1.5")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
