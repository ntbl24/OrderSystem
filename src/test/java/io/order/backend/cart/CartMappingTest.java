package io.order.backend.cart;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.infrastructure.persistence.CartEntity;
import io.order.backend.cart.infrastructure.persistence.CartPersistenceMapper;
import io.order.backend.cart.infrastructure.web.dto.CartResponse;
import io.order.backend.cart.infrastructure.web.dto.CreateCartItemRequest;
import io.order.backend.cart.infrastructure.web.dto.CreateCartRequest;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import jakarta.validation.Validation;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CartMappingTest {
    @Test
    void persistence_whenRoundTrip_shouldPreserveIdsItemsAndVersion() {
        Cart cart = Cart.create(UserId.generate());
        cart.addItem(ProductId.generate(), "Product", 2, BigDecimal.TEN);
        CartEntity entity = new CartEntity();
        CartPersistenceMapper.copy(cart, entity);
        entity.version = 7L;
        Cart restored = CartPersistenceMapper.domain(entity);
        assertThat(restored.getId()).isEqualTo(cart.getId());
        assertThat(restored.getUserId()).isEqualTo(cart.getUserId());
        assertThat(restored.getVersion()).isEqualTo(7L);
        assertThat(restored.getItems().getFirst().getProductId()).isEqualTo(cart.getItems().getFirst().getProductId());
        assertThat(restored.getTotal()).isEqualByComparingTo("20");
        CartResponse response = CartResponse.from(restored);
        assertThat(response.id()).isEqualTo(entity.id.toString());
        assertThat(response.items().getFirst().subTotal()).isEqualByComparingTo("20");
    }

    @Test
    void request_whenMapping_shouldIgnoreClientPriceAndValidateNestedItems() {
        String user = UserId.generate().value().toString();
        String product = ProductId.generate().value().toString();
        var item = new CreateCartItemRequest(product, "Forged", 2, BigDecimal.ONE);
        var command = new CreateCartRequest(user, List.of(item)).toCommand();
        assertThat(command.items().getFirst().quantity()).isEqualTo(2);
        assertThat(command.items().getFirst().productId()).isEqualTo(ProductId.of(product));
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new CreateCartRequest(user,
                    List.of(new CreateCartItemRequest(product, null, 2, null))))).isEmpty();
            assertThat(validator.validate(new CreateCartRequest(user,
                    List.of(new CreateCartItemRequest(product, null, 0, null))))).isNotEmpty();
        }
    }
}
