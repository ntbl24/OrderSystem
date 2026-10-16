package io.order.backend.shipping.application.port.in;

import io.order.backend.shipping.dto.ShipmentDTO;
import java.util.Optional;

public interface ShipmentUseCase {
    ShipmentDTO createShipment(ShipmentDTO dto);
    ShipmentDTO updateShipment(String id, ShipmentDTO dto);
    void deleteShipment(String id);
    Optional<ShipmentDTO> getShipmentById(String id);
    Optional<ShipmentDTO> getShipmentByOrderId(String orderId);
    Optional<ShipmentDTO> getShipmentByTrackingNumber(String trackingNumber);
    ShipmentDTO calculateShippingCost(ShipmentDTO dto);
    ShipmentDTO generateShippingLabel(String id);
    ShipmentDTO updateShipmentStatus(String id, String status);
    ShipmentDTO updateTrackingInfo(String id, String trackingNumber, String status);
}
