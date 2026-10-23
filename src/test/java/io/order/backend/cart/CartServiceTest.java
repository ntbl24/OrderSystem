package io.order.backend.cart;

import io.order.backend.cart.application.port.in.*;
import io.order.backend.cart.application.port.out.*;
import io.order.backend.cart.application.usecase.*;
import io.order.backend.cart.domain.*;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock CartRepository carts;
    @Mock CurrentBuyerPort buyer;
    @Mock ProductCatalogPort products;
    final UserId user = UserId.generate();
    final ProductId product = ProductId.generate();
    CartAccessService access;
    UpdateCartService updates;
    CreateCartService creates;
    CartQueryService queries;
    CartCommandService commands;

    @BeforeEach
    void setUp() {
        access = new CartAccessService(buyer, carts);
        var pricing = new CartPricingService(products);
        updates = new UpdateCartService(access, carts, pricing);
        creates = new CreateCartService(access, carts, pricing);
        queries = new CartQueryService(carts, access);
        commands = new CartCommandService(carts, access);
    }

    void owner() { when(buyer.buyerId()).thenReturn(user.value().toString()); }
    void catalog() {
        when(products.findById(product)).thenReturn(Optional.of(
                new ProductCatalogPort.Snapshot(product, "Catalog title", new BigDecimal("100"))));
    }
    void saves() { when(carts.save(any())).thenAnswer(i -> i.getArgument(0)); }

    @Test
    void createCart_whenAvailable_shouldUseServerPriceAndRejectDuplicate() {
        owner(); catalog(); saves();
        when(carts.findByUserId(user)).thenReturn(Optional.empty());
        var command = new CreateCartCommand(user, List.of(new CartItemData(product, 2)));
        Cart cart = creates.createCart(command);
        assertThat(cart.getTotal()).isEqualByComparingTo("200");
        assertThat(cart.getShippingCost()).isZero();
        assertThat(cart.getItems().getFirst().getProductName()).isEqualTo("Catalog title");
        when(carts.findByUserId(user)).thenReturn(Optional.of(cart));
        assertThatThrownBy(() -> creates.createCart(command)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateCart_whenMerging_shouldPreserveOtherItemsAndRefreshPrices() {
        owner(); catalog(); saves();
        Cart cart = Cart.create(user);
        ProductId other = ProductId.generate();
        cart.addItem(product, "Old", 2, BigDecimal.ONE);
        cart.addItem(other, "Other", 1, BigDecimal.TEN);
        when(carts.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(products.findById(other)).thenReturn(Optional.of(
                new ProductCatalogPort.Snapshot(other, "Other refreshed", new BigDecimal("20"))));
        Cart result = updates.updateCart(cart.getId(), new UpdateCartCommand(List.of(new CartItemData(product, 3))));
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().getFirst().getQuantity()).isEqualTo(3);
        assertThat(result.getTotal()).isEqualByComparingTo("320");
    }

    @Test
    void addItemToCart_whenExisting_shouldAccumulateQuantity() {
        owner(); catalog(); saves();
        Cart cart = Cart.create(user);
        cart.addItem(product, "Old", 2, BigDecimal.ONE);
        when(carts.findByUserId(user)).thenReturn(Optional.of(cart));
        assertThat(updates.addItemToCart(user, new CartItemData(product, 3)).getTotal()).isEqualByComparingTo("500");
    }

    @Test
    void updateItemQuantity_whenZero_shouldRemoveWithoutCatalogLookup() {
        owner(); saves();
        Cart cart = Cart.create(user);
        cart.addItem(product, "Old", 2, BigDecimal.ONE);
        when(carts.findByUserId(user)).thenReturn(Optional.of(cart));
        assertThat(updates.updateItemQuantity(user, product, 0).getItems()).isEmpty();
        assertThat(cart.getTotal()).isZero();
        verifyNoInteractions(products);
    }

    @Test
    void addItemToCart_whenCatalogMissing_shouldNotSave() {
        owner();
        when(carts.findByUserId(user)).thenReturn(Optional.empty());
        when(products.findById(product)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updates.addItemToCart(user, new CartItemData(product, 2)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(carts, never()).save(any());
    }

    @Test
    void operations_whenForeignOwner_shouldHideCartAndNeverWrite() {
        when(buyer.buyerId()).thenReturn(UserId.generate().value().toString());
        Cart cart = Cart.create(user);
        when(carts.findById(cart.getId())).thenReturn(Optional.of(cart));
        assertThatThrownBy(() -> queries.getCartById(cart.getId())).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> commands.deleteCart(cart.getId())).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> commands.clearCart(user)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> updates.addItemToCart(user, new CartItemData(product, 1)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(carts, never()).save(any());
        verify(carts, never()).deleteById(any());
        verify(carts, never()).deleteByUserId(any());
    }

    @Test
    void queriesAndDeletes_whenOwned_shouldUseTypedIdentifiers() {
        owner();
        Cart cart = Cart.create(user);
        when(carts.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(carts.findByUserId(user)).thenReturn(Optional.empty());
        assertThat(queries.getCartById(cart.getId())).isSameAs(cart);
        assertThat(queries.getCartByUserId(user)).isEmpty();
        commands.deleteCart(cart.getId());
        commands.clearCart(user);
        verify(carts).deleteById(cart.getId());
        verify(carts).deleteByUserId(user);
    }
}
