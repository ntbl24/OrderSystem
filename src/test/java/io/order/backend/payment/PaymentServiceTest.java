package io.order.backend.payment;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.idempotency.IdempotencyService;
import io.order.backend.common.messaging.OutboxService;
import io.order.backend.payment.application.PaymentService;
import io.order.backend.payment.application.port.*;
import io.order.backend.payment.domain.Payment;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock CurrentBuyerPort buyer; @Mock IdempotencyService keys; @Mock OutboxService outbox;
    @Mock PayableOrderPort orders; @Mock PaymentStore store; @Mock PaymentGateway gateway;
    PaymentService service;
    @BeforeEach void setup() { service=new PaymentService(buyer,keys,orders,store,gateway,outbox,JsonMapper.builder().build()); }
    @Test void ipn_invalidSignature_doesNotReadOrMutatePayments() {
        assertThat(service.ipn(new PaymentNotification(false,false,null,null,null,false,null))).isEqualTo(PaymentUseCase.NotificationResult.INVALID_SIGNATURE); verifyNoInteractions(store,orders,outbox);
    }
    @Test void ipn_invalidAmount_doesNotLockOrMutate() {
        var f=new PaymentNotification(true,false,"ref",null,null,false,null);
        when(store.byReference("ref")).thenReturn(Optional.of(PaymentDomainTest.pending(BigDecimal.TEN)));
        assertThat(service.ipn(f)).isEqualTo(PaymentUseCase.NotificationResult.INVALID_DATA); verifyNoInteractions(orders,outbox); verify(store,never()).save(any());
    }
    @Test void ipn_lateSuccess_persistsBeforeEventAndLocksOrderFirst() {
        var p=PaymentDomainTest.pending(BigDecimal.TEN).observe(false,null);
        var f=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        when(store.byReference("ref")).thenReturn(Optional.of(p)); when(store.lock(p.id())).thenReturn(p);
        assertThat(service.ipn(f)).isEqualTo(PaymentUseCase.NotificationResult.CONFIRMED);
        var sequence=inOrder(orders,store,outbox); sequence.verify(store).byReference("ref"); sequence.verify(orders).lockForCallback("order");
        sequence.verify(store).lock("payment"); sequence.verify(store).save(argThat(v->v.status()==Payment.Status.SUCCEEDED));
        sequence.verify(outbox).enqueue(eq("PAYMENT_SUCCEEDED"),eq("payment"),contains("\"orderId\":\"order\""));
    }
    @Test void ipn_unknownReference_returnsNotFoundBeforeDataValidation() {
        var notification=new PaymentNotification(true,false,"missing",null,null,false,null);
        when(store.byReference("missing")).thenReturn(Optional.empty());
        assertThat(service.ipn(notification)).isEqualTo(PaymentUseCase.NotificationResult.NOT_FOUND);
        verifyNoInteractions(orders,outbox);
    }
    @Test void ipn_mismatchedAmountCurrencyOrReference_doesNotMutate() {
        var p=PaymentDomainTest.pending(BigDecimal.TEN);
        when(store.byReference(anyString())).thenReturn(Optional.of(p));
        for(var notification:List.of(
                new PaymentNotification(true,true,"ref",BigDecimal.ONE,"VND",true,"123"),
                new PaymentNotification(true,true,"ref",BigDecimal.TEN,"USD",true,"123"),
                new PaymentNotification(true,true,"other",BigDecimal.TEN,"VND",true,"123"))) {
            assertThat(service.ipn(notification)).isEqualTo(PaymentUseCase.NotificationResult.INVALID_DATA);
        }
        verifyNoInteractions(orders,outbox); verify(store,never()).save(any());
    }
    @Test void returnView_validNotification_reportsPersistedStateWithoutSettlement() {
        var p=PaymentDomainTest.pending(BigDecimal.TEN);
        var notification=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        when(store.byReference("ref")).thenReturn(Optional.of(p));
        assertThat(service.returnView(notification)).isEqualTo(new PaymentUseCase.ReturnView(true,"PENDING",
                "Payment confirmation is processed through IPN, not this page"));
        verifyNoInteractions(orders,outbox); verify(store,never()).save(any());
    }
    @Test void ipn_conflictingTransaction_doesNotMutate() {
        var p=PaymentDomainTest.pending(BigDecimal.TEN).observe(true,"123");
        when(store.byReference("ref")).thenReturn(Optional.of(p)); when(store.lock(p.id())).thenReturn(p);
        assertThat(service.ipn(new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"456")))
                .isEqualTo(PaymentUseCase.NotificationResult.CONFLICTING_TRANSACTION);
        verifyNoInteractions(outbox); verify(store,never()).save(any());
    }
    @Test void ipn_duplicateSuccess_doesNotEmitAgain() {
        var p=PaymentDomainTest.pending(BigDecimal.TEN).observe(true,"123");
        var f=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        when(store.byReference("ref")).thenReturn(Optional.of(p)); when(store.lock(p.id())).thenReturn(p);
        assertThat(service.ipn(f)).isEqualTo(PaymentUseCase.NotificationResult.ALREADY_CONFIRMED); verifyNoInteractions(outbox); verify(store,never()).save(any());
    }
}
