package io.order.backend.checkout.application.port.in;

import java.util.List;

public record CheckoutCommand(List<Item> items, String shippingAddress, String notes) {
    public CheckoutCommand {
        items = items == null ? null : List.copyOf(items);
    }

    public record Item(String productId, int quantity) {
    }
}
