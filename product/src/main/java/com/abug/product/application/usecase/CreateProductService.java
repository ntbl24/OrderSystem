package com.abug.product.application.usecase;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.product.application.port.in.ProductCommand;
import com.abug.product.application.port.in.CreateProductUseCase;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.domain.Product;
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
            throw new AppException(ErrorCode.PRODUCT_EXISTED, "Product with SKU " + command.sku() + " already exists");
        }
        Product product = ProductCommandMapper.toDomain(command);
        return productRepository.save(product);
    }
}
