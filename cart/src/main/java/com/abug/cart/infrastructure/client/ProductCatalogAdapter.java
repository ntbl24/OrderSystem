package com.abug.cart.infrastructure.client;

import com.abug.cart.application.port.out.ProductCatalogPort;
import com.abug.cart.domain.ProductId;
import com.abug.cart.infrastructure.client.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogAdapter implements ProductCatalogPort {
    private final ProductClient client;


    @Override
    public Optional<Snapshot> findById(ProductId id) {

        ProductResponse response = client.getProductById(id.toString());


        return Optional.of(new Snapshot(id, response.title(), response.price()));
    }
}
