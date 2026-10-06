package io.order.backend.order.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.order.backend.order.domain.Order;

@Repository
public interface MongoOrderRepository extends MongoRepository<Order, String>, OrderRepository {

    List<Order> findByUserId(String userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByOrderStatus(String status);
}
