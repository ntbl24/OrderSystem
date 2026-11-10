package com.abug.payment.application.usecase;

import com.abug.common.application.port.CurrentBuyerPort;
import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.payment.application.port.in.*;
import com.abug.payment.application.port.out.*;
import com.abug.payment.domain.PaymentId;

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
