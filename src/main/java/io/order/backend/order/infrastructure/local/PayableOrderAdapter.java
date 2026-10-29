package io.order.backend.order.infrastructure.local;

import io.order.backend.payment.application.port.out.PayableOrderPort;
import io.order.backend.user.domain.UserId;
import io.order.backend.order.application.port.in.PayableOrderUseCase;
import io.order.backend.order.domain.OrderId;

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
