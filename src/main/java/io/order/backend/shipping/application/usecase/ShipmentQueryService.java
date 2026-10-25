package io.order.backend.shipping.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.application.port.in.ShipmentQueryUseCase;
import io.order.backend.shipping.application.port.out.ShipmentRepository;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentQueryService implements ShipmentQueryUseCase {
    private final ShipmentRepository shipmentRepository;

    public Optional<Shipment> getShipmentById(ShipmentId id){
        return shipmentRepository.findById(id);
    }

    public Optional<Shipment> getShipmentByOrderId(OrderId orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public Optional<Shipment> getShipmentByTrackingNumber(TrackingNumber trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

}
