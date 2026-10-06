package io.order.backend.order.api;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.order.backend.common.response.ApiResponse;
import io.order.backend.order.application.OrderService;
import io.order.backend.order.dto.OrderDTO;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        ApiResponse<OrderDTO> response = ApiResponse.success(orderService.createOrder(orderDTO));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable String id) {
        ApiResponse<OrderDTO> response = ApiResponse.success(orderService.getOrderById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByOrderNumber(@PathVariable String orderNumber) {
        ApiResponse<OrderDTO> response = ApiResponse.success(orderService.getOrderByOrderNumber(orderNumber));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByUserId(@PathVariable String userId) {
        ApiResponse<List<OrderDTO>> response = ApiResponse.success(orderService.getOrdersByUserId(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByStatus(@PathVariable String orderStatus) {
        ApiResponse<List<OrderDTO>> response = ApiResponse.success(orderService.getOrdersByStatus(orderStatus));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable String id,
            @RequestParam String orderStatus) {
        ApiResponse<OrderDTO> response = ApiResponse.success(orderService.updateOrderStatus(id, orderStatus));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody OrderDTO orderDTO) {
        ApiResponse<OrderDTO> response = ApiResponse.success(orderService.updateOrder(id, orderDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok(response);
    }
}
