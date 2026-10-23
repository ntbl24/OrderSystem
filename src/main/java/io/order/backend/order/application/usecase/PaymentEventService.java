package io.order.backend.order.application.usecase;

import io.order.backend.order.application.port.in.PaymentEventUseCase;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.application.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentEventService implements PaymentEventUseCase {
    private final OrderRepository orders;
    private final InventoryPort inventory;

    @Override
    @Transactional
    public void paymentSucceeded(PaymentEvent event) {
        var order = orders.lockById(event.orderId()).orElseThrow();
        if (order.acceptPayment(event.paymentId(), event.amount(), event.currency(), Instant.now()))
            InventorySettlement.settle(order, inventory, "CONSUMED");
        else if ("PAYMENT_REVIEW".equals(order.getOrderStatus()) && "HELD".equals(order.getReservationStatus()))
            InventorySettlement.settle(order, inventory, "RELEASED");
        orders.save(order);
    }

    @Override
    @Transactional
    public void paymentRefunded(PaymentEvent event) {
        var order = orders.lockById(event.orderId()).orElseThrow();
        if (order.acceptRefund(event.paymentId(), event.amount(), event.currency())
                && "CONSUMED".equals(order.getReservationStatus()))
            InventorySettlement.settle(order, inventory, "RETURNED");
        orders.save(order);
    }
}
