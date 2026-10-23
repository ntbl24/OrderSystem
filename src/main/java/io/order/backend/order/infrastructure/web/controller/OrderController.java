package io.order.backend.order.infrastructure.web.controller;

import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.infrastructure.web.dto.OrderResponse;
import io.order.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderUseCase service;

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> get(@PathVariable String id) {
        return ApiResponse.success(OrderResponse.from(service.getOrderById(id)));
    }

    @GetMapping("/number/{number}")
    public ApiResponse<OrderResponse> byNumber(@PathVariable String number) {
        return ApiResponse.success(OrderResponse.from(service.getOrderByOrderNumber(number)));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> list(@PathVariable String userId) {
        return ApiResponse.success(service.getOrdersByUserId(userId).stream().map(OrderResponse::from).toList());
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancel(@PathVariable String id) {
        return ApiResponse.success(OrderResponse.from(service.cancel(id)));
    }
}
