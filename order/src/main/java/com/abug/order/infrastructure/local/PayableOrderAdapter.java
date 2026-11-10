package com.abug.order.infrastructure.local;

import com.abug.payment.application.port.out.PayableOrderPort;
import com.abug.user.domain.UserId;
import com.abug.order.application.port.in.PayableOrderUseCase;
import com.abug.order.domain.OrderId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PayableOrderAdapter implements PayableOrderPort {
    private final PayableOrderUseCase orders;

    @Transactional(propagation = Propagation.MANDATORY)
    public PayableOrder lockForPayment(OrderId id, UserId buyerId) {
        var order = orders.lockForPayment(id, buyerId);
        return new PayableOrder(order.id(), order.buyerId(), order.amount(), order.currency(), order.expiresAt());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void lockForCallback(OrderId id) {
        orders.lockForCallback(id);
    }
}
