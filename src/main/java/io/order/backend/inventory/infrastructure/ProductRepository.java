package io.order.backend.inventory.infrastructure;

public interface ProductRepository {
    int decreaseStock(Long productId, int quantity);
}
