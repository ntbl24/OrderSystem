package com.abug.shipment.application.usecase;

import com.abug.shipment.application.port.in.ShipmentCommandUseCase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.application.port.in.ShipmentCommand;
import com.abug.shipment.application.port.out.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ShipmentCommandService implements ShipmentCommandUseCase {
    private final ShipmentRepository shipmentRepository;


    @Transactional
    public void deleteShipment(ShipmentId id) {
        if (!shipmentRepository.existsById(id)) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
        shipmentRepository.deleteById(id);
    }

    
    @Transactional 
    public Shipment calculateShippingCost(ShipmentCommand command) {
        Shipment shipment = ShipmentCommandMapper.toDomain(command);
        shipment.calculateShippingCost();
        return shipment;
    }

    @Transactional
    public Shipment generateShippingLabel(ShipmentId id){
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        shipment.generateLabel();
        shipment = shipmentRepository.save(shipment);

        return shipment;
    }

    

}
