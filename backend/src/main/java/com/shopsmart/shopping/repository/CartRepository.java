package com.shopsmart.shopping.repository;

import com.shopsmart.shopping.model.CartItem;
import com.shopsmart.shopping.model.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByIdCustomerId(UUID customerId);
    void deleteByIdCustomerIdAndIdProductId(UUID customerId, UUID productId);
    void deleteByIdCustomerId(UUID customerId);
}
