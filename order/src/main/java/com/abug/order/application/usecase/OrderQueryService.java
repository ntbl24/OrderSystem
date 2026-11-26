package com.abug.order.application.usecase;

import com.abug.order.application.port.in.OrderQueryUseCase;
import com.abug.order.common.exception.AppException;
import com.abug.order.common.exception.ErrorCode;
import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.valueobject.OrderNumber;
import com.abug.order.application.port.out.OrderRepository;

import com.abug.order.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService implements OrderQueryUseCase {
    private final OrderRepository orders;
    private final OrderAccessService access;


    public Order getOrderById(OrderId id) {
        return access.owned(orders.findById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND)));
    }

    public Order getOrderByOrderNumber(OrderNumber number) {
        return access.owned(
                orders.findByOrderNumber(number).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND)));
    }

    public List<Order> getOrdersByUserId(UserId userId) {
        access.owner(userId);
        return orders.findByUserId(userId);
    }
}
