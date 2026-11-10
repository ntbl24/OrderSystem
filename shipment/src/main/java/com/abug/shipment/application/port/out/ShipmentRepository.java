package com.abug.shipment.application.port.out;

import java.util.Optional;

import com.abug.shipment.domain.OrderId;
import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.domain.TrackingNumber;

public interface ShipmentRepository {
    Optional<Shipment> findById(ShipmentId shipmentId);

    Shipment save(Shipment shipment);

    boolean existsById(ShipmentId id);

    void deleteById(ShipmentId id);

    Optional<Shipment> findByOrderId(OrderId orderId);

    Optional<Shipment> findByTrackingNumber(TrackingNumber trackingNumber);
}
