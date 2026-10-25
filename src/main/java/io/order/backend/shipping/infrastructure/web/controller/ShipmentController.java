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
import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.application.port.in.CreateShipmentUseCase;
import io.order.backend.shipping.application.port.in.ShipmentCommandUseCase;
import io.order.backend.shipping.application.port.in.ShipmentQueryUseCase;
import io.order.backend.shipping.application.port.in.UpdateShipmentUseCase;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;
import io.order.backend.shipping.infrastructure.web.dto.ShipmentRequest;
import io.order.backend.shipping.infrastructure.web.dto.ShipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentCommandUseCase shipmentCommandUseCase;
    private final CreateShipmentUseCase createShipmentUseCase;
    private final ShipmentQueryUseCase shipmentQueryUseCase;
    private final UpdateShipmentUseCase updateShipmentUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(createShipmentUseCase.createShipment(request.toCommand()))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipment(
            @PathVariable String id,
            @Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(updateShipmentUseCase.updateShipment(ShipmentId.of(id), request.toCommand()))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable String id) {
        shipmentCommandUseCase.deleteShipment(ShipmentId.of(id));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(@PathVariable String id) {
        return shipmentQueryUseCase.getShipmentById(ShipmentId.of(id))
                .map(ShipmentResponse::from)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrderId(@PathVariable String orderId) {
        return shipmentQueryUseCase.getShipmentByOrderId(OrderId.of(orderId))
                .map(ShipmentResponse::from)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentQueryUseCase.getShipmentByTrackingNumber(TrackingNumber.of(trackingNumber))
                .map(ShipmentResponse::from)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<ApiResponse<ShipmentResponse>> calculateShippingCost(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentCommandUseCase.calculateShippingCost(request.toCommand()))));
    }

    @PostMapping("/{id}/generate-label")
    public ResponseEntity<ApiResponse<ShipmentResponse>> generateShippingLabel(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(shipmentCommandUseCase.generateShippingLabel(ShipmentId.of(id)))));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(updateShipmentUseCase.updateShipmentStatus(ShipmentId.of(id), status))));
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateTrackingInfo(
            @PathVariable String id,
            @RequestParam String trackingNumber,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(ShipmentResponse.from(updateShipmentUseCase.updateTrackingInfo(ShipmentId.of(id), trackingNumber, status))));
    }
}
