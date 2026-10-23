package io.order.backend.shipping.application.usecase;

import io.order.backend.shipping.application.port.in.ShipmentCommand;
import io.order.backend.shipping.domain.Shipment;

final class ShipmentCommandMapper {
    private ShipmentCommandMapper() {}

    static Shipment toDomain(ShipmentCommand command) {
        Shipment result = new Shipment();
        result.setOrderId(command.orderId());
        result.setUserId(command.userId());
        result.setTrackingNumber(command.trackingNumber());
        result.setCarrier(command.carrier());
        result.setServiceType(command.serviceType());
        result.setWeight(command.weight());
        result.setWeightUnit(command.weightUnit());
        result.setShippingCost(command.shippingCost());
        result.setStatus(command.status());
        result.setEstimatedDeliveryDate(command.estimatedDeliveryDate());
        result.setActualDeliveryDate(command.actualDeliveryDate());
        result.setOriginAddress(command.originAddress());
        result.setOriginCity(command.originCity());
        result.setOriginState(command.originState());
        result.setOriginCountry(command.originCountry());
        result.setOriginPostalCode(command.originPostalCode());
        result.setDestinationAddress(command.destinationAddress());
        result.setDestinationCity(command.destinationCity());
        result.setDestinationState(command.destinationState());
        result.setDestinationCountry(command.destinationCountry());
        result.setDestinationPostalCode(command.destinationPostalCode());
        result.setNotes(command.notes());
        result.setLabelUrl(command.labelUrl());
        result.setReturnLabelUrl(command.returnLabelUrl());
        result.setSignatureRequired(command.signatureRequired());
        result.setInsuranceRequired(command.insuranceRequired());
        result.setDeclaredValue(command.declaredValue());
        result.setCustomsInfo(command.customsInfo());
        result.setCustomsValue(command.customsValue());
        result.setCustomsCurrency(command.customsCurrency());
        return result;
    }
}
