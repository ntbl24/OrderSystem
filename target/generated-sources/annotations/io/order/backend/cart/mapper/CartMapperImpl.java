package io.order.backend.cart.mapper;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartItem;
import io.order.backend.cart.dto.CartDTO;
import io.order.backend.cart.dto.CartItemDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-03T09:47:04+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public Cart toEntity(CartDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cart cart = new Cart();

        cart.setCouponCode( dto.getCouponCode() );
        cart.setDiscount( dto.getDiscount() );
        cart.setItems( cartItemDTOListToCartItemList( dto.getItems() ) );
        cart.setShippingCost( dto.getShippingCost() );
        cart.setSubTotal( dto.getSubTotal() );
        cart.setTax( dto.getTax() );
        cart.setTotal( dto.getTotal() );
        cart.setUserId( dto.getUserId() );

        return cart;
    }

    @Override
    public CartDTO toDto(Cart entity) {
        if ( entity == null ) {
            return null;
        }

        CartDTO cartDTO = new CartDTO();

        cartDTO.setCouponCode( entity.getCouponCode() );
        cartDTO.setDiscount( entity.getDiscount() );
        cartDTO.setId( entity.getId() );
        cartDTO.setItems( cartItemListToCartItemDTOList( entity.getItems() ) );
        cartDTO.setShippingCost( entity.getShippingCost() );
        cartDTO.setSubTotal( entity.getSubTotal() );
        cartDTO.setTax( entity.getTax() );
        cartDTO.setTotal( entity.getTotal() );
        cartDTO.setUserId( entity.getUserId() );

        return cartDTO;
    }

    protected CartItem cartItemDTOToCartItem(CartItemDTO cartItemDTO) {
        if ( cartItemDTO == null ) {
            return null;
        }

        CartItem cartItem = new CartItem();

        cartItem.setProductId( cartItemDTO.getProductId() );
        cartItem.setProductName( cartItemDTO.getProductName() );
        if ( cartItemDTO.getQuantity() != null ) {
            cartItem.setQuantity( cartItemDTO.getQuantity() );
        }
        cartItem.setSubTotal( cartItemDTO.getSubTotal() );
        cartItem.setUnitPrice( cartItemDTO.getUnitPrice() );

        return cartItem;
    }

    protected List<CartItem> cartItemDTOListToCartItemList(List<CartItemDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItem> list1 = new ArrayList<CartItem>( list.size() );
        for ( CartItemDTO cartItemDTO : list ) {
            list1.add( cartItemDTOToCartItem( cartItemDTO ) );
        }

        return list1;
    }

    protected CartItemDTO cartItemToCartItemDTO(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemDTO cartItemDTO = new CartItemDTO();

        cartItemDTO.setProductId( cartItem.getProductId() );
        cartItemDTO.setProductName( cartItem.getProductName() );
        cartItemDTO.setQuantity( cartItem.getQuantity() );
        cartItemDTO.setSubTotal( cartItem.getSubTotal() );
        cartItemDTO.setUnitPrice( cartItem.getUnitPrice() );

        return cartItemDTO;
    }

    protected List<CartItemDTO> cartItemListToCartItemDTOList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemDTO> list1 = new ArrayList<CartItemDTO>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( cartItemToCartItemDTO( cartItem ) );
        }

        return list1;
    }
}
