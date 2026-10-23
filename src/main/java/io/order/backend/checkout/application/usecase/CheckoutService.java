package io.order.backend.checkout.application.usecase;

import io.order.backend.checkout.application.port.in.CheckoutCommand;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.checkout.application.port.in.CheckoutUseCase;
import io.order.backend.checkout.application.port.out.InventoryPort;
import io.order.backend.checkout.application.port.out.PlaceOrderPort;
import io.order.backend.order.application.port.in.OrderResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.*;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutService implements CheckoutUseCase {
    private final CurrentBuyerPort buyer;
    private final IdempotencyPort idempotency;
    private final InventoryPort inventory;
    private final PlaceOrderPort orders;

    @Transactional
    public OrderResult checkout(String key, CheckoutCommand request) {
        String owner = buyer.buyerId();
        return idempotency.execute(owner, "CHECKOUT", key, request, OrderResult.class, () -> create(owner, request));
    }

    private OrderResult create(String owner, CheckoutCommand request) {
        if (request.items() == null || request.items().isEmpty() || request.items().size() > 100
                || request.shippingAddress() == null || request.shippingAddress().isBlank())
            throw new IllegalArgumentException("Invalid checkout");
        Map<String, Integer> quantities = new TreeMap<>();
        for (var line : request.items()) {
            if (line.quantity() < 1 || line.quantity() > 100000)
                throw new IllegalArgumentException("Invalid quantity");
            quantities.merge(line.productId(), line.quantity(), Math::addExact);
        }
        var locked = inventory.reserve(quantities);
        String seller = null;
        List<PlaceOrderPort.Line> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var entry : quantities.entrySet()) {
            var p = locked.get(entry.getKey());
            if (seller == null)
                seller = p.sellerId();
            else if (!seller.equals(p.sellerId()))
                throw new IllegalStateException("Checkout requires one seller");
            var lineTotal = p.price().multiply(BigDecimal.valueOf(entry.getValue()));
            lines.add(new PlaceOrderPort.Line(p.id(), p.title(), p.sku(), entry.getValue(), p.price(), lineTotal));
            subtotal = subtotal.add(lineTotal);
        }
        if (subtotal.precision() > 19)
            throw new IllegalArgumentException("Order total exceeds limit");
        return orders.place(new PlaceOrderPort.Command(owner, seller, lines, subtotal,
                request.shippingAddress(), request.notes()));
    }
}
