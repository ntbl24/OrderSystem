package io.order.backend.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.order.backend.product.domain.Product;
import io.order.backend.product.dto.ProductDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping (target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO productDTO);
}
