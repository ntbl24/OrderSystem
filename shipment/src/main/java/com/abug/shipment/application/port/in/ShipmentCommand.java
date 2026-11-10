package com.abug.shipment.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.abug.shipment.domain.OrderId;
import com.abug.shipment.domain.TrackingNumber;
import com.abug.shipment.domain.UserId;

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
