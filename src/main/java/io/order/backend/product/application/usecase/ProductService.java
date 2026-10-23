package io.order.backend.product.application.usecase;

import io.order.backend.product.application.port.in.ProductUseCase;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.domain.Product;
import io.order.backend.product.application.port.in.ProductCommand;
import io.order.backend.product.application.port.out.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ProductService implements ProductUseCase {
    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(ProductCommand command) {
        if (productRepository.existsBySku(command.sku())) {
            throw new ResourceNotFoundException("Product with SKU " + command.sku() + " already exists");
        }
        Product product = ProductCommandMapper.toDomain(command);
        product.validate();
        product = productRepository.save(product);
        return product;
    }

    @Transactional
    public Product updateProduct(String id, ProductCommand command) {
        Product existingProduct = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.replaceDetails(command.title(), command.category(), command.description(),
                command.price(), command.stock(), command.sku(), command.sellerId());
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        Product product = productRepository.lockById(id).orElseThrow();
        if (product.getReserved() > 0) throw new IllegalStateException("Product has reserved stock");
        productRepository.deleteById(id);
    }

    public Product getProductById(String id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with sku: " + sku));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional
    public void updateStock(String id, int quantity) {
        Product product = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.updateStock(quantity);
        productRepository.save(product);
    }
}
