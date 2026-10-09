package io.order.backend.shipping.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import io.order.backend.shipping.application.ShipmentService;
import io.order.backend.shipping.dto.ShipmentDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentDTO>> createShipment(@Valid @RequestBody ShipmentDTO shipmentDTO) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.createShipment(shipmentDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateShipment(
            @PathVariable String id,
            @Valid @RequestBody ShipmentDTO shipmentDTO) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.updateShipment(id, shipmentDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable String id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentById(@PathVariable String id) {
        return shipmentService.getShipmentById(id)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentByOrderId(@PathVariable String orderId) {
        return shipmentService.getShipmentByOrderId(orderId)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.getShipmentByTrackingNumber(trackingNumber)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<ApiResponse<ShipmentDTO>> calculateShippingCost(@Valid @RequestBody ShipmentDTO shipmentDTO) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.calculateShippingCost(shipmentDTO)));
    }

    @PostMapping("/{id}/generate-label")
    public ResponseEntity<ApiResponse<ShipmentDTO>> generateShippingLabel(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.generateShippingLabel(id)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateShipmentStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.updateShipmentStatus(id, status)));
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateTrackingInfo(
            @PathVariable String id,
            @RequestParam String trackingNumber,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.updateTrackingInfo(id, trackingNumber, status)));
    }
}
