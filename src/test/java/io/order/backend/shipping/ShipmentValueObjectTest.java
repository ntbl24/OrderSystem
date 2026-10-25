package io.order.backend.shipping;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;
import io.order.backend.user.domain.UserId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ShipmentValueObjectTest {

    @Test
    void shipmentId_generate_shouldProduceValidUniqueId() {
        ShipmentId id1 = ShipmentId.generate();
        ShipmentId id2 = ShipmentId.generate();

        assertThat(id1).isNotNull();
        assertThat(id1.value()).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.toString()).isEqualTo(id1.value().toString());
    }

    @Test
    void shipmentId_of_withUuidString_shouldParseCorrectly() {
        UUID uuid = UUID.randomUUID();
        ShipmentId id = ShipmentId.of(uuid.toString());

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void shipmentId_of_withNonUuidString_shouldDeriveDeterministicUuid() {
        ShipmentId id1 = ShipmentId.of("shipment-123");
        ShipmentId id2 = ShipmentId.of("shipment-123");

        assertThat(id1.value()).isNotNull();
        assertThat(id1).isEqualTo(id2);
    }

    @Test
    void shipmentId_null_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> new ShipmentId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void trackingNumber_whenValid_shouldHoldValue() {
        TrackingNumber tracking = TrackingNumber.of("VN-POST-12345");
        assertThat(tracking.value()).isEqualTo("VN-POST-12345");
        assertThat(tracking.toString()).isEqualTo("VN-POST-12345");
    }

    @Test
    void trackingNumber_whenBlankOrNull_shouldThrowException() {
        assertThatThrownBy(() -> new TrackingNumber(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TrackingNumber(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TrackingNumber("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shipment_withValueObjectAccessors_shouldSetAndGetCorrectly() {
        Shipment shipment = new Shipment();
        ShipmentId shipmentId = ShipmentId.generate();
        OrderId orderId = OrderId.generate();
        UserId userId = UserId.generate();
        TrackingNumber tracking = TrackingNumber.of("TRK-999");

        shipment.setShipmentId(shipmentId);
        shipment.setOrderIdentifier(orderId);
        shipment.setUserIdentifier(userId);
        shipment.setTrackingNumberVo(tracking);

        assertThat(shipment.getShipmentId()).isEqualTo(shipmentId);
        assertThat(shipment.getId()).isEqualTo(shipmentId.value().toString());
        assertThat(shipment.getOrderIdentifier()).isEqualTo(orderId);
        assertThat(shipment.getOrderId()).isEqualTo(orderId.value().toString());
        assertThat(shipment.getUserIdentifier()).isEqualTo(userId);
        assertThat(shipment.getUserId()).isEqualTo(userId.value().toString());
        assertThat(shipment.getTrackingNumberVo()).isEqualTo(tracking);
        assertThat(shipment.getTrackingNumber()).isEqualTo("TRK-999");
    }
}
