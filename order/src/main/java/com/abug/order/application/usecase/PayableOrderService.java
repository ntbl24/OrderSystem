package com.abug.order.application.usecase;

import com.abug.order.application.port.in.PayableOrderUseCase;
import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.common.exception.AppException;
import com.abug.order.common.exception.ErrorCode;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PayableOrderService implements PayableOrderUseCase {
    private final OrderRepository orders;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public PayableOrder lockForPayment(OrderId id, UserId buyerId) {
        var order = orders.lockById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getUserId().equals(buyerId))
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        if (order.expire(Instant.now()))
            throw new AppException(ErrorCode.BAD_REQUEST, "Order is not payable");
        return new PayableOrder(order.getId(), order.getUserId(), order.getTotal(), "VND", order.getExpiresAt());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void lockForCallback(OrderId id) {
        orders.lockById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }
}
