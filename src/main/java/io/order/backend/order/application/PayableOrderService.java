package io.order.backend.order.application;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.application.port.in.PayableOrderUseCase;
import io.order.backend.order.application.port.out.OrderRepository;
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
    public PayableOrder lockForPayment(String id, String buyerId) {
        var order = orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUserId().equals(buyerId))
            throw new ResourceNotFoundException("Order not found");
        if (!order.unpaid() || !order.getExpiresAt().isAfter(Instant.now()))
            throw new IllegalStateException("Order is not payable");
        return new PayableOrder(order.getId(), order.getUserId(), order.getTotal(), "VND", order.getExpiresAt());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void lockForCallback(String id) {
        orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
