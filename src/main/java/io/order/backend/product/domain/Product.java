package io.order.backend.product.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private ProductId id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private String title;
    private String category;
    private String description;
    private BigDecimal price;
    private int stock;
    private String sku;
    private String sellerId;
    private int reserved;
    private Long version;

    public void validate() {
        if (sellerId == null || sellerId.isBlank() || price == null || price.signum() <= 0
                || price.stripTrailingZeros().scale() > 0 || price.precision() > 19
                || stock < 0 || reserved < 0 || reserved > stock) {
            throw new IllegalArgumentException("Invalid product, integer VND price or inventory");
        }
    }

    public void reserve(int quantity) {
        if (quantity < 1 || stock - reserved < quantity) throw new IllegalStateException("Insufficient stock");
        reserved = Math.addExact(reserved, quantity);
    }

    public void release(int quantity) {
        if (quantity < 1 || reserved < quantity) throw new IllegalStateException("Invalid stock release");
        reserved -= quantity;
    }

    public void consume(int quantity) {
        release(quantity);
        stock -= quantity;
    }

    public void restock(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Invalid quantity");
        stock = Math.addExact(stock, quantity);
    }

    public ProductId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getSku() {
        return sku;
    }

    public String getSellerId() {
        return sellerId;
    }

    public int getReserved() {
        return reserved;
    }
    public void setId(ProductId id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setSku(String sku) { this.sku = sku; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public void setReserved(int reserved) { this.reserved = reserved; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public void setId(String id) { this.id = ProductId.of(id); }

    public void replaceDetails(String title, String category, String description, BigDecimal price,
                               int stock, String sku, String sellerId) {
        if (reserved > 0 && !java.util.Objects.equals(this.sellerId, sellerId))
            throw new IllegalStateException("Cannot change seller with reserved stock");
        Product replacement = new Product();
        replacement.sellerId = sellerId;
        replacement.price = price;
        replacement.stock = stock;
        replacement.reserved = reserved;
        replacement.validate();
        this.title = title; this.category = category; this.description = description;
        this.price = price; this.stock = stock; this.sku = sku; this.sellerId = sellerId;
    }

    public void updateStock(int quantity) {
        if (quantity < reserved || quantity < 0)
            throw new IllegalArgumentException("Invalid product, integer VND price or inventory");
        stock = quantity;
        validate();
    }
}
