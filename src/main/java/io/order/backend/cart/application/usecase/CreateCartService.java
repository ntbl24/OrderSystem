package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.in.CreateCartCommand;
import io.order.backend.cart.application.port.in.CreateCartUseCase;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.domain.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCartService implements CreateCartUseCase {
    private final CartAccessService accessService;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public Cart createCart(CreateCartCommand command) {
        accessService.owner(command.userId());
        if (cartRepository.findByUserId(command.userId()).isPresent())
            throw new IllegalStateException("Cart already exists");
        Cart cart = Cart.create(command.userId());
        command.items().forEach(item ->
                cart.addItem(item.productId(), item.productName(), item.quantity(), item.unitPrice()));
        return cartRepository.save(cart);
    }
}
