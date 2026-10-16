package io.order.backend.checkout.infrastructure.local;

import io.order.backend.checkout.application.port.out.PlaceOrderPort;
import io.order.backend.order.application.port.in.PlaceOrderUseCase;
import io.order.backend.order.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceOrderAdapter implements PlaceOrderPort {
    private final PlaceOrderUseCase orders;

    @Override
    public OrderDTO place(Command command) {
        var lines = command.lines().stream().map(l -> new PlaceOrderUseCase.Line(
                l.productId(), l.productName(), l.sku(), l.quantity(), l.unitPrice(), l.subtotal())).toList();
        return orders.placeOrder(new PlaceOrderUseCase.CreateOrder(command.buyerId(), command.sellerId(),
                lines, command.subtotal(), command.shippingAddress(), command.notes()));
    }
}
