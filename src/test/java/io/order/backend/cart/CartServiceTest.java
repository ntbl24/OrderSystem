package io.order.backend.cart;

import io.order.backend.cart.application.CartService;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.application.port.out.ProductCatalogPort;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.dto.CartDTO;
import io.order.backend.cart.dto.CartItemDTO;
import io.order.backend.cart.mapper.CartMapper;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock CartRepository carts;
    @Mock CartMapper mapper;
    @Mock CurrentBuyerPort buyer;
    @Mock ProductCatalogPort products;
    @InjectMocks CartService service;

    @Test
    void addItemToCart_whenAvailable_shouldPriceUsingCartOwnedCatalogSnapshot() {
        when(buyer.buyerId()).thenReturn("buyer");
        when(carts.findByUserId("buyer")).thenReturn(Optional.empty());
        when(products.findById("p")).thenReturn(Optional.of(
                new ProductCatalogPort.Snapshot("p", "Catalog title", new BigDecimal("100"))));
        when(carts.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CartDTO expected = new CartDTO();
        when(mapper.toDto(any(Cart.class))).thenReturn(expected);
        CartItemDTO item = new CartItemDTO();
        item.setProductId("p");
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.ONE);

        assertThat(service.addItemToCart("buyer", item)).isSameAs(expected);

        var saved = ArgumentCaptor.forClass(Cart.class);
        verify(carts).save(saved.capture());
        assertThat(saved.getValue().getTotal()).isEqualByComparingTo("200");
        assertThat(saved.getValue().getItems()).singleElement().satisfies(line -> {
            assertThat(line.getProductName()).isEqualTo("Catalog title");
            assertThat(line.getUnitPrice()).isEqualByComparingTo("100");
            assertThat(line.getQuantity()).isEqualTo(2);
        });
    }

    @Test
    void addItemToCart_whenCatalogMissing_shouldPreserveNotFoundError() {
        when(buyer.buyerId()).thenReturn("buyer");
        when(carts.findByUserId("buyer")).thenReturn(Optional.empty());
        when(products.findById("p")).thenReturn(Optional.empty());
        CartItemDTO item = new CartItemDTO();
        item.setProductId("p");
        item.setQuantity(1);

        assertThatThrownBy(() -> service.addItemToCart("buyer", item))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("Product not found");
        verify(carts, never()).save(any());
    }
}
