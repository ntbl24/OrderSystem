package io.order.backend.product.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.in.UpdateProductCommand;
import io.order.backend.product.application.port.in.UpdateProductUseCase;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductService implements UpdateProductUseCase {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product updateProduct(ProductId id, UpdateProductCommand command) {
        Product existingProduct = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.replaceDetails(command.title(), command.category(), command.description(),
                command.price(), command.stock(), command.sku(), command.sellerId());
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void updateStock(ProductId id, int quantity) {
        Product product = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.updateStock(quantity);
        productRepository.save(product);
    }
}
