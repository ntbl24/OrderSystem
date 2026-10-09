package io.order.backend.shipping.infrastructure;

import java.util.Optional;

import io.order.backend.shipping.domain.Shipment;

public interface ShipmentRepository {
    Optional<Shipment> findById(String shipmentId);
    Shipment save(Shipment shipment);
    boolean existsById(String id);
    void deleteById(String id);
    Optional<Shipment> findByOrderId(String orderId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
