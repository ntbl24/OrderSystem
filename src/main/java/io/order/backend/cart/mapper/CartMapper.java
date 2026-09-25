package io.order.backend.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.dto.CartDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Cart toEntity(CartDTO dto);
    CartDTO toDto(Cart entity);
}
