package com.abug.payment.application.usecase;

import java.time.Instant;
import com.abug.payment.application.port.out.PaymentGateway;

import com.abug.payment.common.exception.AppException;
import com.abug.payment.common.exception.ErrorCode;
import com.abug.payment.domain.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abug.common.application.port.CurrentBuyerPort;
import com.abug.common.application.port.IdempotencyPort;
import com.abug.payment.application.port.in.CreatePaymentCommand;
import com.abug.payment.application.port.in.CreatePaymentUsecase;
import com.abug.payment.application.port.in.PaymentResult;
import com.abug.payment.application.port.out.PayableOrderPort;
import com.abug.payment.application.port.out.PaymentRepository;
import com.abug.payment.domain.Payment;
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
                throw new AppException(ErrorCode.CONFLICT, "Order already paid");
            var payment = Payment.create(command.orderId(), UserId.of(buyerId), order.amount(), order.currency(),
                    Instant.now(), order.expiresAt());
            store.save(payment);
            return PaymentResult.from(payment, gateway.paymentUrl(payment, command.ipAddress()));
        });
    }
}
