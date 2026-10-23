package io.order.backend.shipping.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.order.backend.shipping.domain.Shipment;

public record ShipmentResponse(
        String id,
        String orderId,
        String userId,
        String trackingNumber,
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
    public static ShipmentResponse from(Shipment value) {
        return new ShipmentResponse(value.getId(), value.getOrderId(), value.getUserId(), value.getTrackingNumber(), value.getCarrier(), value.getServiceType(), value.getWeight(), value.getWeightUnit(), value.getShippingCost(), value.getStatus(), value.getEstimatedDeliveryDate(), value.getActualDeliveryDate(), value.getOriginAddress(), value.getOriginCity(), value.getOriginState(), value.getOriginCountry(), value.getOriginPostalCode(), value.getDestinationAddress(), value.getDestinationCity(), value.getDestinationState(), value.getDestinationCountry(), value.getDestinationPostalCode(), value.getNotes(), value.getLabelUrl(), value.getReturnLabelUrl(), value.isSignatureRequired(), value.isInsuranceRequired(), value.getDeclaredValue(), value.getCustomsInfo(), value.getCustomsValue(), value.getCustomsCurrency());
    }
}
