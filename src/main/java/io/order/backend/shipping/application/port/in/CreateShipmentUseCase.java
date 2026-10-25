package io.order.backend.shipping.application.port.in;

import io.order.backend.shipping.domain.Shipment;

public interface CreateShipmentUseCase {
    Shipment createShipment(ShipmentCommand command);
}
