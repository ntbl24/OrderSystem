package io.order.backend.shipping.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "shipping")
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipmentEntity {
    @Id
    @Column(length = 36)
    private UUID id;
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    private boolean deleted;
    @Version
    private Long version;
    private UUID orderId;
    private UUID userId;
    private String trackingNumber;
    private String carrier;
    private String serviceType;
    @Column(precision = 19, scale = 2)
    private BigDecimal weight;
    private String weightUnit;
    @Column(precision = 19, scale = 2)
    private BigDecimal shippingCost;
    private String status;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String originAddress;
    private String originCity;
    private String originState;
    private String originCountry;
    private String originPostalCode;
    private String destinationAddress;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    private String destinationPostalCode;
    @Column(length = 2000)
    private String notes;
    private String labelUrl;
    private String returnLabelUrl;
    private boolean signatureRequired;
    private boolean insuranceRequired;
    @Column(precision = 19, scale = 2)
    private BigDecimal declaredValue;
    private String customsInfo;
    private String customsValue;
    private String customsCurrency;

    ShipmentEntity(UUID id, UUID orderId, UUID userId, String trackingNumber, String carrier, String serviceType,
            BigDecimal weight, String weightUnit, BigDecimal shippingCost, String status,
            LocalDateTime estimatedDeliveryDate, LocalDateTime actualDeliveryDate, String originAddress,
            String originCity, String originState, String originCountry, String originPostalCode,
            String destinationAddress, String destinationCity, String destinationState, String destinationCountry,
            String destinationPostalCode, String notes, String labelUrl, String returnLabelUrl,
            boolean signatureRequired, boolean insuranceRequired, BigDecimal declaredValue,
            String customsInfo, String customsValue, String customsCurrency){
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.serviceType = serviceType;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.shippingCost = shippingCost;
        this.status = status;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.originAddress = originAddress;
        this.originCity = originCity;
        this.originState = originState;
        this.originCountry = originCountry;
        this.originPostalCode = originPostalCode;
        this.destinationAddress = destinationAddress;
        this.destinationCity = destinationCity;
        this.destinationState = destinationState;
        this.destinationCountry = destinationCountry;
        this.destinationPostalCode = destinationPostalCode;
        this.notes = notes;
        this.labelUrl = labelUrl;
        this.returnLabelUrl = returnLabelUrl;
        this.signatureRequired = signatureRequired;
        this.insuranceRequired = insuranceRequired;
        this.declaredValue = declaredValue;
        this.customsInfo = customsInfo;
        this.customsValue = customsValue;
        this.customsCurrency = customsCurrency;
    }

    void update(UUID id, UUID orderId, UUID userId, String trackingNumber, String carrier, String serviceType,
            BigDecimal weight, String weightUnit, BigDecimal shippingCost, String status,
            LocalDateTime estimatedDeliveryDate, LocalDateTime actualDeliveryDate, String originAddress,
            String originCity, String originState, String originCountry, String originPostalCode,
            String destinationAddress, String destinationCity, String destinationState, String destinationCountry,
            String destinationPostalCode, String notes, String labelUrl, String returnLabelUrl,
            boolean signatureRequired, boolean insuranceRequired, BigDecimal declaredValue,
            String customsInfo, String customsValue, String customsCurrency){
        if(id != null) this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.serviceType = serviceType;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.shippingCost = shippingCost;
        this.status = status;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.originAddress = originAddress;
        this.originCity = originCity;
        this.originState = originState;
        this.originCountry = originCountry;
        this.originPostalCode = originPostalCode;
        this.destinationAddress = destinationAddress;
        this.destinationCity = destinationCity;
        this.destinationState = destinationState;
        this.destinationCountry = destinationCountry;
        this.destinationPostalCode = destinationPostalCode;
        this.notes = notes;
        this.labelUrl = labelUrl;
        this.returnLabelUrl = returnLabelUrl;
        this.signatureRequired = signatureRequired;
        this.insuranceRequired = insuranceRequired;
        this.declaredValue = declaredValue;
        this.customsInfo = customsInfo;
        this.customsValue = customsValue;
        this.customsCurrency = customsCurrency;
    }

    void delete(){
        this.deleted = true;
    }
}
