package io.order.backend.payment;

import io.order.backend.payment.application.port.in.PaymentResult;
import io.order.backend.payment.application.port.in.RefundPaymentCommand;
import io.order.backend.payment.infrastructure.web.dto.PaymentResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResultCompatibilityTest {
    private final JsonMapper json = JsonMapper.builder().build();

    @Test
    void commands_preservePersistedIdempotencyHashInput() {
        assertThat(json.writeValueAsString(java.util.Map.of("orderId", io.order.backend.order.domain.OrderId.of("order").toString())))
                .isEqualTo("{\"orderId\":\"" + io.order.backend.order.domain.OrderId.of("order") + "\"}");
        assertThat(json.writeValueAsString(new RefundPaymentCommand("payment", "requested")))
                .isEqualTo("{\"paymentId\":\"payment\",\"reason\":\"requested\"}");
    }

    @Test
    void result_readsPreviouslyPersistedResponseAndPreservesWebJson() {
        String persisted = "{\"id\":\"payment\",\"orderId\":\"order\",\"amount\":10,\"currency\":\"VND\","
                + "\"status\":\"PENDING\",\"refundStatus\":\"NONE\","
                + "\"expiresAt\":\"2026-10-14T08:15:00Z\",\"paymentUrl\":\"https://provider.test/pay\"}";
        PaymentResult replay = json.readValue(persisted, PaymentResult.class);
        assertThat(replay).isEqualTo(PaymentResult.from(PaymentDomainTest.pending(BigDecimal.TEN),
                "https://provider.test/pay"));
        assertThat(json.readTree(json.writeValueAsString(PaymentResponse.from(replay))))
                .isEqualTo(json.readTree(persisted));
    }
}
