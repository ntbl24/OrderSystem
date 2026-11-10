package com.abug.shipment.application.port.in;

import com.abug.shipment.domain.Shipment;

public interface CreateShipmentUseCase {
    Shipment createShipment(ShipmentCommand command);
}
