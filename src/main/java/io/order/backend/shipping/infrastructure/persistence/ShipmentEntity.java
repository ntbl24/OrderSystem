package io.order.backend.shipping.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping")
public class ShipmentEntity {
    @Id
    @Column(length = 36)
    public String id;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public boolean deleted;
    @Version
    public Long version;
    public String orderId;
    public String userId;
    public String trackingNumber;
    public String carrier;
    public String serviceType;
    @Column(precision = 19, scale = 2)
    public BigDecimal weight;
    public String weightUnit;
    @Column(precision = 19, scale = 2)
    public BigDecimal shippingCost;
    public String status;
    public LocalDateTime estimatedDeliveryDate;
    public LocalDateTime actualDeliveryDate;
    public String originAddress;
    public String originCity;
    public String originState;
    public String originCountry;
    public String originPostalCode;
    public String destinationAddress;
    public String destinationCity;
    public String destinationState;
    public String destinationCountry;
    public String destinationPostalCode;
    @Column(length = 2000)
    public String notes;
    public String labelUrl;
    public String returnLabelUrl;
    public boolean signatureRequired;
    public boolean insuranceRequired;
    @Column(precision = 19, scale = 2)
    public BigDecimal declaredValue;
    public String customsInfo;
    public String customsValue;
    public String customsCurrency;
}
