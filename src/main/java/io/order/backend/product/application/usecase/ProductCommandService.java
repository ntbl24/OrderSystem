package io.order.backend.product.application.usecase;

import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import io.order.backend.product.application.port.in.ProductCommandUseCase;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
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
