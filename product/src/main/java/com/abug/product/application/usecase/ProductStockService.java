package com.abug.product.application.usecase;

import com.abug.product.application.port.in.ProductStockUseCase;
import com.abug.product.application.port.in.command.ReserveCommand;
import com.abug.product.application.dto.ReserveResult;
import com.abug.product.application.port.in.command.SettleCommand;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.common.exception.AppException;
import com.abug.product.common.exception.ErrorCode;
import com.abug.product.domain.Product;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockService implements ProductStockUseCase {
    private final ProductRepository products;

    @Override
    @Transactional
    public ReserveResult reserve(ReserveCommand command) {
        Map<String, Integer> quantities = aggregate(command.items());
        Map<String, Product> locked = lockProducts(quantities.keySet());
        List<Snapshot> snapshots = new ArrayList<>();
        locked.forEach((id, product) -> {
            if (product.isDeleted())
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            product.validate();
            product.reserve(quantities.get(id));
            products.save(product);
            snapshots.add(new Snapshot(product.getId(), quantities.get(id), product.getSellerId(), product.getTitle(),
                    product.getSku(), product.getPrice(), product.isDeleted()));
        });
        return new ReserveResult(command.orderId(), snapshots);
    }

    @Override
    @Transactional
    public void settle(SettleCommand command) {
        Map<String, Integer> quantities = aggregate(command.items());
        Map<String, Product> locked = lockProducts(quantities.keySet());
        quantities.forEach((id, quantity) -> {
            Product product = locked.get(id);
            switch (command.action()) {
                case CONSUMED -> product.consume(quantity);
                case RELEASED -> product.release(quantity);
                case RETURNED -> product.restock(quantity);
                default -> throw new AppException(ErrorCode.INVALID_INPUT,"Invalid inventory transition");
            }
            products.save(product);
        });
    }

    private Map<String, Product> lockProducts(Collection<String> ids) {
        Map<String, Product> result = new LinkedHashMap<>();
        ids.stream().distinct().sorted().forEach(id -> result.put(id, products.lockById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND))));
        return result;
    }

    private Map<String, Integer> aggregate(List<Line> items) {
        Map<String, Integer> result = new TreeMap<>();

        for (Line item : items) {
            result.merge(
                    item.productId().toString(),
                    item.quantity(),
                    Math::addExact
            );
        }

        return result;
    }
}
