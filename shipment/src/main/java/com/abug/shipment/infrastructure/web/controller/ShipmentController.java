package com.abug.shipment.infrastructure.web.controller;


import com.abug.shipment.common.response.ApiResponse;
import com.abug.shipment.domain.OrderId;
import com.abug.shipment.infrastructure.web.dto.ShipmentResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abug.shipment.application.port.in.CreateShipmentUseCase;
import com.abug.shipment.application.port.in.ShipmentCommandUseCase;
import com.abug.shipment.application.port.in.ShipmentQueryUseCase;
import com.abug.shipment.application.port.in.UpdateShipmentUseCase;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.domain.TrackingNumber;
import com.abug.shipment.infrastructure.web.dto.ShipmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentCommandUseCase shipmentCommandUseCase;
    private final CreateShipmentUseCase createShipmentUseCase;
    private final ShipmentQueryUseCase shipmentQueryUseCase;
    private final UpdateShipmentUseCase updateShipmentUseCase;

    @PostMapping
    public ApiResponse<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        return ApiResponse.success(ShipmentResponse.from(createShipmentUseCase.createShipment(request.toCommand())));
    }

    @PutMapping("/{id}")
    public ApiResponse<ShipmentResponse> updateShipment(
            @PathVariable String id,
            @Valid @RequestBody ShipmentRequest request) {
        return ApiResponse.success(ShipmentResponse.from(updateShipmentUseCase.updateShipment(ShipmentId.of(id), request.toCommand())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShipment(@PathVariable String id) {
        shipmentCommandUseCase.deleteShipment(ShipmentId.of(id));
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ShipmentResponse> getShipmentById(@PathVariable String id) {
        return ApiResponse.success(ShipmentResponse.from(shipmentQueryUseCase.getShipmentById(ShipmentId.of(id))));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<ShipmentResponse> getShipmentByOrderId(@PathVariable String orderId) {
        return ApiResponse.success(ShipmentResponse.from(shipmentQueryUseCase.getShipmentByOrderId(OrderId.of(orderId))));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ApiResponse<ShipmentResponse> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return ApiResponse.success((ShipmentResponse.from(shipmentQueryUseCase.getShipmentByTrackingNumber(TrackingNumber.of(trackingNumber)))));
    }

    @PostMapping("/calculate-cost")
    public ApiResponse<ShipmentResponse> calculateShippingCost(@Valid @RequestBody ShipmentRequest request) {
        return ApiResponse.success(ShipmentResponse.from(shipmentCommandUseCase.calculateShippingCost(request.toCommand())));
    }

    @PostMapping("/{id}/generate-label")
    public ApiResponse<ShipmentResponse>generateShippingLabel(@PathVariable String id) {
        return ApiResponse.success(ShipmentResponse.from(shipmentCommandUseCase.generateShippingLabel(ShipmentId.of(id))));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ShipmentResponse> updateShipmentStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ApiResponse.success(ShipmentResponse.from(updateShipmentUseCase.updateShipmentStatus(ShipmentId.of(id), status)));
    }

    @PutMapping("/{id}/tracking")
    public ApiResponse<ShipmentResponse> updateTrackingInfo(
            @PathVariable String id,
            @RequestParam String trackingNumber,
            @RequestParam String status) {
        return ApiResponse.success(ShipmentResponse.from(updateShipmentUseCase.updateTrackingInfo(ShipmentId.of(id), trackingNumber, status)));
    }
}
