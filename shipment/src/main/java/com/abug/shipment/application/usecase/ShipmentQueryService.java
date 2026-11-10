package com.abug.shipment.application.usecase;

import java.util.Optional;

import com.abug.shipment.common.exception.AppException;
import com.abug.shipment.common.exception.ErrorCode;
import com.abug.shipment.domain.OrderId;
import org.springframework.stereotype.Service;

import com.abug.shipment.application.port.in.ShipmentQueryUseCase;
import com.abug.shipment.application.port.out.ShipmentRepository;
import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.domain.TrackingNumber;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentQueryService implements ShipmentQueryUseCase {
    private final ShipmentRepository shipmentRepository;

    public Shipment getShipmentById(ShipmentId id){
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

    public Shipment getShipmentByOrderId(OrderId orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

    public Shipment getShipmentByTrackingNumber(TrackingNumber trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

}
