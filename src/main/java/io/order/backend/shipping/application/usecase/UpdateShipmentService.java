package io.order.backend.shipping.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.shipping.application.port.in.ShipmentCommand;
import io.order.backend.shipping.application.port.in.UpdateShipmentUseCase;
import io.order.backend.shipping.application.port.out.ShipmentRepository;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentDetails;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateShipmentService implements UpdateShipmentUseCase {
    
    private final ShipmentRepository shipmentRepository;

    @Transactional
    public Shipment updateShipment(ShipmentId id, ShipmentCommand command) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        ShipmentDetails details = ShipmentCommandMapper.toDetails(command);

        existingShipment.updateDetails(details);

        return shipmentRepository.save(existingShipment);
    }

    @Transactional 
    public Shipment updateShipmentStatus(ShipmentId id, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        
        shipment.updateStatus(status, LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return shipment;
    }

    @Transactional
    public Shipment updateTrackingInfo(ShipmentId id, String trackingNumber, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        shipment.updateTracking(TrackingNumber.of(trackingNumber), status, LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return shipment;
    }
}
