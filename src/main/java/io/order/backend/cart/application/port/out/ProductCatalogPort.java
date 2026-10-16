package io.order.backend.cart.application.port.out;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductCatalogPort {
    Optional<Snapshot> findById(String id);

    record Snapshot(String id, String title, BigDecimal price) {}
}
