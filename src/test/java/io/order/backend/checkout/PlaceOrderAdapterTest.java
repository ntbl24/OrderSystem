package io.order.backend.checkout;

import io.order.backend.checkout.application.port.out.PlaceOrderPort;
import io.order.backend.checkout.infrastructure.local.PlaceOrderAdapter;
import io.order.backend.order.application.port.in.PlaceOrderUseCase;
import io.order.backend.order.application.port.in.OrderResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceOrderAdapterTest {
    @Mock PlaceOrderUseCase orders;
    @InjectMocks PlaceOrderAdapter adapter;

    @Test
    void place_snapshot_mapsToOrderOwnedCommand() {
        var expected = new OrderResult();
        when(orders.placeOrder(any())).thenReturn(expected);
        var line = new PlaceOrderPort.Line("p", "Product", "sku", 2, BigDecimal.TEN, new BigDecimal("20"));

        assertThat(adapter.place(new PlaceOrderPort.Command("buyer", "seller", List.of(line),
                new BigDecimal("20"), "address", "notes"))).isSameAs(expected);

        var command = ArgumentCaptor.forClass(PlaceOrderUseCase.CreateOrder.class);
        verify(orders).placeOrder(command.capture());
        assertThat(command.getValue().ownerId()).isEqualTo("buyer");
        assertThat(command.getValue().shippingAddress()).isEqualTo("address");
        assertThat(command.getValue().notes()).isEqualTo("notes");
        assertThat(command.getValue().lines()).containsExactly(new PlaceOrderUseCase.Line(
                "p", "Product", "sku", 2, BigDecimal.TEN, new BigDecimal("20")));
    }
}
