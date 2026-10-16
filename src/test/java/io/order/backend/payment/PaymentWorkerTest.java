package io.order.backend.payment;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.idempotency.IdempotencyService;
import io.order.backend.common.messaging.OutboxService;
import io.order.backend.payment.application.*;
import io.order.backend.payment.application.port.*;
import io.order.backend.payment.domain.Payment;
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
    @Mock PaymentStore store; @Mock PayableOrderPort orders; @Mock PaymentGateway gateway;
    @Mock PlatformTransactionManager manager; @Mock TransactionStatus txStatus;
    @Mock CurrentBuyerPort buyer; @Mock IdempotencyService keys; @Mock OutboxService outbox;
    PaymentWorker worker;
    @BeforeEach void setup() {
        when(manager.getTransaction(any())).thenReturn(txStatus);
        worker=new PaymentWorker(store,orders,gateway,new PaymentService(buyer,keys,orders,store,gateway,outbox,JsonMapper.builder().build()),manager);
    }
    private Payment refund(Payment.RefundStatus state) {
        var p=PaymentDomainTest.pending(BigDecimal.TEN).observe(true,"123").requestRefund("stable","admin","reason",Instant.now()).refundState(Payment.RefundStatus.DISPATCHING);
        return state==Payment.RefundStatus.DISPATCHING?p:p.refundState(state);
    }
    private void claim(Payment p,int attempts,boolean abandoned) {
        when(store.find("payment")).thenReturn(Optional.of(p)); when(store.claim(eq("payment"),any())).thenReturn(new PaymentStore.Work(p,"token",attempts,abandoned));
        when(store.lock("payment")).thenReturn(p); when(store.ownsLease("payment","token")).thenReturn(true);
    }
    @Test void refund_abandonedLease_reconcilesWithoutResending() {
        var p=refund(Payment.RefundStatus.DISPATCHING); claim(p,8,true);
        when(gateway.query(p)).thenReturn(new PaymentGateway.Observation(PaymentGateway.Observation.State.UNKNOWN,null));
        worker.process("payment"); verify(gateway,never()).refund(any());
        verify(store).save(argThat(v->v.refundStatus()==Payment.RefundStatus.MANUAL_REVIEW)); verifyNoInteractions(outbox);
    }
    @Test void refund_acceptedRequest_doesNotEmitRefunded() {
        var p=refund(Payment.RefundStatus.DISPATCHING); claim(p,1,false);
        when(gateway.refund(p)).thenReturn(PaymentGateway.RefundOutcome.ACCEPTED);
        worker.process("payment"); verify(store).save(argThat(v->v.refundStatus()==Payment.RefundStatus.ACCEPTED)); verifyNoInteractions(outbox);
    }
    @Test void refund_verifiedFullRefund_persistsBeforeEvent() {
        var p=refund(Payment.RefundStatus.ACCEPTED); claim(p,2,false);
        when(gateway.query(p)).thenReturn(new PaymentGateway.Observation(PaymentGateway.Observation.State.REFUNDED,"123"));
        worker.process("payment"); var sequence=inOrder(store,outbox);
        sequence.verify(store).find("payment"); sequence.verify(store).claim(eq("payment"),any()); sequence.verify(store).lock("payment"); sequence.verify(store).ownsLease("payment","token");
        sequence.verify(store).save(argThat(v->v.refundStatus()==Payment.RefundStatus.REFUNDED)); sequence.verify(outbox).enqueue(eq("PAYMENT_REFUNDED"),eq("payment"),anyString());
    }
}
