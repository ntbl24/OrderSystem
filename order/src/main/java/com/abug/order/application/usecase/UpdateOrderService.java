package com.abug.order.application.usecase;

import com.abug.order.application.port.in.UpdateOrderUseCase;
import com.abug.order.application.port.out.ProductStockPort;
import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.common.exception.AppException;
import com.abug.order.common.exception.ErrorCode;
import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrderService implements UpdateOrderUseCase {
    private final OrderRepository orders;
    private final ProductStockPort inventory;
    private final OrderAccessService access;

    @Transactional
    public Order cancelOrder(OrderId id) {
        Order order = access.owned(orders.lockById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND)));
        order.cancel();
        InventorySettlement.settle(order, inventory, ReservationStatus.RELEASED);
        return orders.save(order);
    }
}
