package io.order.backend.product.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Product {
    private ProductId id;
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

    public Product() {}

    public Product(ProductId id, String title, String category, String description,
                   BigDecimal price, int stock, String sku, String sellerId) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.sellerId = sellerId;
    }

    public static Product create(String title, String category, String description,
                                 BigDecimal price, int stock, String sku, String sellerId) {
        Product product = new Product(ProductId.generate(), title, category, description, price, stock, sku, sellerId);
        product.validate();
        return product;
    }

    public static Product restore(ProductId id, boolean deleted, String title, String category, String description,
                                  BigDecimal price, int stock, String sku, String sellerId,
                                  int reserved, Long version) {
        Product product = new Product();
        product.id = id;
        product.deleted = deleted;
        product.title = title;
        product.category = category;
        product.description = description;
        product.price = price;
        product.stock = stock;
        product.sku = sku;
        product.sellerId = sellerId;
        product.reserved = reserved;
        product.version = version;
        return product;
    }

    public void validate() {
        if (sellerId == null || sellerId.isBlank() || price == null || price.signum() <= 0
                || price.stripTrailingZeros().scale() > 0 || price.precision() > 19
                || stock < 0 || reserved < 0 || reserved > stock) {
            throw new IllegalArgumentException("Invalid product, integer VND price or inventory");
        }
    }

    public void reserve(int quantity) {
        if (quantity < 1 || stock - reserved < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        reserved = Math.addExact(reserved, quantity);
    }

    public void release(int quantity) {
        if (quantity < 1 || reserved < quantity) {
            throw new IllegalStateException("Invalid stock release");
        }
        reserved -= quantity;
    }

    public void consume(int quantity) {
        release(quantity);
        stock -= quantity;
    }

    public void restock(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        stock = Math.addExact(stock, quantity);
    }

    public void replaceDetails(String title, String category, String description, BigDecimal price,
                               int stock, String sku, String sellerId) {
        if (reserved > 0 && !Objects.equals(this.sellerId, sellerId)) {
            throw new IllegalStateException("Cannot change seller with reserved stock");
        }
        Product replacement = new Product();
        replacement.sellerId = sellerId;
        replacement.price = price;
        replacement.stock = stock;
        replacement.reserved = reserved;
        replacement.validate();

        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.sellerId = sellerId;
    }

    public void updateStock(int quantity) {
        if (quantity < reserved || quantity < 0) {
            throw new IllegalArgumentException("Invalid product, integer VND price or inventory");
        }
        stock = quantity;
        validate();
    }

    public ProductId getId() {
        return id;
    }

    public boolean isDeleted() {
        return deleted;
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

    public Long getVersion() {
        return version;
    }
}
