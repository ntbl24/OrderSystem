package io.order.backend.order.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderItem;
import io.order.backend.order.dto.OrderDTO;
import io.order.backend.order.dto.OrderItemDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Order toEntity(OrderDTO dto);

    OrderDTO toDto(Order entity);

    List<OrderDTO> toOrderDTOList(List<Order> entityList);

    OrderItem toOrderItem(OrderItem item);

    OrderItemDTO toOrderItemDTO(OrderItem item);

}
