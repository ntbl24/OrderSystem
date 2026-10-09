package io.order.backend.shipping.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.order.backend.common.infrastructure.BaseMongoRepository;
import io.order.backend.shipping.domain.Shipment;

@Repository 
public interface MongoShipmentRepository extends BaseMongoRepository<Shipment> {
    Optional<Shipment> findByOrderId(String orderId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
