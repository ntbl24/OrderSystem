package io.order.backend.shipping.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.order.backend.order.domain.OrderId;
import io.order.backend.user.domain.UserId;

public class Shipment {
    private ShipmentId id;
    private Long version;
    private OrderId orderId;
    private UserId userId;
    private TrackingNumber trackingNumber;
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
    private boolean deleted;

    public ShipmentId getId() { return id; }
    public Long getVersion() { return version; }
    public OrderId getOrderId() { return orderId; }
    public UserId getUserId() { return userId; }
    public TrackingNumber getTrackingNumber() { return trackingNumber; }
    public String getCarrier() { return carrier; }
    public String getServiceType() { return serviceType; }
    public BigDecimal getWeight() { return weight; }
    public String getWeightUnit() { return weightUnit; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public String getStatus() { return status; }
    public LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public String getOriginAddress() { return originAddress; }
    public String getOriginCity() { return originCity; }
    public String getOriginState() { return originState; }
    public String getOriginCountry() { return originCountry; }
    public String getOriginPostalCode() { return originPostalCode; }
    public String getDestinationAddress() { return destinationAddress; }
    public String getDestinationCity() { return destinationCity; }
    public String getDestinationState() { return destinationState; }
    public String getDestinationCountry() { return destinationCountry; }
    public String getDestinationPostalCode() { return destinationPostalCode; }
    public String getNotes() { return notes; }
    public String getLabelUrl() { return labelUrl; }
    public String getReturnLabelUrl() { return returnLabelUrl; }
    public boolean isSignatureRequired() { return signatureRequired; }
    public boolean isInsuranceRequired() { return insuranceRequired; }
    public BigDecimal getDeclaredValue() { return declaredValue; }
    public String getCustomsInfo() { return customsInfo; }
    public String getCustomsValue() { return customsValue; }
    public String getCustomsCurrency() { return customsCurrency; }

    public static Shipment create(OrderId orderId,
        UserId userId,
        TrackingNumber trackingNumber,
        String carrier,
        String serviceType,
        BigDecimal weight,
        String weightUnit,
        BigDecimal shippingCost,
        String status,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate,
        String originAddress,
        String originCity,
        String originState,
        String originCountry,
        String originPostalCode,
        String destinationAddress,
        String destinationCity,
        String destinationState,
        String destinationCountry,
        String destinationPostalCode,
        String notes,
        String labelUrl,
        String returnLabelUrl,
        boolean signatureRequired,
        boolean insuranceRequired,
        BigDecimal declaredValue,
        String customsInfo,
        String customsValue,
        String customsCurrency
    ) {
        return new Shipment(ShipmentId.generate(), orderId, userId, trackingNumber, 
        carrier, serviceType, weight, weightUnit, shippingCost, status, 
        estimatedDeliveryDate, actualDeliveryDate, 
        originAddress, originCity, originState, originCountry, originPostalCode, 
        destinationAddress, destinationCity, destinationState, destinationCountry, 
        destinationPostalCode, notes, labelUrl, returnLabelUrl, 
        signatureRequired, insuranceRequired, declaredValue, 
        customsInfo, customsValue, customsCurrency);
    }

    public void updateDetails(ShipmentDetails newShipment) {
            this.orderId = newShipment.orderId();
            this.userId = newShipment.userId();
            this.trackingNumber = newShipment.trackingNumber();
            this.carrier = newShipment.carrier();
            this.serviceType = newShipment.serviceType();
            this.weight = newShipment.weight();
            this.weightUnit = newShipment.weightUnit();
            this.shippingCost = newShipment.shippingCost();
            this.status = newShipment.status();
            this.estimatedDeliveryDate = newShipment.estimatedDeliveryDate();
            this.actualDeliveryDate = newShipment.actualDeliveryDate();
            this.originAddress = newShipment.originAddress();
            this.originCity = newShipment.originCity();
            this.originState = newShipment.originState();
            this.originCountry = newShipment.originCountry();
            this.originPostalCode = newShipment.originPostalCode();
            this.destinationAddress = newShipment.destinationAddress();
            this.destinationCity = newShipment.destinationCity();
            this.destinationState = newShipment.destinationState();
            this.destinationCountry = newShipment.destinationCountry();
            this.destinationPostalCode = newShipment.destinationPostalCode();
            this.notes = newShipment.notes();
            this.labelUrl = newShipment.labelUrl();
            this.returnLabelUrl = newShipment.returnLabelUrl();
            this.signatureRequired = newShipment.signatureRequired();
            this.insuranceRequired = newShipment.insuranceRequired();
            this.declaredValue = newShipment.declaredValue();
            this.customsInfo = newShipment.customsInfo();
            this.customsValue = newShipment.customsValue();
            this.customsCurrency = newShipment.customsCurrency();
        }
    public Shipment(){}

    public Shipment(
        ShipmentId id,
        OrderId orderId,
        UserId userId,
        TrackingNumber trackingNumber,
        String carrier,
        String serviceType,
        BigDecimal weight,
        String weightUnit,
        BigDecimal shippingCost,
        String status,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime actualDeliveryDate,
        String originAddress,
        String originCity,
        String originState,
        String originCountry,
        String originPostalCode,
        String destinationAddress,
        String destinationCity,
        String destinationState,
        String destinationCountry,
        String destinationPostalCode,
        String notes,
        String labelUrl,
        String returnLabelUrl,
        boolean signatureRequired,
        boolean insuranceRequired,
        BigDecimal declaredValue,
        String customsInfo,
        String customsValue,
        String customsCurrency) {
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

    public void calculateShippingCost(){
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

        this.shippingCost = cost.setScale(2, java.math.RoundingMode.HALF_UP);
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

    public void updateTracking(TrackingNumber trackingNumber, String status, LocalDateTime now) {
        this.trackingNumber = trackingNumber;
        updateStatus(status, now);
    }

    public ShipmentId getShipmentId() {
        return id;
    }

    public OrderId getOrderIdentifier() {
        return orderId;
    }

    public UserId getUserIdentifier() {
        return userId;
    }

    public TrackingNumber getTrackingNumberVo() {
        return trackingNumber;
    }
}
