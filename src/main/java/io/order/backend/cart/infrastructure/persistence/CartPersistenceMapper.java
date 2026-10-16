package io.order.backend.cart.infrastructure.persistence;

import io.order.backend.cart.domain.*;
import java.util.*;

public final class CartPersistenceMapper {
    public static Cart domain(CartEntity e) {
        Cart d = new Cart();
        d.setId(e.id);
        d.setCreatedAt(e.createdAt);
        d.setUpdatedAt(e.updatedAt);
        d.setDeleted(e.deleted);
        d.setUserId(e.userId);
        d.setVersion(e.version);
        d.setItems(e.items.stream().map(i -> {
            CartItem x = new CartItem();
            x.setProductId(i.productId);
            x.setProductName(i.productName);
            x.setQuantity(i.quantity);
            x.setUnitPrice(i.unitPrice);
            x.setSubTotal(i.subTotal);
            return x;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        d.setSubTotal(e.subTotal);
        d.setShippingCost(e.shippingCost);
        d.setTotal(e.total);
        d.setTax(e.tax);
        d.setCouponCode(e.couponCode);
        d.setDiscount(e.discount);
        return d;
    }

    public static void copy(Cart d, CartEntity e) {
        e.id = d.getId();
        e.createdAt = d.getCreatedAt();
        e.updatedAt = d.getUpdatedAt();
        e.deleted = d.isDeleted();
        e.userId = d.getUserId();
        e.version = d.getVersion();
        e.items = d.getItems().stream().map(i -> {
            CartLine x = new CartLine();
            x.productId = i.getProductId();
            x.productName = i.getProductName();
            x.quantity = i.getQuantity();
            x.unitPrice = i.getUnitPrice();
            x.subTotal = i.getSubTotal();
            return x;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        e.subTotal = d.getSubTotal();
        e.shippingCost = d.getShippingCost();
        e.total = d.getTotal();
        e.tax = d.getTax();
        e.couponCode = d.getCouponCode();
        e.discount = d.getDiscount();
    }
}
