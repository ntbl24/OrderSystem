package io.order.backend.shipping.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.order.backend.common.domain.BaseEntity;
public class Shipment extends BaseEntity {
    private Long version;
    private String orderId;
    private String userId;
    private String trackingNumber;
    private String carrier;
    private String serviceType;
    private BigDecimal weight;
    private String weightUnit;
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
    private String notes;
    private String labelUrl;
    private String returnLabelUrl;
    private boolean signatureRequired;
    private boolean insuranceRequired;
    private BigDecimal declaredValue;
    private String customsInfo;
    private String customsValue;
    private String customsCurrency;
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    public String getOriginAddress() { return originAddress; }
    public void setOriginAddress(String originAddress) { this.originAddress = originAddress; }
    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public String getOriginState() { return originState; }
    public void setOriginState(String originState) { this.originState = originState; }
    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
    public String getOriginPostalCode() { return originPostalCode; }
    public void setOriginPostalCode(String originPostalCode) { this.originPostalCode = originPostalCode; }
    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    public String getDestinationState() { return destinationState; }
    public void setDestinationState(String destinationState) { this.destinationState = destinationState; }
    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }
    public String getDestinationPostalCode() { return destinationPostalCode; }
    public void setDestinationPostalCode(String destinationPostalCode) { this.destinationPostalCode = destinationPostalCode; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getLabelUrl() { return labelUrl; }
    public void setLabelUrl(String labelUrl) { this.labelUrl = labelUrl; }
    public String getReturnLabelUrl() { return returnLabelUrl; }
    public void setReturnLabelUrl(String returnLabelUrl) { this.returnLabelUrl = returnLabelUrl; }
    public boolean isSignatureRequired() { return signatureRequired; }
    public void setSignatureRequired(boolean signatureRequired) { this.signatureRequired = signatureRequired; }
    public boolean isInsuranceRequired() { return insuranceRequired; }
    public void setInsuranceRequired(boolean insuranceRequired) { this.insuranceRequired = insuranceRequired; }
    public BigDecimal getDeclaredValue() { return declaredValue; }
    public void setDeclaredValue(BigDecimal declaredValue) { this.declaredValue = declaredValue; }
    public String getCustomsInfo() { return customsInfo; }
    public void setCustomsInfo(String customsInfo) { this.customsInfo = customsInfo; }
    public String getCustomsValue() { return customsValue; }
    public void setCustomsValue(String customsValue) { this.customsValue = customsValue; }
    public String getCustomsCurrency() { return customsCurrency; }
    public void setCustomsCurrency(String customsCurrency) { this.customsCurrency = customsCurrency; }
    public BigDecimal calculateShippingCost(){
        BigDecimal baseCost = BigDecimal.valueOf(5.00);
        BigDecimal perKgRate = BigDecimal.valueOf(1.50);
        BigDecimal weight = getWeight() != null ? getWeight() : BigDecimal.ONE;

        BigDecimal cost = baseCost.add(perKgRate.multiply(weight));

        if ("express".equalsIgnoreCase(getServiceType())){
            cost = cost.multiply(BigDecimal.valueOf(1.5));
        } else if ("overnight".equalsIgnoreCase(getServiceType())){
            cost = cost.multiply(BigDecimal.valueOf(2.0));
        }

        if(isInsuranceRequired()){
            BigDecimal declaredValue = getDeclaredValue() != null ? getDeclaredValue() : BigDecimal.ZERO;
            cost = cost.add(declaredValue.multiply(BigDecimal.valueOf(0.02)));
        }

        return cost.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void markCreated() { status = "created"; }

    public void generateLabel() {
        labelUrl = "https://shipping-carrier.com/labels/" + trackingNumber;
        status = "label_generated";
    }

    public void updateStatus(String status, LocalDateTime now) {
        this.status = status;
        if ("delivered".equals(status)) actualDeliveryDate = now;
    }

    public void updateTracking(String trackingNumber, String status, LocalDateTime now) {
        this.trackingNumber = trackingNumber;
        updateStatus(status, now);
    }
}
