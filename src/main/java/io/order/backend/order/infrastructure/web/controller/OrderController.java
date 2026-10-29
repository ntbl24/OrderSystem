package io.order.backend.order.infrastructure.web.controller;

import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderNumber;
import io.order.backend.order.infrastructure.web.dto.OrderResponse;
import io.order.backend.user.domain.UserId;
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
        return ApiResponse.success(OrderResponse.from(service.getOrderById(OrderId.of(id))));
    }

    @GetMapping("/number/{number}")
    public ApiResponse<OrderResponse> byNumber(@PathVariable String number) {
        return ApiResponse.success(OrderResponse.from(service.getOrderByOrderNumber(OrderNumber.of(number))));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> list(@PathVariable String userId) {
        return ApiResponse.success(service.getOrdersByUserId(UserId.of(userId)).stream().map(OrderResponse::from).toList());
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancel(@PathVariable String id) {
        return ApiResponse.success(OrderResponse.from(service.cancel(OrderId.of(id))));
    }
}
