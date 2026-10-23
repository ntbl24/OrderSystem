package io.order.backend.shipping.application.usecase;

import io.order.backend.shipping.application.port.in.ShipmentUseCase;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.application.port.in.ShipmentCommand;
import io.order.backend.shipping.application.port.out.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ShipmentService implements ShipmentUseCase {
    private final ShipmentRepository shipmentRepository;

    @Transactional
    public Shipment createShipment(ShipmentCommand command) {
        Shipment shipment = ShipmentCommandMapper.toDomain(command);
        shipment.markCreated();
        shipment = shipmentRepository.save(shipment);
        return shipment;
    }

    @Transactional
    public Shipment updateShipment(String id, ShipmentCommand command) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        Shipment updatedShipment = ShipmentCommandMapper.toDomain(command);
        updatedShipment.setId(existingShipment.getId());
        updatedShipment = shipmentRepository.save(updatedShipment);
        return updatedShipment;
    }

    @Transactional
    public void deleteShipment(String id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shipment not found with id: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    public Optional<Shipment> getShipmentById(String id){
        return shipmentRepository.findById(id);
    }

    public Optional<Shipment> getShipmentByOrderId(String orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public Optional<Shipment> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    @Transactional 
    public Shipment calculateShippingCost(ShipmentCommand command) {
        Shipment shipment = ShipmentCommandMapper.toDomain(command);
        shipment.setShippingCost(shipment.calculateShippingCost());
        return shipment;
    }

    @Transactional
    public Shipment generateShippingLabel(String id){
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        
        shipment.generateLabel();
        shipment = shipmentRepository.save(shipment);

        return shipment;
    }

    @Transactional 
    public Shipment updateShipmentStatus(String id, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        
        shipment.updateStatus(status, LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return shipment;
    }

    @Transactional
    public Shipment updateTrackingInfo(String id, String trackingNumber, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        shipment.updateTracking(trackingNumber, status, LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return shipment;
    }

}
