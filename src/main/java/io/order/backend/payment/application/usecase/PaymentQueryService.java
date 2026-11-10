package io.order.backend.payment.application.usecase;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
import io.order.backend.payment.domain.PaymentId;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@lombok.RequiredArgsConstructor
public class PaymentQueryService implements PaymentQueryUseCase {
    private final CurrentBuyerPort buyer;
    private final PaymentRepository store;

    @Transactional(readOnly = true)
    public PaymentResult get(PaymentId id) {
        var p = store.find(id).orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found"));
        if (!p.getBuyerId().toString().equals(buyer.buyerId()))
            throw new AccessDeniedException("Payment does not belong to buyer");
        return PaymentResult.from(p, null);
    }

}
