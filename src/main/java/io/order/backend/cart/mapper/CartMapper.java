package io.order.backend.cart.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.cart.infrastructure.persistence.CartEntity;
import io.order.backend.user.domain.UserId;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    CartEntity toEntity(Cart cart);

    Cart toDomain(CartEntity entity);

    default CartId map(UUID value) {
        return value == null ? null : new CartId(value);
    }

    default UUID map(CartId id) {
        return id == null ? null : id.value();
    }

    default UserId mapUserId(UUID value) {
        return value == null ? null : new UserId(value);
    }
}
