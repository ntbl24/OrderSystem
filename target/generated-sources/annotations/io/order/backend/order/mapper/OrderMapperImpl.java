package io.order.backend.order.mapper;

import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderItem;
import io.order.backend.order.dto.OrderDTO;
import io.order.backend.order.dto.OrderItemDTO;
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
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toEntity(OrderDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Order order = new Order();

        order.setUserId( dto.getUserId() );
        order.setOrderNumber( dto.getOrderNumber() );
        order.setItems( orderItemDTOListToOrderItemList( dto.getItems() ) );
        order.setSubtotal( dto.getSubtotal() );
        order.setShippingCost( dto.getShippingCost() );
        order.setShippingAddress( dto.getShippingAddress() );
        order.setPaymentMethod( dto.getPaymentMethod() );
        order.setPaymentStatus( dto.getPaymentStatus() );
        order.setOrderStatus( dto.getOrderStatus() );
        order.setOrderDate( dto.getOrderDate() );
        order.setEstimatedDeliveryDate( dto.getEstimatedDeliveryDate() );
        order.setNotes( dto.getNotes() );

        return order;
    }

    @Override
    public OrderDTO toDto(Order entity) {
        if ( entity == null ) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setEstimatedDeliveryDate( entity.getEstimatedDeliveryDate() );
        orderDTO.setItems( orderItemListToOrderItemDTOList( entity.getItems() ) );
        orderDTO.setNotes( entity.getNotes() );
        orderDTO.setOrderDate( entity.getOrderDate() );
        orderDTO.setOrderNumber( entity.getOrderNumber() );
        orderDTO.setOrderStatus( entity.getOrderStatus() );
        orderDTO.setPaymentMethod( entity.getPaymentMethod() );
        orderDTO.setPaymentStatus( entity.getPaymentStatus() );
        orderDTO.setShippingAddress( entity.getShippingAddress() );
        orderDTO.setShippingCost( entity.getShippingCost() );
        orderDTO.setSubtotal( entity.getSubtotal() );
        orderDTO.setUserId( entity.getUserId() );

        return orderDTO;
    }

    @Override
    public List<OrderDTO> toOrderDTOList(List<Order> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<OrderDTO> list = new ArrayList<OrderDTO>( entityList.size() );
        for ( Order order : entityList ) {
            list.add( toDto( order ) );
        }

        return list;
    }

    @Override
    public OrderItem toOrderItem(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setProductId( item.getProductId() );
        orderItem.setProductName( item.getProductName() );
        orderItem.setQuantity( item.getQuantity() );
        orderItem.setSubtotal( item.getSubtotal() );
        orderItem.setUnitPrice( item.getUnitPrice() );

        return orderItem;
    }

    @Override
    public OrderItemDTO toOrderItemDTO(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        OrderItemDTO orderItemDTO = new OrderItemDTO();

        orderItemDTO.setProductId( item.getProductId() );
        orderItemDTO.setProductName( item.getProductName() );
        orderItemDTO.setQuantity( item.getQuantity() );
        orderItemDTO.setSubtotal( item.getSubtotal() );
        orderItemDTO.setUnitPrice( item.getUnitPrice() );

        return orderItemDTO;
    }

    protected OrderItem orderItemDTOToOrderItem(OrderItemDTO orderItemDTO) {
        if ( orderItemDTO == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setProductId( orderItemDTO.getProductId() );
        orderItem.setProductName( orderItemDTO.getProductName() );
        orderItem.setQuantity( orderItemDTO.getQuantity() );
        orderItem.setSubtotal( orderItemDTO.getSubtotal() );
        orderItem.setUnitPrice( orderItemDTO.getUnitPrice() );

        return orderItem;
    }

    protected List<OrderItem> orderItemDTOListToOrderItemList(List<OrderItemDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItem> list1 = new ArrayList<OrderItem>( list.size() );
        for ( OrderItemDTO orderItemDTO : list ) {
            list1.add( orderItemDTOToOrderItem( orderItemDTO ) );
        }

        return list1;
    }

    protected List<OrderItemDTO> orderItemListToOrderItemDTOList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemDTO> list1 = new ArrayList<OrderItemDTO>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toOrderItemDTO( orderItem ) );
        }

        return list1;
    }
}
