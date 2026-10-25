package io.order.backend.shipping.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.domain.TrackingNumber;
import io.order.backend.user.domain.UserId;

public record ShipmentCommand(
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
        String customsCurrency
) {}
