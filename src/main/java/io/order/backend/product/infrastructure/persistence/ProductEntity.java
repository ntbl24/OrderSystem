package io.order.backend.product.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @Column(length = 36)
    public String id;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public boolean deleted;
    public String title;
    public String category;
    @Column(length = 2000)
    public String description;
    @Column(precision = 19, scale = 0)
    public BigDecimal price;
    public int stock;
    public String sku;
    public String sellerId;
    public int reserved;
    @Version
    public Long version;
}
