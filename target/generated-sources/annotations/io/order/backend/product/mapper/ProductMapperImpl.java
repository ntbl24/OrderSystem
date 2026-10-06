package io.order.backend.product.mapper;

import io.order.backend.product.domain.Product;
import io.order.backend.product.dto.ProductDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-03T09:47:04+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductDTO productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        Product product = new Product();

        product.setCategory( productDTO.getCategory() );
        product.setDescription( productDTO.getDescription() );
        product.setPrice( productDTO.getPrice() );
        product.setSku( productDTO.getSku() );
        product.setStock( productDTO.getStock() );
        product.setTitle( productDTO.getTitle() );

        return product;
    }

    @Override
    public ProductDTO toDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setCategory( product.getCategory() );
        productDTO.setDescription( product.getDescription() );
        productDTO.setPrice( product.getPrice() );
        productDTO.setSku( product.getSku() );
        productDTO.setStock( product.getStock() );
        productDTO.setTitle( product.getTitle() );

        return productDTO;
    }
}
