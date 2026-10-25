package io.order.backend.shipping.application.port.in;

import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;

public interface ShipmentCommandUseCase {
    void deleteShipment(ShipmentId id);
    Shipment calculateShippingCost(ShipmentCommand command);
    Shipment generateShippingLabel(ShipmentId id);
    
}
