package io.order.backend.shipping.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.shipping.application.port.in.CreateShipmentUseCase;
import io.order.backend.shipping.application.port.in.ShipmentCommand;
import io.order.backend.shipping.application.port.out.ShipmentRepository;
import io.order.backend.shipping.domain.Shipment;
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
