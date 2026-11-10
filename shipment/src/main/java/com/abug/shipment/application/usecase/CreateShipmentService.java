package com.abug.shipment.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abug.shipment.application.port.in.CreateShipmentUseCase;
import com.abug.shipment.application.port.in.ShipmentCommand;
import com.abug.shipment.application.port.out.ShipmentRepository;
import com.abug.shipment.domain.Shipment;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CreateShipmentService implements CreateShipmentUseCase{
    private final ShipmentRepository shipmentRepository;

    @Transactional
    public Shipment createShipment(ShipmentCommand command) {
        Shipment shipment = ShipmentCommandMapper.toDomain(command);
        shipment.markCreated();
        shipment = shipmentRepository.save(shipment);
        return shipment;
    }
}
