package com.shopsmart.orders.repository;

import com.shopsmart.orders.model.OrderItem;
import com.shopsmart.orders.model.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByIdOrderId(UUID orderId);
}
