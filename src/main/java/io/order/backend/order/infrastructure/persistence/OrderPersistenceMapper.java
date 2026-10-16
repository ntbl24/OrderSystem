package io.order.backend.order.infrastructure.persistence;

import io.order.backend.order.domain.*;
import java.util.*;

public final class OrderPersistenceMapper {
    public static Order domain(OrderEntity e) {
        Order d = new Order();
        d.setId(e.id);
        d.setCreatedAt(e.createdAt);
        d.setUpdatedAt(e.updatedAt);
        d.setDeleted(e.deleted);
        d.setUserId(e.userId);
        d.setOrderNumber(e.orderNumber);
        d.setItems(e.items.stream().map(i -> {
            OrderItem x = new OrderItem();
            x.setProductId(i.productId);
            x.setProductName(i.productName);
            x.setSku(i.sku);
            x.setQuantity(i.quantity);
            x.setUnitPrice(i.unitPrice);
            x.setSubtotal(i.subtotal);
            return x;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        d.setSubtotal(e.subtotal);
        d.setShippingCost(e.shippingCost);
        d.setShippingAddress(e.shippingAddress);
        d.setPaymentMethod(e.paymentMethod);
        d.setPaymentStatus(e.paymentStatus);
        d.setOrderStatus(e.orderStatus);
        d.setOrderDate(e.orderDate);
        d.setEstimatedDeliveryDate(e.estimatedDeliveryDate);
        d.setNotes(e.notes);
        d.setSellerId(e.sellerId);
        d.setExpiresAt(e.expiresAt);
        d.setPaymentId(e.paymentId);
        d.setReviewPaymentId(e.reviewPaymentId);
        d.setReservationStatus(e.reservationStatus);
        d.setVersion(e.version);
        return d;
    }

    public static void copy(Order d, OrderEntity e) {
        e.id = d.getId();
        e.createdAt = d.getCreatedAt();
        e.updatedAt = d.getUpdatedAt();
        e.deleted = d.isDeleted();
        e.userId = d.getUserId();
        e.orderNumber = d.getOrderNumber();
        e.items = d.getItems().stream().map(i -> {
            OrderLine x = new OrderLine();
            x.productId = i.getProductId();
            x.productName = i.getProductName();
            x.sku = i.getSku();
            x.quantity = i.getQuantity();
            x.unitPrice = i.getUnitPrice();
            x.subtotal = i.getSubtotal();
            return x;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        e.subtotal = d.getSubtotal();
        e.shippingCost = d.getShippingCost();
        e.shippingAddress = d.getShippingAddress();
        e.paymentMethod = d.getPaymentMethod();
        e.paymentStatus = d.getPaymentStatus();
        e.orderStatus = d.getOrderStatus();
        e.orderDate = d.getOrderDate();
        e.estimatedDeliveryDate = d.getEstimatedDeliveryDate();
        e.notes = d.getNotes();
        e.sellerId = d.getSellerId();
        e.expiresAt = d.getExpiresAt();
        e.paymentId = d.getPaymentId();
        e.reviewPaymentId = d.getReviewPaymentId();
        e.reservationStatus = d.getReservationStatus();
        e.version = d.getVersion();
    }
}
