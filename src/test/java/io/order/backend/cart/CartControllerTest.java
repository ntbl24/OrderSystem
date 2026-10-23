package io.order.backend.cart;

import io.order.backend.cart.application.port.in.*;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.infrastructure.web.controller.CartController;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {
    @Mock CartCommandUseCase carts;
    @Mock CartQueryUseCase queries;
    @Mock CreateCartUseCase creates;
    @Mock UpdateCartUseCase updates;
    MockMvc mvc;
    Cart cart;
    ProductId product;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CartController(carts, creates, updates, queries)).build();
        cart = Cart.create(UserId.generate());
        product = ProductId.generate();
        cart.addItem(product, "Server title", 2, BigDecimal.TEN);
    }

    @Test
    void create_whenValid_shouldMapCommandAndReturnCompleteResponse() throws Exception {
        when(creates.createCart(any())).thenReturn(cart);
        mvc.perform(post("/api/v1/cart").contentType(MediaType.APPLICATION_JSON).content(
                "{\"userId\":\"" + cart.getUserId().value() + "\",\"items\":[{\"productId\":\""
                        + product.value() + "\",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(cart.getId().value().toString()))
                .andExpect(jsonPath("$.data.userId").value(cart.getUserId().value().toString()))
                .andExpect(jsonPath("$.data.items[0].productName").value("Server title"))
                .andExpect(jsonPath("$.data.total").value(20));
        verify(creates).createCart(argThat(c -> c.userId().equals(cart.getUserId())
                && c.items().getFirst().productId().equals(product) && c.items().getFirst().quantity() == 2));
    }

    @Test
    void create_whenNestedQuantityInvalid_shouldReturn400WithoutCallingUseCase() throws Exception {
        mvc.perform(post("/api/v1/cart").contentType(MediaType.APPLICATION_JSON).content(
                "{\"userId\":\"" + cart.getUserId().value() + "\",\"items\":[{\"productId\":\""
                        + product.value() + "\",\"quantity\":0}]}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(creates);
    }

    @Test
    void queriesAndMutations_whenValid_shouldKeepRoutesAndResponseEnvelope() throws Exception {
        String id = cart.getId().value().toString();
        String user = cart.getUserId().value().toString();
        String productId = product.value().toString();
        when(queries.getCartById(cart.getId())).thenReturn(cart);
        when(queries.getCartByUserId(cart.getUserId())).thenReturn(Optional.of(cart));
        when(updates.updateCart(eq(cart.getId()), any())).thenReturn(cart);
        when(updates.addItemToCart(eq(cart.getUserId()), any())).thenReturn(cart);
        when(updates.updateItemQuantity(cart.getUserId(), product, 3)).thenReturn(cart);
        when(updates.removeItemFromCart(cart.getUserId(), product)).thenReturn(cart);
        mvc.perform(get("/api/v1/cart/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(20));
        mvc.perform(get("/api/v1/cart/user/{userId}", user)).andExpect(status().isOk());
        mvc.perform(put("/api/v1/cart/{id}", id).contentType(MediaType.APPLICATION_JSON).content("{\"items\":[]}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/cart/user/{userId}/items", user).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":\"" + productId + "\",\"quantity\":2}"))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/cart/user/{userId}/items/{productId}", user, productId).param("quantity", "3"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/cart/user/{userId}/items/{productId}", user, productId)).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/cart/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        mvc.perform(delete("/api/v1/cart/user/{userId}", user)).andExpect(status().isOk());
        verify(carts).deleteCart(cart.getId());
        verify(carts).clearCart(cart.getUserId());
    }
}
