package io.order.backend.order.api;

import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.dto.OrderDTO;
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
    public ApiResponse<OrderDTO> get(@PathVariable String id) {
        return ApiResponse.success(service.getOrderById(id));
    }

    @GetMapping("/number/{number}")
    public ApiResponse<OrderDTO> byNumber(@PathVariable String number) {
        return ApiResponse.success(service.getOrderByOrderNumber(number));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderDTO>> list(@PathVariable String userId) {
        return ApiResponse.success(service.getOrdersByUserId(userId));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderDTO> cancel(@PathVariable String id) {
        return ApiResponse.success(service.cancel(id));
    }
}
