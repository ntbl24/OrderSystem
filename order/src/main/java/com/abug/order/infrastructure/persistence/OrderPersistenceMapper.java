package com.abug.order.infrastructure.persistence;

import com.abug.order.domain.*;
import com.abug.payment.domain.PaymentId;
import com.abug.product.domain.ProductId;
import com.abug.user.domain.UserId;

public final class OrderPersistenceMapper {
    public static Order domain(OrderEntity e) {
        Order d = new Order(
            OrderId.of(e.getId()),
            UserId.of(e.getUserId()),
            OrderNumber.of(e.getOrderNumber()),
            e.getSubtotal(),
            e.getShippingCost(),
            e.getShippingAddress(),
            e.getPaymentMethod(),
            e.getPaymentStatus(),
            e.getOrderStatus(),
            e.getOrderDate(),
            e.getEstimatedDeliveryDate(),
            e.getNotes(),
            e.getSellerId(),
            e.getExpiresAt(),
            PaymentId.of(e.getPaymentId()),
            PaymentId.of(e.getReviewPaymentId()),
            e.isDeleted(),
            e.getReservationStatus(),
            e.getVersion()
        );

        e.getItems().forEach(i -> d.addItem(ProductId.of(i.getProductId()), i.getProductName(), i.getSku(), i.getQuantity(), i.getUnitPrice()));
    
        return d;
    }

    public static void copy(Order d, OrderEntity e) {
        e.update(
            d.getUserId().value(), 
            d.getOrderNumber().value(), 
            d.getSubtotal(), 
            d.getShippingCost(), 
            d.getShippingAddress(), 
            d.getPaymentMethod(), 
            d.getPaymentStatus(), 
            d.getOrderStatus(), 
            d.getOrderDate(), 
            d.getEstimatedDeliveryDate(), 
            d.getNotes(), 
            d.getSellerId(), 
            d.getExpiresAt(), 
            d.getPaymentId().value(), 
            d.getReviewPaymentId().value(), 
            d.getReservationStatus()
        );
        e.clearItems();
        d.getItems().forEach(i -> e.addItem(new OrderLine(i.getProductId().value(), i.getProductName(), i.getSku(), i.getQuantity(), i.getUnitPrice())));
    }
}
