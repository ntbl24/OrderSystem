package io.order.backend.order;

import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderNumber;
import io.order.backend.payment.domain.PaymentId;
import io.order.backend.user.domain.UserId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class OrderValueObjectTest {

    @Test
    void orderId_generate_shouldProduceValidUniqueId() {
        OrderId id1 = OrderId.generate();
        OrderId id2 = OrderId.generate();

        assertThat(id1).isNotNull();
        assertThat(id1.value()).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.toString()).isEqualTo(id1.value().toString());
    }

    @Test
    void orderId_of_withUuidString_shouldParseCorrectly() {
        UUID uuid = UUID.randomUUID();
        OrderId id = OrderId.of(uuid.toString());

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void orderId_of_withNonUuidString_shouldDeriveDeterministicUuid() {
        OrderId id1 = OrderId.of("order-test-1");
        OrderId id2 = OrderId.of("order-test-1");

        assertThat(id1.value()).isNotNull();
        assertThat(id1).isEqualTo(id2);
    }

    @Test
    void orderId_null_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> new OrderId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void orderNumber_whenValid_shouldHoldValue() {
        OrderNumber number = OrderNumber.of("ORD-12345");
        assertThat(number.value()).isEqualTo("ORD-12345");
        assertThat(number.toString()).isEqualTo("ORD-12345");

        OrderId orderId = OrderId.generate();
        OrderNumber fromId = OrderNumber.fromOrderId(orderId);
        assertThat(fromId.value()).isEqualTo("ORD-" + orderId.value());
    }

    @Test
    void orderNumber_whenBlankOrNull_shouldThrowException() {
        assertThatThrownBy(() -> new OrderNumber(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new OrderNumber(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new OrderNumber("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void order_withValueObjectAccessors_shouldSetAndGetCorrectly() {
        Order order = new Order();
        OrderId orderId = OrderId.generate();
        OrderNumber orderNumber = OrderNumber.of("ORD-9999");
        UserId userId = UserId.generate();
        PaymentId paymentId = PaymentId.generate();

        order.setOrderId(orderId);
        order.setOrderNumberVo(orderNumber);
        order.setUserIdentifier(userId);
        order.setPaymentIdentifier(paymentId);

        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getId()).isEqualTo(orderId.value().toString());
        assertThat(order.getOrderNumberVo()).isEqualTo(orderNumber);
        assertThat(order.getOrderNumber()).isEqualTo("ORD-9999");
        assertThat(order.getUserIdentifier()).isEqualTo(userId);
        assertThat(order.getUserId()).isEqualTo(userId.value().toString());
        assertThat(order.getPaymentIdentifier()).isEqualTo(paymentId);
        assertThat(order.getPaymentId()).isEqualTo(paymentId.value().toString());
    }
}
