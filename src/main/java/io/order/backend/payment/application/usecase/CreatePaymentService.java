package io.order.backend.payment.application.usecase;

import java.time.Instant;
import io.order.backend.payment.application.port.out.PaymentGateway;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.payment.application.port.in.CreatePaymentCommand;
import io.order.backend.payment.application.port.in.CreatePaymentUsecase;
import io.order.backend.payment.application.port.in.PaymentResult;
import io.order.backend.payment.application.port.out.PayableOrderPort;
import io.order.backend.payment.application.port.out.PaymentRepository;
import io.order.backend.payment.domain.Payment;
import io.order.backend.user.domain.UserId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePaymentService implements CreatePaymentUsecase {
    private final CurrentBuyerPort buyer;
    private final PayableOrderPort orders;
    private final IdempotencyPort keys;
    private final PaymentRepository store;
    private final PaymentGateway gateway;

    @Override
    @Transactional
    public PaymentResult create(CreatePaymentCommand command) {
        String buyerId = buyer.buyerId();
        String orderId = command.orderId().toString();
        orders.lockForCallback(command.orderId());
        return keys.execute(buyerId, "PAYMENT_ATTEMPT", command.key(),
                java.util.Map.of("orderId", orderId), PaymentResult.class, () -> {
            var order = orders.lockForPayment(command.orderId(), UserId.of(buyerId));
            if (store.orderSettled(command.orderId()))
                throw new IllegalStateException("Order already paid");
            var payment = Payment.create(command.orderId(), UserId.of(buyerId), order.amount(), order.currency(),
                    Instant.now(), order.expiresAt());
            store.save(payment);
            return PaymentResult.from(payment, gateway.paymentUrl(payment, command.ipAddress()));
        });
    }
}
