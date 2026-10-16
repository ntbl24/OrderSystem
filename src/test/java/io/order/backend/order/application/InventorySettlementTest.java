package io.order.backend.order.application;

import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventorySettlementTest {
    @Mock InventoryPort inventory;

    private Order held() {
        var order = new Order();
        order.setReservationStatus("HELD");
        var item = new OrderItem();
        item.setProductId("product");
        item.setQuantity(2);
        order.setItems(List.of(item));
        return order;
    }

    @Test
    void settle_whenHeld_mapsLinesAndUpdatesReservation() {
        var order = held();
        InventorySettlement.settle(order, inventory, "CONSUMED");
        verify(inventory).settle(List.of(new InventoryPort.Line("product", 2)), "CONSUMED");
        assertThat(order.getReservationStatus()).isEqualTo("CONSUMED");
    }

    @Test
    void settle_whenRepeated_doesNotCallInventory() {
        var order = held();
        InventorySettlement.settle(order, inventory, "HELD");
        verifyNoInteractions(inventory);
    }

    @Test
    void settle_whenInvalid_rejectsBeforeCallingInventory() {
        var order = held();
        assertThatThrownBy(() -> InventorySettlement.settle(order, inventory, "RETURNED"))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(inventory);
        assertThat(order.getReservationStatus()).isEqualTo("HELD");
    }

    @Test
    void settle_whenInventoryFails_preservesReservation() {
        var order = held();
        doThrow(new IllegalStateException("stock failure")).when(inventory).settle(anyList(), eq("RELEASED"));
        assertThatThrownBy(() -> InventorySettlement.settle(order, inventory, "RELEASED"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(order.getReservationStatus()).isEqualTo("HELD");
    }

    @Test
    void settle_whenConsumed_returnsOnce() {
        var order = held();
        order.setReservationStatus("CONSUMED");
        InventorySettlement.settle(order, inventory, "RETURNED");
        InventorySettlement.settle(order, inventory, "RETURNED");
        verify(inventory).settle(List.of(new InventoryPort.Line("product", 2)), "RETURNED");
        assertThat(order.getReservationStatus()).isEqualTo("RETURNED");
    }
}
