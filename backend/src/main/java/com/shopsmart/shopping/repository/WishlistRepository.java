package com.shopsmart.shopping.repository;

import com.shopsmart.shopping.model.WishlistItem;
import com.shopsmart.shopping.model.WishlistItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, WishlistItemId> {
    List<WishlistItem> findByIdCustomerIdOrderByAddedAtDesc(UUID customerId);
    void deleteByIdCustomerIdAndIdProductId(UUID customerId, UUID productId);
    boolean existsByIdCustomerIdAndIdProductId(UUID customerId, UUID productId);
}
