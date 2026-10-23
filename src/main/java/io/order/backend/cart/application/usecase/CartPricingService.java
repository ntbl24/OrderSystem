package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.out.ProductCatalogPort;
import io.order.backend.cart.domain.Cart;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartPricingService {
    private final ProductCatalogPort products;

    public ProductCatalogPort.Snapshot product(ProductId id) {
        return products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public void refresh(Cart cart) {
        cart.getItems().forEach(item -> {
            var p = product(item.getProductId());
            cart.addOrUpdateItem(item.getProductId(), p.title(), item.getQuantity(), p.price());
        });
    }
}
