package io.order.backend.checkout;

import io.order.backend.checkout.application.CheckoutService;
import io.order.backend.checkout.application.port.out.InventoryPort;
import io.order.backend.checkout.application.port.out.PlaceOrderPort;
import io.order.backend.checkout.dto.CheckoutRequest;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.order.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {
    @Mock CurrentBuyerPort buyer;
    @Mock IdempotencyPort idempotency;
    @Mock InventoryPort inventory;
    @Mock PlaceOrderPort orders;
    @InjectMocks CheckoutService checkout;

    @BeforeEach
    void executeAction() {
        when(buyer.buyerId()).thenReturn("buyer");
        when(idempotency.execute(eq("buyer"), eq("CHECKOUT"), eq("key"), any(), eq(OrderDTO.class), any()))
                .thenAnswer(call -> ((Supplier<?>) call.getArgument(5)).get());
    }

    @Test
    void checkout_duplicateItems_aggregatesReservationAndUsesSnapshotPrice() {
        var request = new CheckoutRequest(List.of(new CheckoutRequest.Item("p", 1), new CheckoutRequest.Item("p", 2)), "address", "notes");
        when(inventory.reserve(Map.of("p", 3))).thenReturn(Map.of("p",
                new InventoryPort.ProductSnapshot("p", "seller", "Product", "sku", new BigDecimal("100000"))));
        var expected = new OrderDTO();
        when(orders.place(any())).thenReturn(expected);

        assertThat(checkout.checkout("key", request)).isSameAs(expected);

        var command = ArgumentCaptor.forClass(PlaceOrderPort.Command.class);
        verify(orders).place(command.capture());
        assertThat(command.getValue().buyerId()).isEqualTo("buyer");
        assertThat(command.getValue().subtotal()).isEqualByComparingTo("300000");
        assertThat(command.getValue().lines()).singleElement().satisfies(line -> {
            assertThat(line.quantity()).isEqualTo(3);
            assertThat(line.productName()).isEqualTo("Product");
        });
    }

    @Test
    void checkout_mixedSellers_doesNotPlaceOrder() {
        var request = new CheckoutRequest(List.of(new CheckoutRequest.Item("a", 1), new CheckoutRequest.Item("b", 1)), "address", null);
        when(inventory.reserve(any())).thenReturn(Map.of(
                "a", new InventoryPort.ProductSnapshot("a", "one", "A", "a", BigDecimal.TEN),
                "b", new InventoryPort.ProductSnapshot("b", "two", "B", "b", BigDecimal.TEN)));

        assertThatThrownBy(() -> checkout.checkout("key", request)).isInstanceOf(IllegalStateException.class)
                .hasMessage("Checkout requires one seller");
        verifyNoInteractions(orders);
    }
}
