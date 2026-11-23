package com.abug.cart.infrastructure.external.httpclient;

import com.abug.cart.application.port.out.ProductCatalogPort;
import com.abug.cart.common.exception.AppException;
import com.abug.cart.common.exception.ErrorCode;
import com.abug.cart.domain.ProductId;
import com.abug.cart.infrastructure.external.httpclient.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogAdapter implements ProductCatalogPort {
    private final ProductClient client;


    @Override
    public Optional<Snapshot> findById(ProductId id) {

        try{
            ProductResponse response = client.getProductById(id.toString());
        return Optional.of(new Snapshot(ProductId.of(response.id()), response.title(), response.price()));

        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
