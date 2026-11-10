package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.in.CreateCartCommand;
import io.order.backend.cart.application.port.in.CreateCartUseCase;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.domain.Cart;
import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCartService implements CreateCartUseCase {
    private final CartAccessService accessService;
    private final CartRepository cartRepository;
    private final CartPricingService pricing;

    @Override
    @Transactional
    public Cart createCart(CreateCartCommand command) {
        accessService.owner(command.userId());
        if (cartRepository.findByUserId(command.userId()).isPresent())
            throw new AppException(ErrorCode.CART_EXISTED);
        Cart cart = Cart.create(command.userId());
        command.items().forEach(item -> {
            var product = pricing.product(item.productId());
            cart.addItem(item.productId(), product.title(), item.quantity(), product.price());
        });
        return cartRepository.save(cart);
    }
}
