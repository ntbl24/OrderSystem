package io.order.backend.shipping.application.usecase;

import io.order.backend.shipping.application.port.in.ShipmentCommand;
import io.order.backend.shipping.domain.Shipment;

final class ShipmentCommandMapper {
    private ShipmentCommandMapper() {}

    static Shipment toDomain(ShipmentCommand command) {
        Shipment result = Shipment.create(
            command.orderId(),
            command.userId(),
            command.trackingNumber(),
            command.carrier(),
            command.serviceType(),
            command.weight(),
            command.weightUnit(),
            command.shippingCost(),
            command.status(),
            command.estimatedDeliveryDate(),
            command.actualDeliveryDate(),
            command.originAddress(),
            command.originCity(),
            command.originState(),
            command.originCountry(),
            command.originPostalCode(),
            command.destinationAddress(),
            command.destinationCity(),
            command.destinationState(),
            command.destinationCountry(),
            command.destinationPostalCode(),
            command.notes(),
            command.labelUrl(),
            command.returnLabelUrl(),
            command.signatureRequired(),
            command.insuranceRequired(),
            command.declaredValue(),
            command.customsInfo(),
            command.customsValue(),
            command.customsCurrency()
        );
        return result;
    }
}
