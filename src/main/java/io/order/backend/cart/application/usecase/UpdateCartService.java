package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.in.UpdateCartCommand;
import io.order.backend.cart.application.port.in.UpdateCartUseCase;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.application.port.out.ProductCatalogPort;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.cart.domain.CartItem;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateCartService implements UpdateCartUseCase {
    private final CartAccessService accessService;
    private final CartRepository cartRepository;
    private final ProductCatalogPort products;

    @Override
    public Cart updateCart(String id, UpdateCartCommand command) {
        Cart cart = accessService.byId(CartId.of(id));
        command.items().stream()
                .forEach(i -> {
                    var p = products.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                    cart.addOrUpdateItem(i.productId(), p.title(), i.quantity(), p.price());
                });

        return cartRepository.save(cart);
    }
}
