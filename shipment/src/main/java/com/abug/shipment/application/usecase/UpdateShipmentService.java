package com.abug.shipment.application.usecase;

import com.abug.shipment.application.port.in.ShipmentCommand;
import com.abug.shipment.application.port.in.UpdateShipmentUseCase;
import com.abug.shipment.application.port.out.ShipmentRepository;
import com.abug.shipment.common.exception.AppException;
import com.abug.shipment.common.exception.ErrorCode;
import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentDetails;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.domain.TrackingNumber;
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
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        ShipmentDetails details = ShipmentCommandMapper.toDetails(command);

        existingShipment.updateDetails(details);

        return shipmentRepository.save(existingShipment);
    }

    @Transactional 
    public Shipment updateShipmentStatus(ShipmentId id, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        shipment.updateStatus(status, LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return shipment;
    }

    @Transactional
    public Shipment updateTrackingInfo(ShipmentId id, String trackingNumber, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        shipment.updateTracking(TrackingNumber.of(trackingNumber), status, LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return shipment;
    }
}
