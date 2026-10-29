package io.order.backend.product.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.in.ProductCommand;
import io.order.backend.product.application.port.in.CreateProductUseCase;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProductService implements CreateProductUseCase {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product createProduct(ProductCommand command) {
        if (productRepository.existsBySku(command.sku())) {
            throw new ResourceNotFoundException("Product with SKU " + command.sku() + " already exists");
        }
        Product product = ProductCommandMapper.toDomain(command);
        return productRepository.save(product);
    }
}
