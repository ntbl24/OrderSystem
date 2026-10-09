package io.order.backend.order.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.domain.Order;
import io.order.backend.order.dto.OrderDTO;
import io.order.backend.order.infrastructure.OrderRepository;
import io.order.backend.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    

    @Transactional 
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        OrderDTO savedOrderDTO = orderMapper.toDto(orderRepository.save(order));
        return savedOrderDTO;
    }

    public OrderDTO getOrderById(String id){
        return orderRepository.findById(id)
            .map(orderMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public OrderDTO getOrderByOrderNumber(String orderNumber){
        return orderRepository.findByOrderNumber(orderNumber)
            .map(orderMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
    }

    public List<OrderDTO> getOrdersByUserId(String userId){
        return orderMapper.toOrderDTOList(orderRepository.findByUserId(userId));
    }

    public List<OrderDTO> getOrdersByStatus(String status){
        return orderMapper.toOrderDTOList(orderRepository.findByOrderStatus(status));
    }

    @Transactional
    public OrderDTO updateOrderStatus(String id, String orderStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setOrderStatus(orderStatus);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO updateOrder(String id, OrderDTO orderDTO) {
        orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        Order updatedOrder = orderMapper.toEntity(orderDTO);
        updatedOrder.setId(id);
        return orderMapper.toDto(orderRepository.save(updatedOrder));
    }

    @Transactional
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
