package io.order.backend.product.infrastructure.persistence;

import io.order.backend.product.domain.*;

public final class ProductPersistenceMapper {
    public static Product domain(ProductEntity e) {
        Product d = new Product();
        d.setId(e.id);
        d.setCreatedAt(e.createdAt);
        d.setUpdatedAt(e.updatedAt);
        d.setDeleted(e.deleted);
        d.setTitle(e.title);
        d.setCategory(e.category);
        d.setDescription(e.description);
        d.setPrice(e.price);
        d.setStock(e.stock);
        d.setSku(e.sku);
        d.setSellerId(e.sellerId);
        d.setReserved(e.reserved);
        d.setVersion(e.version);
        return d;
    }

    public static void copy(Product d, ProductEntity e) {
        e.id = d.getId().value().toString();
        e.createdAt = d.getCreatedAt();
        e.updatedAt = d.getUpdatedAt();
        e.deleted = d.isDeleted();
        e.title = d.getTitle();
        e.category = d.getCategory();
        e.description = d.getDescription();
        e.price = d.getPrice();
        e.stock = d.getStock();
        e.sku = d.getSku();
        e.sellerId = d.getSellerId();
        e.reserved = d.getReserved();
        e.version = d.getVersion();
    }
}
