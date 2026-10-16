package io.order.backend.product.mapper;

import io.order.backend.product.domain.Product;
import io.order.backend.product.dto.ProductDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-16T09:38:15+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20260930-0211, environment: Java 25.0.4.1 (Eclipse Adoptium)"
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
        product.setSellerId( productDTO.getSellerId() );
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
        productDTO.setId( product.getId() );
        productDTO.setPrice( product.getPrice() );
        productDTO.setSellerId( product.getSellerId() );
        productDTO.setSku( product.getSku() );
        productDTO.setStock( product.getStock() );
        productDTO.setTitle( product.getTitle() );

        return productDTO;
    }
}
