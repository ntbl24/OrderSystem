package io.order.backend.shipping.application.port.in;

import java.util.Optional;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;

public interface ShipmentQueryUseCase {
    Optional<Shipment> getShipmentById(ShipmentId id);
    Optional<Shipment> getShipmentByOrderId(OrderId orderId);
    Optional<Shipment> getShipmentByTrackingNumber(TrackingNumber trackingNumber);
}
