package io.order.backend.payment;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.common.application.port.OutboxPort;
import io.order.backend.payment.application.usecase.*;
import io.order.backend.payment.application.port.out.*;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.domain.PaymentId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.*;
import tools.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PaymentWorkerTest {
    private static final PaymentId PAYMENT_ID = PaymentId.of("payment");
    @Mock PaymentRepository store; @Mock PayableOrderPort orders; @Mock PaymentGateway gateway;
    @Mock PlatformTransactionManager manager; @Mock TransactionStatus txStatus;
    @Mock CurrentBuyerPort buyer; @Mock IdempotencyPort keys; @Mock OutboxPort outbox;
    PaymentWorker worker;
    @BeforeEach void setup() {
        when(manager.getTransaction(any())).thenReturn(txStatus);
        worker=new PaymentWorker(store,orders,gateway,new PaymentCallbackService(orders,store,outbox,JsonMapper.builder().build()),outbox,JsonMapper.builder().build(),manager);
    }
    private Payment refund(Payment.RefundStatus state) {
        var p=PaymentTestFixtures.pending(BigDecimal.TEN).observe(true,"123").requestRefund("stable","admin","reason",Instant.now()).refundState(Payment.RefundStatus.DISPATCHING);
        return state==Payment.RefundStatus.DISPATCHING?p:p.refundState(state);
    }
    private void claim(Payment p,int attempts,boolean abandoned) {
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(p)); when(store.claim(eq(PAYMENT_ID),any())).thenReturn(new PaymentRepository.Work(p,"token",attempts,abandoned));
        when(store.lock(PAYMENT_ID)).thenReturn(p); when(store.ownsLease(PAYMENT_ID,"token")).thenReturn(true);
    }
    @Test void refund_requestedIntent_isDispatchedAndSavedBeforeCallingProvider() {
        var p = PaymentTestFixtures.pending(BigDecimal.TEN).observe(true, "123")
                .requestRefund("stable", "admin", "reason", Instant.now());
        claim(p, 1, false);
        when(gateway.refund(p)).thenReturn(PaymentGateway.RefundOutcome.ACCEPTED);
        worker.process(PAYMENT_ID);
        var sequence = inOrder(store, gateway);
        sequence.verify(store).find(PAYMENT_ID);
        sequence.verify(store).claim(eq(PAYMENT_ID), any());
        sequence.verify(store).save(p);
        sequence.verify(gateway).refund(p);
        verify(gateway, never()).query(any());
    }
    @Test void refund_exhaustedAbandonedLease_requiresReviewWithoutProviderCall() {
        var p = refund(Payment.RefundStatus.DISPATCHING);
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(p));
        when(store.claim(eq(PAYMENT_ID), any())).thenReturn(new PaymentRepository.Work(p, "token", 9, true));
        worker.process(PAYMENT_ID);
        org.assertj.core.api.Assertions.assertThat(p.getRefundStatus()).isEqualTo(Payment.RefundStatus.MANUAL_REVIEW);
        verify(store).save(p);
        verify(store).complete(eq(PAYMENT_ID), eq("token"), any(), eq("MANUAL_REVIEW_EXHAUSTED_LEASE"));
        verifyNoInteractions(gateway, outbox);
    }
    @Test void process_unclaimedPayment_neverCallsProvider() {
        var p = PaymentTestFixtures.pending(BigDecimal.TEN);
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(p));
        worker.process(PAYMENT_ID);
        verify(store).claim(eq(PAYMENT_ID), any());
        verifyNoInteractions(gateway, outbox);
        verify(store, never()).save(any());
    }
    @Test void process_staleLease_doesNotPersistProviderResult() {
        var p = PaymentTestFixtures.pending(BigDecimal.TEN);
        claim(p, 1, false);
        when(store.ownsLease(PAYMENT_ID, "token")).thenReturn(false);
        when(gateway.query(p)).thenReturn(new PaymentGateway.Observation(PaymentGateway.Observation.State.SUCCEEDED, "123"));
        worker.process(PAYMENT_ID);
        verify(store, never()).save(any());
        verify(store, never()).complete(any(PaymentId.class), anyString(), any(), anyString());
        verifyNoInteractions(outbox);
    }
    @Test void reconciliation_success_persistsSettlementAndCompletesLease() {
        var p = PaymentTestFixtures.pending(BigDecimal.TEN);
        claim(p, 2, false);
        when(gateway.query(p)).thenReturn(new PaymentGateway.Observation(PaymentGateway.Observation.State.SUCCEEDED, "123"));
        worker.process(PAYMENT_ID);
        verify(store).save(argThat(v -> v.getStatus() == Payment.Status.SUCCEEDED && v.getProviderTransactionNo().equals("123")));
        verify(outbox).enqueue(eq("PAYMENT_SUCCEEDED"), eq(PAYMENT_ID.toString()), anyString());
        verify(store).complete(eq(PAYMENT_ID), eq("token"), any(), eq("SUCCEEDED"));
        verify(gateway, never()).refund(any());
    }
    @Test void refund_abandonedLease_reconcilesWithoutResending() {
        var p=refund(Payment.RefundStatus.DISPATCHING); claim(p,8,true);
        when(gateway.query(p)).thenReturn(new PaymentGateway.Observation(PaymentGateway.Observation.State.UNKNOWN,null));
        worker.process(PAYMENT_ID); verify(gateway,never()).refund(any());
        verify(store).save(argThat(v->v.getRefundStatus()==Payment.RefundStatus.MANUAL_REVIEW)); verifyNoInteractions(outbox);
    }
    @Test void refund_acceptedRequest_doesNotEmitRefunded() {
        var p=refund(Payment.RefundStatus.DISPATCHING); claim(p,1,false);
        when(gateway.refund(p)).thenReturn(PaymentGateway.RefundOutcome.ACCEPTED);
        worker.process(PAYMENT_ID); verify(store).save(argThat(v->v.getRefundStatus()==Payment.RefundStatus.ACCEPTED)); verifyNoInteractions(outbox);
    }
    @Test void refund_verifiedFullRefund_persistsBeforeEvent() {
        var p=refund(Payment.RefundStatus.ACCEPTED); claim(p,2,false);
        when(gateway.query(p)).thenReturn(new PaymentGateway.Observation(PaymentGateway.Observation.State.REFUNDED,"123"));
        worker.process(PAYMENT_ID); var sequence=inOrder(store,outbox);
        sequence.verify(store).find(PAYMENT_ID); sequence.verify(store).claim(eq(PAYMENT_ID),any()); sequence.verify(store).lock(PAYMENT_ID); sequence.verify(store).ownsLease(PAYMENT_ID,"token");
        sequence.verify(store).save(argThat(v->v.getRefundStatus()==Payment.RefundStatus.REFUNDED)); sequence.verify(outbox).enqueue(eq("PAYMENT_REFUNDED"),eq(PAYMENT_ID.toString()),anyString());
    }
}
