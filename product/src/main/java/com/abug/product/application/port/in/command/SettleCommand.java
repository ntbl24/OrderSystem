package com.abug.product.application.port.in.command;

import com.abug.product.application.port.in.ProductStockUseCase;
import com.abug.product.common.constant.StockAction;
import com.abug.product.common.exception.AppException;
import com.abug.product.common.exception.ErrorCode;
import com.abug.product.domain.OrderId;

import java.util.List;

public record SettleCommand(
        OrderId orderId,
        List<ProductStockUseCase.Line> items,
        StockAction action
) {
    public SettleCommand{
        if (orderId.value() == null || items == null || items.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        items.forEach(item -> {
            if (item.quantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
        });
    }
}
