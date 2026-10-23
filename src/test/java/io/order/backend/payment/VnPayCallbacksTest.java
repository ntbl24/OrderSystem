package io.order.backend.payment;

import io.order.backend.payment.application.port.in.PaymentNotification;
import io.order.backend.payment.application.port.in.PaymentUseCase;
import io.order.backend.payment.infrastructure.provider.VnPayCallbacks;
import io.order.backend.payment.infrastructure.provider.VnPayProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VnPayCallbacksTest {
    @Mock VnPayProvider provider;
    @Mock PaymentUseCase payments;
    VnPayCallbacks callbacks;

    @BeforeEach void setup() { callbacks = new VnPayCallbacks(provider, payments); }

    @Test void duplicateFields_rejectBeforeSignatureOrApplication() {
        var fields = Map.of("vnp_TxnRef", List.of("one", "two"));
        assertThat(callbacks.ipn(fields)).isEqualTo(new VnPayCallbacks.Acknowledgment("97", "Duplicate fields"));
        assertThat(callbacks.returnView(fields)).isEqualTo(new PaymentUseCase.ReturnView(false, "INVALID", null));
        verifyNoInteractions(provider, payments);
    }

    @Test void ipn_applicationResults_preserveProtocolCodesAndMessages() {
        var notification = new PaymentNotification(true, true, "ref", null, "VND", true, "123");
        when(provider.decodeNotification(Map.of())).thenReturn(notification);
        var results = PaymentUseCase.NotificationResult.values();
        var expected = List.of(
                new VnPayCallbacks.Acknowledgment("97", "Invalid signature"),
                new VnPayCallbacks.Acknowledgment("01", "Order not found"),
                new VnPayCallbacks.Acknowledgment("04", "Invalid payment data"),
                new VnPayCallbacks.Acknowledgment("04", "Conflicting transaction"),
                new VnPayCallbacks.Acknowledgment("02", "Order already confirmed"),
                new VnPayCallbacks.Acknowledgment("00", "Confirm success"));
        for (int i = 0; i < results.length; i++) {
            when(payments.ipn(notification)).thenReturn(results[i]);
            assertThat(callbacks.ipn(Map.of())).isEqualTo(expected.get(i));
        }
    }

    @Test void returnView_decodedNotification_isPassedToReadOnlyUseCase() {
        var notification = new PaymentNotification(false, false, null, null, null, false, null);
        when(provider.decodeNotification(Map.of("vnp_TxnRef", "ref"))).thenReturn(notification);
        var expected = new PaymentUseCase.ReturnView(false, "INVALID", null);
        when(payments.returnView(notification)).thenReturn(expected);
        assertThat(callbacks.returnView(Map.of("vnp_TxnRef", List.of("ref")))).isEqualTo(expected);
        verify(payments, never()).ipn(any());
    }
}
