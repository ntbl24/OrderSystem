package io.order.backend.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.dto.ShipmentDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Shipment toEntity(ShipmentDTO shipmentDTO);
    
    ShipmentDTO toDTO(Shipment shipment);

    
}
