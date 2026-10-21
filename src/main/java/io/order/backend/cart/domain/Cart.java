package io.order.backend.cart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;


public class Cart{
    private CartId id;
    private UserId userId;
    private Long version;
    private List<CartItem> items;
    private BigDecimal subTotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private BigDecimal tax;
    private String couponCode;
    private BigDecimal discount;

    public Cart(CartId id, UserId userId){
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>();
        this.subTotal = BigDecimal.ZERO;
        this.shippingCost = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.couponCode = null;
        this.discount = BigDecimal.ZERO;
    }

    public static Cart create(UserId userId){
        return new Cart(CartId.generate(), userId);
    }

    public void addItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice){
         if (quantity < 1 || quantity > 100000)
            throw new IllegalArgumentException("Invalid quantity");
        items.add(new CartItem(productId, productName, quantity, unitPrice));
        calculateTotals();
    }

    private void calculateTotals(){
        this.subTotal = items.stream()
        .map(i -> BigDecimal.valueOf(i.getQuantity()).multiply(i.getUnitPrice()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.subTotal.compareTo(BigDecimal.ZERO) > 0)
            this.shippingCost = BigDecimal.valueOf(3000);
        else
            this.shippingCost = BigDecimal.ZERO;
        
        this.total = this.subTotal.add(this.shippingCost).add(this.tax).subtract(this.discount);
    }

    public void addOrUpdateItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice) {
        items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .ifPresentOrElse(
                item -> item.setQuantity(quantity),
                () -> addItem(productId, productName, quantity, unitPrice)
            );
    }

    public CartId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public BigDecimal getDiscount() {
        return discount;
    }
}


