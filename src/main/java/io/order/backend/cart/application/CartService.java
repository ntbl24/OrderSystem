package io.order.backend.cart.application;

import io.order.backend.cart.application.port.in.CartUseCase;

import io.order.backend.cart.domain.*;
import io.order.backend.cart.dto.*;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.mapper.CartMapper;
import io.order.backend.cart.application.port.out.ProductCatalogPort;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements CartUseCase {
    private final CartRepository carts;
    private final CartMapper mapper;
    private final CurrentBuyerPort buyer;
    private final ProductCatalogPort products;

    private void owner(String id) {
        if (!buyer.buyerId().equals(id))
            throw new ResourceNotFoundException("Cart not found");
    }

    private Cart owned(Cart cart) {
        owner(cart.getUserId());
        return cart;
    }

    private Cart byId(String id) {
        return owned(carts.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found")));
    }

    private Cart byUser(String id) {
        owner(id);
        return carts.findByUserId(id).orElseGet(() -> {
            var c = new Cart();
            c.setUserId(id);
            return c;
        });
    }

    private CartItem line(String id, int quantity) {
        if (quantity < 1 || quantity > 100000)
            throw new IllegalArgumentException("Invalid quantity");
        var p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        var item = new CartItem();
        item.setProductId(id);
        item.setProductName(p.title());
        item.setQuantity(quantity);
        item.setUnitPrice(p.price());
        item.setSubTotal(p.price().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    private CartDTO save(Cart cart) {
        if (cart.getItems().size() > 100)
            throw new IllegalArgumentException("Too many cart items");
        cart.setItems(cart.getItems().stream().map(i -> line(i.getProductId(), i.getQuantity()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        cart.setSubTotal(cart.getItems().stream().map(CartItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        cart.setTotal(cart.getSubTotal());
        cart.setTax(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setShippingCost(BigDecimal.ZERO);
        cart.setCouponCode(null);
        return mapper.toDto(carts.save(cart));
    }

    public CartDTO createCart(CartDTO dto) {
        owner(dto.getUserId());
        if (carts.findByUserId(dto.getUserId()).isPresent())
            throw new IllegalStateException("Cart already exists");
        Cart c = new Cart();
        c.setUserId(dto.getUserId());
        c.setItems(dto.getItems().stream().map(i -> line(i.getProductId(), i.getQuantity()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return save(c);
    }

    public CartDTO updateCart(String id, CartDTO dto) {
        Cart c = byId(id);
        owner(dto.getUserId());
        c.setItems(dto.getItems().stream().map(i -> line(i.getProductId(), i.getQuantity()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return save(c);
    }

    public void deleteCart(String id) {
        byId(id);
        carts.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<CartDTO> getCartById(String id) {
        return Optional.of(mapper.toDto(byId(id)));
    }

    @Transactional(readOnly = true)
    public Optional<CartDTO> getCartByUserId(String id) {
        owner(id);
        return carts.findByUserId(id).map(mapper::toDto);
    }

    public CartDTO addItemToCart(String userId, CartItemDTO item) {
        Cart c = byUser(userId);
        var found = c.getItems().stream().filter(i -> i.getProductId().equals(item.getProductId())).findFirst();
        int quantity = item.getQuantity();
        if (found.isPresent())
            quantity = Math.addExact(quantity, found.get().getQuantity());
        c.getItems().removeIf(i -> i.getProductId().equals(item.getProductId()));
        c.getItems().add(line(item.getProductId(), quantity));
        return save(c);
    }

    public CartDTO updateItemQuantity(String userId, String productId, int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Invalid quantity");
        Cart c = byUser(userId);
        c.getItems().removeIf(i -> i.getProductId().equals(productId));
        if (quantity > 0)
            c.getItems().add(line(productId, quantity));
        return save(c);
    }

    public CartDTO removeItemFromCart(String userId, String productId) {
        return updateItemQuantity(userId, productId, 0);
    }

    public void clearCart(String userId) {
        owner(userId);
        carts.deleteByUserId(userId);
    }
}
