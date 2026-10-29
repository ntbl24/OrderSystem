package io.order.backend.payment.infrastructure.provider;

import io.order.backend.payment.application.port.in.PaymentCallbackUseCase;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Translates the VNPay callback protocol at the provider boundary. */
@Component
public class VnPayCallbacks {
    private final VnPayProvider provider;
    private final PaymentCallbackUseCase payments;

    public VnPayCallbacks(VnPayProvider provider, PaymentCallbackUseCase payments) {
        this.provider = provider;
        this.payments = payments;
    }

    public Acknowledgment ipn(Map<String, List<String>> fields) {
        if (hasDuplicates(fields))
            return new Acknowledgment("97", "Duplicate fields");
        return switch (payments.ipn(provider.decodeNotification(singleValues(fields)))) {
            case INVALID_SIGNATURE -> new Acknowledgment("97", "Invalid signature");
            case NOT_FOUND -> new Acknowledgment("01", "Order not found");
            case INVALID_DATA -> new Acknowledgment("04", "Invalid payment data");
            case CONFLICTING_TRANSACTION -> new Acknowledgment("04", "Conflicting transaction");
            case ALREADY_CONFIRMED -> new Acknowledgment("02", "Order already confirmed");
            case CONFIRMED -> new Acknowledgment("00", "Confirm success");
        };
    }

    public PaymentCallbackUseCase.ReturnView returnView(Map<String, List<String>> fields) {
        if (hasDuplicates(fields))
            return new PaymentCallbackUseCase.ReturnView(false, "INVALID", null);
        return payments.returnView(provider.decodeNotification(singleValues(fields)));
    }

    private static boolean hasDuplicates(Map<String, List<String>> fields) {
        return fields.values().stream().anyMatch(values -> values.size() != 1);
    }

    private static Map<String, String> singleValues(Map<String, List<String>> fields) {
        return fields.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFirst()));
    }

    public record Acknowledgment(String RspCode, String Message) {
    }
}
