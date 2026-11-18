package com.abug.order.infrastructure.web.controller;

import com.abug.order.application.port.in.OrderUseCase;
import com.abug.order.common.response.ApiResponse;
import com.abug.order.domain.OrderId;
import com.abug.order.domain.OrderNumber;
import com.abug.order.domain.UserId;
import com.abug.order.infrastructure.web.dto.OrderResponse;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
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
