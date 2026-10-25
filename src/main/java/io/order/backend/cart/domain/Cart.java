package io.order.backend.cart.domain;

import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart {
    private final CartId id;
    private final UserId userId;
    private Long version;
    private final List<CartItem> items = new ArrayList<>();
    private BigDecimal subTotal = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private String couponCode;
    private BigDecimal discount = BigDecimal.ZERO;
    private boolean deleted;

    public Cart(CartId id, UserId userId) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
    }

    public static Cart create(UserId userId) {
        return new Cart(CartId.generate(), userId);
    }

    public static Cart restore(CartId id, UserId userId, Long version, List<CartItem> items,
                               BigDecimal shippingCost, BigDecimal tax, String couponCode, BigDecimal discount) {
        Cart cart = new Cart(id, userId);
        cart.version = version;
        cart.items.addAll(items);
        cart.shippingCost = shippingCost;
        cart.tax = tax;
        cart.couponCode = couponCode;
        cart.discount = discount;
        cart.calculateTotals();
        return cart;
    }

    public void addItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice) {
        CartItem.validateQuantity(quantity);
        int existing = items.stream().filter(i -> i.getProductId().equals(productId))
                .mapToInt(CartItem::getQuantity).findFirst().orElse(0);
        addOrUpdateItem(productId, productName, Math.addExact(existing, quantity), unitPrice);
    }

    public void addOrUpdateItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice) {
        CartItem replacement = new CartItem(productId, productName, quantity, unitPrice);
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

    public void removeItem(ProductId productId) {
        items.removeIf(i -> i.getProductId().equals(productId));
        calculateTotals();
    }

    private void calculateTotals() {
        subTotal = items.stream().map(CartItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subTotal.add(shippingCost).add(tax).subtract(discount);
    }

    public CartId getId() { return id; }
    public UserId getUserId() { return userId; }
    public Long getVersion() { return version; }
    public List<CartItem> getItems() { return List.copyOf(items); }
    public BigDecimal getSubTotal() { return subTotal; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getTax() { return tax; }
    public String getCouponCode() { return couponCode; }
    public BigDecimal getDiscount() { return discount; }
}
