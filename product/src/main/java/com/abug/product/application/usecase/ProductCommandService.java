package com.abug.product.application.usecase;

import com.abug.product.application.port.in.ProductCommandUseCase;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.common.exception.AppException;
import com.abug.product.common.exception.ErrorCode;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCommandService implements ProductCommandUseCase {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void deleteProduct(ProductId id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        Product product = productRepository.lockById(id).orElseThrow();
        if (product.getReserved() > 0) {
            throw new AppException(ErrorCode.CONFLICT, "Product has reserved stock");
        }
        productRepository.deleteById(id);
    }
}
