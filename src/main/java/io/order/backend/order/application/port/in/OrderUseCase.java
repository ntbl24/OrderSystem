package io.order.backend.order.application.port.in;

import java.util.List;

public interface OrderUseCase {
    OrderResult getOrderById(String id);
    OrderResult getOrderByOrderNumber(String number);
    List<OrderResult> getOrdersByUserId(String userId);
    OrderResult cancel(String id);
}
