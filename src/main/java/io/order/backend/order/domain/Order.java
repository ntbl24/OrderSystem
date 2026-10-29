package io.order.backend.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import io.order.backend.payment.domain.PaymentId;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;

public class Order {
    private OrderId id;
    private UserId userId;
    private OrderNumber orderNumber;
    private List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal shippingCost;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
    private String sellerId;
    private Instant expiresAt;
    private PaymentId paymentId;
    private PaymentId reviewPaymentId;
    private boolean deleted;
    private String reservationStatus;
    private Long version;

    Order(){}

    public Order(
            OrderId id,
            UserId userId,
            OrderNumber orderNumber,
            BigDecimal subtotal,
            BigDecimal shippingCost,
            String shippingAddress,
            String paymentMethod,
            String paymentStatus,
            String orderStatus,
            LocalDateTime orderDate,
            LocalDateTime estimatedDeliveryDate,
            String notes,
            String sellerId,
            Instant expiresAt,
            PaymentId paymentId,
            PaymentId reviewPaymentId,
            boolean deleted,
            String reservationStatus,
            Long version) {
        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.notes = notes;
        this.sellerId = sellerId;
        this.expiresAt = expiresAt;
        this.paymentId = paymentId;
        this.reviewPaymentId = reviewPaymentId;
        this.deleted = deleted;
        this.reservationStatus = reservationStatus;
        this.version = version;
    }

    public void addItem(ProductId productId, String productName, String sku, int quantity, BigDecimal unitPrice) {
        int existing = items.stream().filter(i -> i.getProductId().equals(productId))
                .mapToInt(OrderItem::getQuantity).findFirst().orElse(0);
        addOrUpdateItem(productId, productName, sku, Math.addExact(existing, quantity), unitPrice);
    }

    public void addOrUpdateItem(ProductId productId, String productName, String sku, int quantity, BigDecimal unitPrice) {
        OrderItem replacement = new OrderItem(productId, productName, sku, quantity, unitPrice);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProductId().equals(productId)) {
                items.set(i, replacement);
                calculateTotals();
                return;
            }
        }
        if (items.size() >= 100)
            throw new IllegalArgumentException("Too many cart items");
        items.add(replacement);
        calculateTotals();
    }

    private void calculateTotals() {
        subtotal = items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subtotal.add(shippingCost);
    }

    public static Order place(UserId ownerId, String sellerId, List<OrderItem> items,
            BigDecimal subtotal, String shippingAddress, String notes, Instant now,
            LocalDateTime orderDate) {
        Order order = new Order();
        order.id = OrderId.generate();
        order.orderNumber = OrderNumber.fromOrderId(order.id);
        order.userId = ownerId;
        order.sellerId = sellerId;
        order.subtotal = subtotal;
        order.shippingCost = BigDecimal.valueOf(30000);
        order.shippingAddress = shippingAddress;
        order.notes = notes;
        order.paymentMethod = "VNPAY";
        order.paymentStatus = "UNPAID";
        order.orderStatus = "UNPAID";
        order.reservationStatus = "HELD";
        order.orderDate = orderDate;
        order.expiresAt = now.plusSeconds(900);

        items.stream().forEach(item -> order.addItem(item.getProductId(), item.getProductName(), item.getSku(), item.getQuantity(), item.getUnitPrice()));
        
        return order;
    }

    public PaymentId getReviewPaymentId() {
        return reviewPaymentId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public PaymentId getPaymentId() {
        return paymentId;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public boolean requiresReservationTransition(String target) {
        if (target.equals(reservationStatus))
            return false;
        if (!("HELD".equals(reservationStatus) && ("RELEASED".equals(target) || "CONSUMED".equals(target)))
                && !("CONSUMED".equals(reservationStatus) && "RETURNED".equals(target)))
            throw new IllegalStateException("Invalid inventory transition");
        return true;
    }

    public void transitionReservation(String target) {
        if (requiresReservationTransition(target))
            reservationStatus = target;
    }

    public Long getVersion() {
        return version;
    }

    public BigDecimal getTotal() {
        return subtotal.add(shippingCost);
    }

    public boolean unpaid() {
        return "UNPAID".equals(orderStatus);
    }

    public void cancel() {
        if (!unpaid())
            throw new IllegalStateException("Only unpaid orders can be cancelled");
        orderStatus = "CANCELLED";
    }

    public boolean expire(Instant now) {
        if (!unpaid() || expiresAt.isAfter(now))
            return false;
        orderStatus = "EXPIRED";
        return true;
    }

    public boolean acceptPayment(PaymentId id, BigDecimal amount, String currency, Instant now) {
        if (id == null)
            throw new IllegalArgumentException("Payment ID required");
        if (id.equals(paymentId))
            return false;
        if (!"VND".equals(currency) || amount == null || amount.compareTo(getTotal()) != 0
                || !unpaid() || !expiresAt.isAfter(now)) {
            orderStatus = "PAYMENT_REVIEW";
            paymentStatus = "REVIEW";
            reviewPaymentId = id;
            return false;
        }
        paymentId = id;
        paymentStatus = "PAID";
        orderStatus = "PAID";
        return true;
    }

    public boolean acceptRefund(PaymentId id, BigDecimal amount, String currency) {
        if (!"VND".equals(currency) || amount == null || amount.compareTo(getTotal()) != 0)
            throw new IllegalStateException("Refund amount or currency mismatch");
        if (!id.equals(paymentId) || "REFUNDED".equals(paymentStatus))
            return false;
        paymentStatus = "REFUNDED";
        orderStatus = "REFUNDED";
        return true;
    }

    public UserId getUserId() {
        return userId;
    }

    public OrderNumber getOrderNumber() {
        return orderNumber;
    }


    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public OrderId getId() {
        return id;
    }
}
