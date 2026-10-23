package io.order.backend.shipping.infrastructure.web.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.order.backend.common.response.ApiResponse;
import io.order.backend.shipping.application.port.in.ShipmentUseCase;
import io.order.backend.shipping.infrastructure.web.dto.ShipmentRequest;
import io.order.backend.shipping.infrastructure.web.dto.ShipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentUseCase shipmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentService.createShipment(request.toCommand()))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipment(
            @PathVariable String id,
            @Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentService.updateShipment(id, request.toCommand()))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable String id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(@PathVariable String id) {
        return shipmentService.getShipmentById(id)
                .map(ShipmentResponse::from)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrderId(@PathVariable String orderId) {
        return shipmentService.getShipmentByOrderId(orderId)
                .map(ShipmentResponse::from)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.getShipmentByTrackingNumber(trackingNumber)
                .map(ShipmentResponse::from)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<ApiResponse<ShipmentResponse>> calculateShippingCost(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentService.calculateShippingCost(request.toCommand()))));
    }

    @PostMapping("/{id}/generate-label")
    public ResponseEntity<ApiResponse<ShipmentResponse>> generateShippingLabel(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentService.generateShippingLabel(id))));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentService.updateShipmentStatus(id, status))));
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateTrackingInfo(
            @PathVariable String id,
            @RequestParam String trackingNumber,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentService.updateTrackingInfo(id, trackingNumber, status))));
    }
}
