package com.abug.cart.application.usecase;

import com.abug.cart.application.port.out.ProductCatalogPort;
import com.abug.cart.common.exception.AppException;
import com.abug.cart.common.exception.ErrorCode;
import com.abug.cart.domain.Cart;
import com.abug.cart.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartPricingService {
    private final ProductCatalogPort products;

    public ProductCatalogPort.Snapshot product(ProductId id) {
        return products.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public void refresh(Cart cart) {
        cart.getItems().forEach(item -> {
            var p = product(item.getProductId());
            cart.addOrUpdateItem(item.getProductId(), p.title(), item.getQuantity(), p.price());
        });
    }
}
