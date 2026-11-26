package com.abug.product.application.usecase;

import com.abug.product.application.port.in.command.ProductCommand;
import com.abug.product.application.port.in.UpdateProductUseCase;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.common.exception.AppException;
import com.abug.product.common.exception.ErrorCode;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductDetails;
import com.abug.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductService implements UpdateProductUseCase {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product updateProduct(ProductId id, ProductCommand command) {
        Product existingProduct = productRepository.lockById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductDetails details = ProductCommandMapper.toDetails(command);

        existingProduct.replaceDetails(details);
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void updateStock(ProductId id, int quantity) {
        Product product = productRepository.lockById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.updateStock(quantity);
        productRepository.save(product);
    }
}
