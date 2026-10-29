package io.order.backend.payment.application.usecase;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@lombok.RequiredArgsConstructor
public class PaymentQueryService implements PaymentQueryUseCase {
    private final CurrentBuyerPort buyer;
    private final PaymentRepository store;

    @Transactional(readOnly = true)
    public PaymentResult get(String id) {
        var p = store.find(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (!p.getBuyerId().equals(buyer.buyerId()))
            throw new org.springframework.security.access.AccessDeniedException("Payment does not belong to buyer");
        return PaymentResult.from(p, null);
    }

}
