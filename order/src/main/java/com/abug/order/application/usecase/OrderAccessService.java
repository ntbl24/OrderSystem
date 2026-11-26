package com.abug.order.application.usecase;

import com.abug.order.application.port.out.CurrentUserPort;
import com.abug.order.common.exception.AppException;
import com.abug.order.common.exception.ErrorCode;
import com.abug.order.domain.Order;
import com.abug.order.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderAccessService {

    private final CurrentUserPort currentUserPort;

    public void owner(UserId id) {
        UserId buyerId = UserId.of(currentUserPort.currentUserId());
        if (!buyerId.equals(id))
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
    }

    public Order owned(Order order) {
        if (!currentUserPort.currentUserId().equals(order.getUserId().value()))
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        return order;
    }
}
