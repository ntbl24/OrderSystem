package io.order.backend.shipping.application.port.out;

import java.util.Optional;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;

public interface ShipmentRepository {
    Optional<Shipment> findById(ShipmentId shipmentId);

    Shipment save(Shipment shipment);

    boolean existsById(ShipmentId id);

    void deleteById(ShipmentId id);

    Optional<Shipment> findByOrderId(OrderId orderId);

    Optional<Shipment> findByTrackingNumber(TrackingNumber trackingNumber);
}
