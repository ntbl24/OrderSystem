package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.in.CartItemData;
import io.order.backend.cart.application.port.in.UpdateCartCommand;
import io.order.backend.cart.application.port.in.UpdateCartUseCase;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCartService implements UpdateCartUseCase {
    private final CartAccessService access;
    private final CartRepository carts;
    private final CartPricingService pricing;

    public Cart updateCart(CartId id, UpdateCartCommand command) {
        Cart cart = access.byId(id);
        command.items().forEach(i -> {
            var p = pricing.product(i.productId());
            cart.addOrUpdateItem(i.productId(), p.title(), i.quantity(), p.price());
        });
        return save(cart);
    }

    public Cart addItemToCart(UserId userId, CartItemData item) {
        Cart cart = access.byUser(userId);
        var p = pricing.product(item.productId());
        cart.addItem(item.productId(), p.title(), item.quantity(), p.price());
        return save(cart);
    }

    public Cart updateItemQuantity(UserId userId, ProductId productId, int quantity) {
        if (quantity < 0 || quantity > 100000)
            throw new IllegalArgumentException("Invalid quantity");
        Cart cart = access.byUser(userId);
        if (quantity == 0) {
            cart.removeItem(productId);
        } else {
            var p = pricing.product(productId);
            cart.addOrUpdateItem(productId, p.title(), quantity, p.price());
        }
        return save(cart);
    }

    public Cart removeItemFromCart(UserId userId, ProductId productId) {
        return updateItemQuantity(userId, productId, 0);
    }

    private Cart save(Cart cart) {
        pricing.refresh(cart);
        return carts.save(cart);
    }
}
