package com.abug.shipment.application.port.in;

import java.util.Optional;

import com.abug.shipment.domain.OrderId;
import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.domain.TrackingNumber;

public interface ShipmentQueryUseCase {
    Shipment getShipmentById(ShipmentId id);
    Shipment getShipmentByOrderId(OrderId orderId);
    Shipment getShipmentByTrackingNumber(TrackingNumber trackingNumber);
}
