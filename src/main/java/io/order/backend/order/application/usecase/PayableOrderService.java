package io.order.backend.order.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.application.port.in.PayableOrderUseCase;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.OrderId;
import io.order.backend.user.domain.UserId;
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
        var order = orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUserId().equals(buyerId))
            throw new ResourceNotFoundException("Order not found");
        if (order.expire(Instant.now()))
            throw new IllegalStateException("Order is not payable");
        return new PayableOrder(order.getId(), order.getUserId(), order.getTotal(), "VND", order.getExpiresAt());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void lockForCallback(OrderId id) {
        orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
