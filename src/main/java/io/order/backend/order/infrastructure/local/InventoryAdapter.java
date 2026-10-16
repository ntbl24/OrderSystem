package io.order.backend.order.infrastructure.local;

import io.order.backend.inventory.application.port.in.InventoryUseCase;
import io.order.backend.order.application.port.out.InventoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryAdapter implements InventoryPort {
    private final InventoryUseCase inventory;

    @Override
    public void settle(List<Line> lines, String target) {
        inventory.settle(lines.stream()
                .map(line -> new InventoryUseCase.Line(line.productId(), line.quantity())).toList(), target);
    }
}
