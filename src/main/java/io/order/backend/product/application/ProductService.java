package io.order.backend.product.application;

import io.order.backend.product.application.port.in.ProductUseCase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.domain.Product;
import io.order.backend.product.dto.ProductDTO;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ProductService implements ProductUseCase {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new ResourceNotFoundException("Product with SKU " + productDTO.getSku() + " already exists");
        }
        Product product = productMapper.toEntity(productDTO);
        product.validate();
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        Product existingProduct = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Product updatedProduct = productMapper.toEntity(productDTO);
        updatedProduct.setId(existingProduct.getId());
        updatedProduct.setVersion(existingProduct.getVersion());
        updatedProduct.setReserved(existingProduct.getReserved());
        if (existingProduct.getReserved() > 0 && !existingProduct.getSellerId().equals(updatedProduct.getSellerId()))
            throw new IllegalStateException("Cannot change seller with reserved stock");
        updatedProduct.validate();
        updatedProduct = productRepository.save(updatedProduct);
        return productMapper.toDTO(updatedProduct);
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

    public ProductDTO getProductById(String id){
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public ProductDTO getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with sku: " + sku));
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStock(String id, int quantity) {
        Product product = productRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setStock(quantity);
        product.validate();
        product = productRepository.save(product);
    }
}
