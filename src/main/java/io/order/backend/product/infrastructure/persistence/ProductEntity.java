package io.order.backend.product.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    private boolean deleted;
    private String title;
    private String category;
    @Column(length = 2000)
    private String description;
    @Column(precision = 19, scale = 0)
    private BigDecimal price;
    private int stock;
    private String sku;
    private String sellerId;
    private int reserved;
    @Version
    private Long version;

    ProductEntity(UUID id, String title, String category, String description, BigDecimal price, int stock,
            String sku, String sellerId, int reserved) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.sellerId = sellerId;
        this.reserved = reserved;
    }

    public void update(UUID id, String title, String category, String description, BigDecimal price, int stock,
            String sku, String sellerId, int reserved) {
        if(id != null){
            this.id = id;
        }
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.sellerId = sellerId;
        this.reserved = reserved;
    }

    public void delete(){
        this.deleted = true;
    }
}
