package com.abug.payment;
import com.abug.common.application.port.CurrentBuyerPort;
import com.abug.common.application.port.IdempotencyPort;
import com.abug.common.application.port.OutboxPort;
import com.abug.common.exception.AppException;
import com.abug.payment.application.usecase.*;
import com.abug.payment.application.port.in.*;
import com.abug.payment.application.port.out.*;
import com.abug.payment.domain.Payment;
import com.abug.payment.domain.PaymentId;
import com.abug.payment.domain.TransactionRef;
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
    private static final PaymentId PAYMENT_ID = PaymentId.of("payment");
    private static final TransactionRef TRANSACTION_REF = TransactionRef.of("ref");
    @Mock CurrentBuyerPort buyer; @Mock IdempotencyPort keys; @Mock OutboxPort outbox;
    @Mock PayableOrderPort orders; @Mock PaymentRepository store; @Mock PaymentGateway gateway;
    CreatePaymentService create; PaymentQueryService query; RefundPaymentService refunds; PaymentCallbackService callbacks;
    @BeforeEach void setup() {
        create = new CreatePaymentService(buyer, orders, keys, store, gateway);
        query = new PaymentQueryService(buyer, store);
        refunds = new RefundPaymentService(buyer, keys, orders, store, outbox, JsonMapper.builder().build());
        callbacks = new PaymentCallbackService(orders, store, outbox, JsonMapper.builder().build());
    }
    private void executeIdempotentAction() {
        when(keys.execute(anyString(), anyString(), anyString(), any(), eq(PaymentResult.class), any()))
                .thenAnswer(invocation -> ((java.util.function.Supplier<?>) invocation.getArgument(5)).get());
    }
    @Test void create_newAttempt_persistsExactAmountAndReturnsProviderUrl() {
        when(buyer.buyerId()).thenReturn("00000000-0000-0000-0000-000000000001");
        when(orders.lockForPayment(com.abug.order.domain.OrderId.of("order"), com.abug.user.domain.UserId.of("00000000-0000-0000-0000-000000000001"))).thenReturn(new PayableOrderPort.PayableOrder(
                com.abug.order.domain.OrderId.of("order"), com.abug.user.domain.UserId.of("00000000-0000-0000-0000-000000000001"), new BigDecimal("123456789"), "VND", java.time.Instant.now().plusSeconds(300)));
        when(gateway.paymentUrl(any(), eq("127.0.0.1"))).thenReturn("https://provider.test/pay");
        executeIdempotentAction();
        var result = create.create(new CreatePaymentCommand(com.abug.order.domain.OrderId.of("order"), "key", "127.0.0.1"));
        assertThat(result.amount()).isEqualByComparingTo("123456789");
        assertThat(result.paymentUrl()).isEqualTo("https://provider.test/pay");
        assertThat(result.status()).isEqualTo("PENDING");
        var sequence = inOrder(orders, keys, store, gateway);
        sequence.verify(orders).lockForCallback(com.abug.order.domain.OrderId.of("order"));
        sequence.verify(keys).execute(eq("00000000-0000-0000-0000-000000000001"), eq("PAYMENT_ATTEMPT"), eq("key"),
                eq(java.util.Map.of("orderId", com.abug.order.domain.OrderId.of("order").toString())), eq(PaymentResult.class), any());
        sequence.verify(orders).lockForPayment(com.abug.order.domain.OrderId.of("order"), com.abug.user.domain.UserId.of("00000000-0000-0000-0000-000000000001"));
        sequence.verify(store).orderSettled(com.abug.order.domain.OrderId.of("order"));
        sequence.verify(store).save(argThat(payment -> payment.providerAmount() == 12345678900L));
        sequence.verify(gateway).paymentUrl(any(), eq("127.0.0.1"));
        verifyNoInteractions(outbox);
    }
    @Test void create_idempotencyReplay_returnsOriginalResultWithoutNewAttempt() {
        when(buyer.buyerId()).thenReturn("00000000-0000-0000-0000-000000000001");
        var original = PaymentResult.from(PaymentTestFixtures.pending(BigDecimal.TEN), "original-url");
        when(keys.execute(eq("00000000-0000-0000-0000-000000000001"), eq("PAYMENT_ATTEMPT"), eq("key"),
                eq(java.util.Map.of("orderId", com.abug.order.domain.OrderId.of("order").toString())), eq(PaymentResult.class), any())).thenReturn(original);
        assertThat(create.create(new CreatePaymentCommand(com.abug.order.domain.OrderId.of("order"), "key", "new-ip"))).isSameAs(original);
        verify(orders).lockForCallback(com.abug.order.domain.OrderId.of("order"));
        verify(orders, never()).lockForPayment(any(), any());
        verifyNoInteractions(store, gateway, outbox);
    }
    @Test void create_alreadySettled_rejectsWithoutSaving() {
        when(buyer.buyerId()).thenReturn("00000000-0000-0000-0000-000000000001");
        when(orders.lockForPayment(com.abug.order.domain.OrderId.of("order"), com.abug.user.domain.UserId.of("00000000-0000-0000-0000-000000000001"))).thenReturn(new PayableOrderPort.PayableOrder(
                com.abug.order.domain.OrderId.of("order"), com.abug.user.domain.UserId.of("00000000-0000-0000-0000-000000000001"), BigDecimal.TEN, "VND", java.time.Instant.now().plusSeconds(300)));
        when(store.orderSettled(com.abug.order.domain.OrderId.of("order"))).thenReturn(true);
        executeIdempotentAction();
        assertThatThrownBy(() -> create.create(new CreatePaymentCommand(com.abug.order.domain.OrderId.of("order"), "key", "ip"))).isInstanceOf(AppException.class)
                .hasMessage("Order already paid");
        verify(store, never()).save(any()); verifyNoInteractions(gateway, outbox);
    }
    @Test void get_otherBuyer_cannotReadPayment() {
        when(buyer.buyerId()).thenReturn("other");
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(PaymentTestFixtures.pending(BigDecimal.TEN)));
        assertThatThrownBy(() -> query.get(PAYMENT_ID))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
        verifyNoInteractions(gateway, outbox);
    }
    @Test void refund_settledPayment_persistsIntentBeforeOutboxWithoutProviderCall() {
        var payment = PaymentTestFixtures.pending(BigDecimal.TEN).observe(true, "123");
        when(buyer.buyerId()).thenReturn("admin");
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(store.lock(PAYMENT_ID)).thenReturn(payment);
        executeIdempotentAction();
        var result = refunds.refund(PAYMENT_ID, "key", "requested");
        assertThat(result.refundStatus()).isEqualTo("REQUESTED");
        var sequence = inOrder(orders, store, outbox);
        sequence.verify(store).find(PAYMENT_ID); sequence.verify(orders).lockForCallback(com.abug.order.domain.OrderId.of("order"));
        sequence.verify(store).lock(PAYMENT_ID);
        sequence.verify(store).save(argThat(p -> p.getRefundStatus() == Payment.RefundStatus.REQUESTED
                && p.getRefundActor().equals("admin") && p.getRefundReason().equals("requested")));
        sequence.verify(outbox).enqueue("PAYMENT_REFUND_REQUESTED", PAYMENT_ID.toString(),
                "{\"paymentId\":\"" + PAYMENT_ID + "\"}");
        verifyNoInteractions(gateway);
    }
    @Test void refund_existingIntent_doesNotEnqueueOrReplaceStableRequest() {
        var payment = PaymentTestFixtures.pending(BigDecimal.TEN).observe(true, "123")
                .requestRefund("stable", "admin", "original", java.time.Instant.now());
        when(buyer.buyerId()).thenReturn("admin");
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(store.lock(PAYMENT_ID)).thenReturn(payment);
        executeIdempotentAction();
        assertThat(refunds.refund(PAYMENT_ID, "new-key", "new reason").refundStatus()).isEqualTo("REQUESTED");
        verify(store).save(payment); verifyNoInteractions(outbox, gateway);
    }
    @Test void refund_pendingPayment_rejectsWithoutIntentOrEvent() {
        var payment = PaymentTestFixtures.pending(BigDecimal.TEN);
        when(buyer.buyerId()).thenReturn("admin");
        when(store.find(PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(store.lock(PAYMENT_ID)).thenReturn(payment);
        executeIdempotentAction();
        assertThatThrownBy(() -> refunds.refund(PAYMENT_ID, "key", "requested"))
                .isInstanceOf(AppException.class).hasMessage("Only settled payments can be refunded");
        verify(store, never()).save(any()); verifyNoInteractions(outbox, gateway);
    }
    @Test void ipn_invalidSignature_doesNotReadOrMutatePayments() {
        assertThat(callbacks.ipn(new PaymentNotification(false,false,null,null,null,false,null))).isEqualTo(PaymentCallbackUseCase.NotificationResult.INVALID_SIGNATURE); verifyNoInteractions(store,orders,outbox);
    }
    @Test void ipn_invalidAmount_doesNotLockOrMutate() {
        var f=new PaymentNotification(true,false,"ref",null,null,false,null);
        when(store.byReference(TRANSACTION_REF)).thenReturn(Optional.of(PaymentTestFixtures.pending(BigDecimal.TEN)));
        assertThat(callbacks.ipn(f)).isEqualTo(PaymentCallbackUseCase.NotificationResult.INVALID_DATA); verifyNoInteractions(orders,outbox); verify(store,never()).save(any());
    }
    @Test void ipn_lateSuccess_persistsBeforeEventAndLocksOrderFirst() {
        var p=PaymentTestFixtures.pending(BigDecimal.TEN).observe(false,null);
        var f=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        when(store.byReference(TRANSACTION_REF)).thenReturn(Optional.of(p)); when(store.lock(p.getId())).thenReturn(p);
        assertThat(callbacks.ipn(f)).isEqualTo(PaymentCallbackUseCase.NotificationResult.CONFIRMED);
        var sequence=inOrder(orders,store,outbox); sequence.verify(store).byReference(TRANSACTION_REF); sequence.verify(orders).lockForCallback(com.abug.order.domain.OrderId.of("order"));
        sequence.verify(store).lock(PAYMENT_ID); sequence.verify(store).save(argThat(v->v.getStatus()==Payment.Status.SUCCEEDED));
        sequence.verify(outbox).enqueue(eq("PAYMENT_SUCCEEDED"),eq(PAYMENT_ID.toString()),
                contains("\"orderId\":\"" + com.abug.order.domain.OrderId.of("order") + "\""));
    }
    @Test void ipn_unknownReference_returnsNotFoundBeforeDataValidation() {
        var notification=new PaymentNotification(true,false,"missing",null,null,false,null);
        when(store.byReference(TransactionRef.of("missing"))).thenReturn(Optional.empty());
        assertThat(callbacks.ipn(notification)).isEqualTo(PaymentCallbackUseCase.NotificationResult.NOT_FOUND);
        verifyNoInteractions(orders,outbox);
    }
    @Test void ipn_mismatchedAmountCurrencyOrReference_doesNotMutate() {
        var p=PaymentTestFixtures.pending(BigDecimal.TEN);
        when(store.byReference(any(TransactionRef.class))).thenReturn(Optional.of(p));
        for(var notification:List.of(
                new PaymentNotification(true,true,"ref",BigDecimal.ONE,"VND",true,"123"),
                new PaymentNotification(true,true,"ref",BigDecimal.TEN,"USD",true,"123"),
                new PaymentNotification(true,true,"other",BigDecimal.TEN,"VND",true,"123"))) {
            assertThat(callbacks.ipn(notification)).isEqualTo(PaymentCallbackUseCase.NotificationResult.INVALID_DATA);
        }
        verifyNoInteractions(orders,outbox); verify(store,never()).save(any());
    }
    @Test void returnView_validNotification_reportsPersistedStateWithoutSettlement() {
        var p=PaymentTestFixtures.pending(BigDecimal.TEN);
        var notification=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        when(store.byReference(TRANSACTION_REF)).thenReturn(Optional.of(p));
        assertThat(callbacks.returnView(notification)).isEqualTo(new PaymentCallbackUseCase.ReturnView(true,"PENDING",
                "Payment confirmation is processed through IPN, not this page"));
        verifyNoInteractions(orders,outbox); verify(store,never()).save(any());
    }
    @Test void ipn_conflictingTransaction_doesNotMutate() {
        var p=PaymentTestFixtures.pending(BigDecimal.TEN).observe(true,"123");
        when(store.byReference(TRANSACTION_REF)).thenReturn(Optional.of(p)); when(store.lock(p.getId())).thenReturn(p);
        assertThat(callbacks.ipn(new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"456")))
                .isEqualTo(PaymentCallbackUseCase.NotificationResult.CONFLICTING_TRANSACTION);
        verifyNoInteractions(outbox); verify(store,never()).save(any());
    }
    @Test void ipn_duplicateSuccess_doesNotEmitAgain() {
        var p=PaymentTestFixtures.pending(BigDecimal.TEN).observe(true,"123");
        var f=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        when(store.byReference(TRANSACTION_REF)).thenReturn(Optional.of(p)); when(store.lock(p.getId())).thenReturn(p);
        assertThat(callbacks.ipn(f)).isEqualTo(PaymentCallbackUseCase.NotificationResult.ALREADY_CONFIRMED); verifyNoInteractions(outbox); verify(store,never()).save(any());
    }
}
