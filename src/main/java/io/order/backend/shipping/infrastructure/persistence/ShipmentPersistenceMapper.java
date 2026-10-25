package io.order.backend.shipping.infrastructure.persistence;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.domain.*;
import io.order.backend.user.domain.UserId;

public final class ShipmentPersistenceMapper {
    public static Shipment domain(ShipmentEntity e) {
        Shipment d = new Shipment(
            ShipmentId.of(e.getId()),
            OrderId.of(e.getOrderId()),
            UserId.of(e.getUserId()),
            TrackingNumber.of(e.getTrackingNumber()),
            e.getCarrier(),
            e.getServiceType(),
            e.getWeight(),
            e.getWeightUnit(),
            e.getShippingCost(),
            e.getStatus(),
            e.getEstimatedDeliveryDate(),
            e.getActualDeliveryDate(),
            e.getOriginAddress(),
            e.getOriginCity(),
            e.getOriginState(),
            e.getOriginCountry(),
            e.getOriginPostalCode(),
            e.getDestinationAddress(),
            e.getDestinationCity(),
            e.getDestinationState(),
            e.getDestinationCountry(),
            e.getDestinationPostalCode(),
            e.getNotes(),
            e.getLabelUrl(),
            e.getReturnLabelUrl(),
            e.isSignatureRequired(),
            e.isInsuranceRequired(),
            e.getDeclaredValue(),
            e.getCustomsInfo(),
            e.getCustomsValue(),
            e.getCustomsCurrency()
        );
        return d;
    }

    public static void copy(Shipment d, ShipmentEntity e) {
        e.update(d.getId().value(),
                d.getOrderId().value(),
                d.getUserId().value(),
                d.getTrackingNumber().value(),
                d.getCarrier(),
                d.getServiceType(),
                d.getWeight(),
                d.getWeightUnit(),
                d.getShippingCost(),
                d.getStatus(),
                d.getEstimatedDeliveryDate(),
                d.getActualDeliveryDate(),
                d.getOriginAddress(),
                d.getOriginCity(),
                d.getOriginState(),
                d.getOriginCountry(),
                d.getOriginPostalCode(),
                d.getDestinationAddress(),
                d.getDestinationCity(),
                d.getDestinationState(),
                d.getDestinationCountry(),
                d.getDestinationPostalCode(),
                d.getNotes(),
                d.getLabelUrl(),
                d.getReturnLabelUrl(),
                d.isSignatureRequired(),
                d.isInsuranceRequired(),
                d.getDeclaredValue(),
                d.getCustomsInfo(),
                d.getCustomsValue(),
                d.getCustomsCurrency());
    }
}
