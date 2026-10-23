package io.order.backend.shipping.application.port.in;

import io.order.backend.shipping.domain.Shipment;
import java.util.Optional;

public interface ShipmentUseCase {
    Shipment createShipment(ShipmentCommand command);
    Shipment updateShipment(String id, ShipmentCommand command);
    void deleteShipment(String id);
    Optional<Shipment> getShipmentById(String id);
    Optional<Shipment> getShipmentByOrderId(String orderId);
    Optional<Shipment> getShipmentByTrackingNumber(String trackingNumber);
    Shipment calculateShippingCost(ShipmentCommand command);
    Shipment generateShippingLabel(String id);
    Shipment updateShipmentStatus(String id, String status);
    Shipment updateTrackingInfo(String id, String trackingNumber, String status);
}
