package io.order.backend.product.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.in.ProductCommand;
import io.order.backend.product.application.port.in.UpdateProductUseCase;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductDetails;
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
    public Product updateProduct(ProductId id, ProductCommand command) {
        Product existingProduct = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        ProductDetails details = ProductCommandMapper.toDetails(command);

        existingProduct.replaceDetails(details);
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
