package com.abug.shipment.application.port.in;

import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;

public interface ShipmentCommandUseCase {
    void deleteShipment(ShipmentId id);
    Shipment calculateShippingCost(ShipmentCommand command);
    Shipment generateShippingLabel(ShipmentId id);
    
}
