package io.order.backend.inventory.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.inventory.infrastructure.ProductRepository;

@Service 
public class InventoryService {
    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional 
    public void decreaseStock(Long productId, int quantity){
        if(quantity <= 0){
            throw new IllegalArgumentException(
                "Quantity must be greater than 0"
            );
        }

        int updated = productRepository.decreaseStock(productId, quantity);

        if(updated == 0){
            throw new IllegalStateException("Insufficient stock");
        }
    }
}
