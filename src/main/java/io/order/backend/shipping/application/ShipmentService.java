package io.order.backend.shipping.application;

import io.order.backend.shipping.application.port.in.ShipmentUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.dto.ShipmentDTO;
import io.order.backend.shipping.application.port.out.ShipmentRepository;
import io.order.backend.shipping.mapper.ShipmentMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ShipmentService implements ShipmentUseCase {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;

    @Transactional
    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO) {
        Shipment shipment = shipmentMapper.toEntity(shipmentDTO);
        shipment.setStatus("created");
        shipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDTO(shipment);
    }

    @Transactional
    public ShipmentDTO updateShipment(String id, ShipmentDTO shipmentDTO) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        Shipment updatedShipment = shipmentMapper.toEntity(shipmentDTO);
        updatedShipment.setId(existingShipment.getId());
        updatedShipment = shipmentRepository.save(updatedShipment);
        return shipmentMapper.toDTO(updatedShipment);
    }

    @Transactional
    public void deleteShipment(String id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shipment not found with id: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    public Optional<ShipmentDTO> getShipmentById(String id){
        return shipmentRepository.findById(id)
                .map(shipmentMapper::toDTO);
    }

    public Optional<ShipmentDTO> getShipmentByOrderId(String orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .map(shipmentMapper::toDTO);
    }

    public Optional<ShipmentDTO> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(shipmentMapper::toDTO);
    }

    @Transactional 
    public ShipmentDTO calculateShippingCost(ShipmentDTO shipmentDTO) {
        Shipment shipment = shipmentMapper.toEntity(shipmentDTO);
        shipment.setShippingCost(computeCost(shipment));
        return shipmentMapper.toDTO(shipment);
    }

    public ShipmentDTO generateShippingLabel(String id){
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        
        shipment.setLabelUrl("https://shipping-carrier.com/labels/" + shipment.getTrackingNumber());
        shipment.setStatus("label_generated");
        shipment = shipmentRepository.save(shipment);

        return shipmentMapper.toDTO(shipment);
    }

    @Transactional 
    public ShipmentDTO updateShipmentStatus(String id, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        
        shipment.setStatus(status);
        if("delivered".equals(status)){
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }
        shipment = shipmentRepository.save(shipment);
        
        return shipmentMapper.toDTO(shipment);
    }

    @Transactional
    public ShipmentDTO updateTrackingInfo(String id, String trackingNumber, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        shipment.setTrackingNumber(trackingNumber);
        shipment.setStatus(status);
        if("delivered".equals(status)){
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }
        shipment = shipmentRepository.save(shipment);
        
        return shipmentMapper.toDTO(shipment);
    }

    private BigDecimal computeCost(Shipment shipment){
        BigDecimal baseCost = BigDecimal.valueOf(5.00);
        BigDecimal perKgRate = BigDecimal.valueOf(1.50);
        BigDecimal weight = shipment.getWeight() != null ? shipment.getWeight() : BigDecimal.ONE;

        BigDecimal cost = baseCost.add(perKgRate.multiply(weight));

        if ("express".equalsIgnoreCase(shipment.getServiceType())){
            cost = cost.multiply(BigDecimal.valueOf(1.5));
        } else if ("overnight".equalsIgnoreCase(shipment.getServiceType())){
            cost = cost.multiply(BigDecimal.valueOf(2.0));
        }

        if(shipment.isInsuranceRequired()){
            BigDecimal declaredValue = shipment.getDeclaredValue() != null ? shipment.getDeclaredValue() : BigDecimal.ZERO;
            cost = cost.add(declaredValue.multiply(BigDecimal.valueOf(0.02)));
        }

        return cost.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
