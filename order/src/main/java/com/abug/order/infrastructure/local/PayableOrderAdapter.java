package com.abug.order.infrastructure.local;

import com.abug.order.application.port.in.PayableOrderUseCase;
import com.abug.order.domain.valueobject.OrderId;

import com.abug.order.domain.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PayableOrderAdapter implements PayableOrderPort {
    private final PayableOrderUseCase orders;

    @Transactional(propagation = Propagation.MANDATORY)
    public PayableOrderUseCase.PayableOrder lockForPayment(OrderId id, UserId buyerId) {
        var order = orders.lockForPayment(id, buyerId);
        return new PayableOrderUseCase.PayableOrder(order.id(), order.buyerId(), order.amount(), order.currency(), order.expiresAt());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void lockForCallback(OrderId id) {
        orders.lockForCallback(id);
    }
}
