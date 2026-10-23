package io.order.backend.payment;

import io.order.backend.payment.application.port.in.PaymentWorkUseCase;
import io.order.backend.payment.application.port.in.RefundIntentUseCase;
import io.order.backend.payment.application.port.out.PayableOrderPort;
import io.order.backend.payment.application.port.out.PaymentStore;
import io.order.backend.payment.application.usecase.RefundIntentService;
import io.order.backend.payment.infrastructure.messaging.RefundIntentHandler;
import io.order.backend.payment.infrastructure.scheduling.PaymentScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentAdaptersTest {
    @Mock PaymentWorkUseCase work;
    @Mock RefundIntentUseCase refunds;
    @Mock PaymentStore store;
    @Mock PayableOrderPort orders;

    @Test
    void scheduler_delegatesOneTick() {
        new PaymentScheduler(work).tick();
        verify(work).tick();
        verifyNoMoreInteractions(work);
    }

    @Test
    void refundHandler_acknowledgesDurableAggregateId() {
        var handler = new RefundIntentHandler(refunds);
        assertThat(handler.eventType()).isEqualTo("PAYMENT_REFUND_REQUESTED");
        handler.handle("event", "payment", "{\"paymentId\":\"payment\"}");
        verify(refunds).acknowledge("payment");
        verifyNoMoreInteractions(refunds);
    }

    @Test
    void refundIntent_preservesOrderBeforePaymentLockWithoutMutation() {
        var payment = PaymentDomainTest.pending(BigDecimal.TEN);
        when(store.find("payment")).thenReturn(Optional.of(payment));
        new RefundIntentService(store, orders).acknowledge("payment");
        var sequence = inOrder(store, orders);
        sequence.verify(store).find("payment");
        sequence.verify(orders).lockForCallback("order");
        sequence.verify(store).lock("payment");
        verifyNoMoreInteractions(store, orders);
    }
}
