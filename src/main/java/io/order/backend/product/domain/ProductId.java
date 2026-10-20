package io.order.backend.product.domain;

import java.util.UUID;

public record ProductId(UUID value) {
    public static ProductId generate(){
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId of(String value){
        return new ProductId(UUID.fromString(value));
    }
}
