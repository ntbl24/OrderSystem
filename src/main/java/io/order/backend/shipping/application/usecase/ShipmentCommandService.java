package io.order.backend.shipping.application.usecase;

import io.order.backend.shipping.application.port.in.ShipmentCommandUseCase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.application.port.in.ShipmentCommand;
import io.order.backend.shipping.application.port.out.ShipmentRepository;

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
