package io.order.backend.order.application.port.in;

import io.order.backend.order.dto.OrderDTO;
import java.util.List;

public interface OrderUseCase {
    OrderDTO getOrderById(String id);
    OrderDTO getOrderByOrderNumber(String number);
    List<OrderDTO> getOrdersByUserId(String userId);
    OrderDTO cancel(String id);
}
