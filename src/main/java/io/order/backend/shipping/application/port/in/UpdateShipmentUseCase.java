package io.order.backend.shipping.application.port.in;

import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;

public interface UpdateShipmentUseCase {
    Shipment updateShipment(ShipmentId id, ShipmentCommand command);
    Shipment updateShipmentStatus(ShipmentId id, String status);
    Shipment updateTrackingInfo(ShipmentId id, String trackingNumber, String status);
}
