package io.order.backend.shipping.infrastructure.persistence;

import io.order.backend.shipping.domain.*;

public final class ShipmentPersistenceMapper {
    public static Shipment domain(ShipmentEntity e) {
        Shipment d = new Shipment();
        d.setId(e.id);
        d.setCreatedAt(e.createdAt);
        d.setUpdatedAt(e.updatedAt);
        d.setDeleted(e.deleted);
        d.setVersion(e.version);
        d.setOrderId(e.orderId);
        d.setUserId(e.userId);
        d.setTrackingNumber(e.trackingNumber);
        d.setCarrier(e.carrier);
        d.setServiceType(e.serviceType);
        d.setWeight(e.weight);
        d.setWeightUnit(e.weightUnit);
        d.setShippingCost(e.shippingCost);
        d.setStatus(e.status);
        d.setEstimatedDeliveryDate(e.estimatedDeliveryDate);
        d.setActualDeliveryDate(e.actualDeliveryDate);
        d.setOriginAddress(e.originAddress);
        d.setOriginCity(e.originCity);
        d.setOriginState(e.originState);
        d.setOriginCountry(e.originCountry);
        d.setOriginPostalCode(e.originPostalCode);
        d.setDestinationAddress(e.destinationAddress);
        d.setDestinationCity(e.destinationCity);
        d.setDestinationState(e.destinationState);
        d.setDestinationCountry(e.destinationCountry);
        d.setDestinationPostalCode(e.destinationPostalCode);
        d.setNotes(e.notes);
        d.setLabelUrl(e.labelUrl);
        d.setReturnLabelUrl(e.returnLabelUrl);
        d.setSignatureRequired(e.signatureRequired);
        d.setInsuranceRequired(e.insuranceRequired);
        d.setDeclaredValue(e.declaredValue);
        d.setCustomsInfo(e.customsInfo);
        d.setCustomsValue(e.customsValue);
        d.setCustomsCurrency(e.customsCurrency);
        return d;
    }

    public static void copy(Shipment d, ShipmentEntity e) {
        e.id = d.getId();
        e.createdAt = d.getCreatedAt();
        e.updatedAt = d.getUpdatedAt();
        e.deleted = d.isDeleted();
        e.version = d.getVersion();
        e.orderId = d.getOrderId();
        e.userId = d.getUserId();
        e.trackingNumber = d.getTrackingNumber();
        e.carrier = d.getCarrier();
        e.serviceType = d.getServiceType();
        e.weight = d.getWeight();
        e.weightUnit = d.getWeightUnit();
        e.shippingCost = d.getShippingCost();
        e.status = d.getStatus();
        e.estimatedDeliveryDate = d.getEstimatedDeliveryDate();
        e.actualDeliveryDate = d.getActualDeliveryDate();
        e.originAddress = d.getOriginAddress();
        e.originCity = d.getOriginCity();
        e.originState = d.getOriginState();
        e.originCountry = d.getOriginCountry();
        e.originPostalCode = d.getOriginPostalCode();
        e.destinationAddress = d.getDestinationAddress();
        e.destinationCity = d.getDestinationCity();
        e.destinationState = d.getDestinationState();
        e.destinationCountry = d.getDestinationCountry();
        e.destinationPostalCode = d.getDestinationPostalCode();
        e.notes = d.getNotes();
        e.labelUrl = d.getLabelUrl();
        e.returnLabelUrl = d.getReturnLabelUrl();
        e.signatureRequired = d.isSignatureRequired();
        e.insuranceRequired = d.isInsuranceRequired();
        e.declaredValue = d.getDeclaredValue();
        e.customsInfo = d.getCustomsInfo();
        e.customsValue = d.getCustomsValue();
        e.customsCurrency = d.getCustomsCurrency();
    }
}
