package io.order.backend.payment;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.common.application.port.OutboxPort;
import io.order.backend.payment.application.usecase.PaymentService;
import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
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
    @Mock CurrentBuyerPort buyer; @Mock IdempotencyPort keys; @Mock OutboxPort outbox;
    @Mock PayableOrderPort orders; @Mock PaymentStore store; @Mock PaymentGateway gateway;
    PaymentService service;
    @BeforeEach void setup() { service=new PaymentService(buyer,keys,orders,store,gateway,outbox,JsonMapper.builder().build()); }
    private void executeIdempotentAction() {
        when(keys.execute(anyString(), anyString(), anyString(), any(), eq(PaymentResult.class), any()))
                .thenAnswer(invocation -> ((java.util.function.Supplier<?>) invocation.getArgument(5)).get());
    }
    @Test void create_newAttempt_persistsExactAmountAndReturnsProviderUrl() {
        when(buyer.buyerId()).thenReturn("buyer");
        when(orders.lockForPayment("order", "buyer")).thenReturn(new PayableOrderPort.PayableOrder(
                "order", "buyer", new BigDecimal("123456789"), "VND", java.time.Instant.now().plusSeconds(300)));
        when(gateway.paymentUrl(any(), eq("127.0.0.1"))).thenReturn("https://provider.test/pay");
        executeIdempotentAction();
        var result = service.create("order", "key", "127.0.0.1");
        assertThat(result.amount()).isEqualByComparingTo("123456789");
        assertThat(result.paymentUrl()).isEqualTo("https://provider.test/pay");
        assertThat(result.status()).isEqualTo("PENDING");
        var sequence = inOrder(orders, keys, store, gateway);
        sequence.verify(orders).lockForCallback("order");
        sequence.verify(keys).execute(eq("buyer"), eq("PAYMENT_ATTEMPT"), eq("key"),
                eq(new CreatePaymentCommand("order")), eq(PaymentResult.class), any());
        sequence.verify(orders).lockForPayment("order", "buyer");
        sequence.verify(store).orderSettled("order");
        sequence.verify(store).save(argThat(payment -> payment.providerAmount() == 12345678900L));
        sequence.verify(gateway).paymentUrl(any(), eq("127.0.0.1"));
        verifyNoInteractions(outbox);
    }
    @Test void create_idempotencyReplay_returnsOriginalResultWithoutNewAttempt() {
        when(buyer.buyerId()).thenReturn("buyer");
        var original = PaymentResult.from(PaymentDomainTest.pending(BigDecimal.TEN), "original-url");
        when(keys.execute(eq("buyer"), eq("PAYMENT_ATTEMPT"), eq("key"),
                eq(new CreatePaymentCommand("order")), eq(PaymentResult.class), any())).thenReturn(original);
        assertThat(service.create("order", "key", "new-ip")).isSameAs(original);
        verify(orders).lockForCallback("order");
        verify(orders, never()).lockForPayment(anyString(), anyString());
        verifyNoInteractions(store, gateway, outbox);
    }
    @Test void create_alreadySettled_rejectsWithoutSaving() {
        when(buyer.buyerId()).thenReturn("buyer");
        when(orders.lockForPayment("order", "buyer")).thenReturn(new PayableOrderPort.PayableOrder(
                "order", "buyer", BigDecimal.TEN, "VND", java.time.Instant.now().plusSeconds(300)));
        when(store.orderSettled("order")).thenReturn(true);
        executeIdempotentAction();
        assertThatThrownBy(() -> service.create("order", "key", "ip")).isInstanceOf(IllegalStateException.class)
                .hasMessage("Order already paid");
        verify(store, never()).save(any()); verifyNoInteractions(gateway, outbox);
    }
    @Test void get_otherBuyer_cannotReadPayment() {
        when(buyer.buyerId()).thenReturn("other");
        when(store.find("payment")).thenReturn(Optional.of(PaymentDomainTest.pending(BigDecimal.TEN)));
        assertThatThrownBy(() -> service.get("payment"))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
        verifyNoInteractions(gateway, outbox);
    }
    @Test void refund_settledPayment_persistsIntentBeforeOutboxWithoutProviderCall() {
        var payment = PaymentDomainTest.pending(BigDecimal.TEN).observe(true, "123");
        when(buyer.buyerId()).thenReturn("admin");
        when(store.find("payment")).thenReturn(Optional.of(payment));
        when(store.lock("payment")).thenReturn(payment);
        executeIdempotentAction();
        var result = service.refund("payment", "key", "requested");
        assertThat(result.refundStatus()).isEqualTo("REQUESTED");
        var sequence = inOrder(orders, store, outbox);
        sequence.verify(store).find("payment"); sequence.verify(orders).lockForCallback("order");
        sequence.verify(store).lock("payment");
        sequence.verify(store).save(argThat(p -> p.refundStatus() == Payment.RefundStatus.REQUESTED
                && p.refundActor().equals("admin") && p.refundReason().equals("requested")));
        sequence.verify(outbox).enqueue("PAYMENT_REFUND_REQUESTED", "payment", "{\"paymentId\":\"payment\"}");
        verifyNoInteractions(gateway);
    }
    @Test void refund_existingIntent_doesNotEnqueueOrReplaceStableRequest() {
        var payment = PaymentDomainTest.pending(BigDecimal.TEN).observe(true, "123")
                .requestRefund("stable", "admin", "original", java.time.Instant.now());
        when(buyer.buyerId()).thenReturn("admin");
        when(store.find("payment")).thenReturn(Optional.of(payment));
        when(store.lock("payment")).thenReturn(payment);
        executeIdempotentAction();
        assertThat(service.refund("payment", "new-key", "new reason").refundStatus()).isEqualTo("REQUESTED");
        verify(store).save(payment); verifyNoInteractions(outbox, gateway);
    }
    @Test void refund_pendingPayment_rejectsWithoutIntentOrEvent() {
        var payment = PaymentDomainTest.pending(BigDecimal.TEN);
        when(buyer.buyerId()).thenReturn("admin");
        when(store.find("payment")).thenReturn(Optional.of(payment));
        when(store.lock("payment")).thenReturn(payment);
        executeIdempotentAction();
        assertThatThrownBy(() -> service.refund("payment", "key", "requested"))
                .isInstanceOf(IllegalStateException.class).hasMessage("Only settled payments can be refunded");
        verify(store, never()).save(any()); verifyNoInteractions(outbox, gateway);
    }
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
