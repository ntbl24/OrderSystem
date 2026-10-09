package io.order.backend.payment.provider;

import org.springframework.stereotype.Service;

import io.order.backend.common.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFactory {

    private final VnPayProvider vnPayProvider;
    
    public ProviderPayment getProvider(String provider) {
        return switch (provider.toUpperCase()) {
            case "VNPAY" -> vnPayProvider;
            default -> throw new InvalidInputException("Unsupported payment provider");
        };
    }
}
