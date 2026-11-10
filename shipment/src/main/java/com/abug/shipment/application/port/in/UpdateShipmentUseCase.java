package com.abug.shipment.application.port.in;

import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;

public interface UpdateShipmentUseCase {
    Shipment updateShipment(ShipmentId id, ShipmentCommand command);
    Shipment updateShipmentStatus(ShipmentId id, String status);
    Shipment updateTrackingInfo(ShipmentId id, String trackingNumber, String status);
}
