package com.shopsmart.shopping.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wishlist_item")
public class WishlistItem {

    @EmbeddedId
    private WishlistItemId id;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    public WishlistItem() {}

    public WishlistItem(UUID customerId, UUID productId) {
        this.id = new WishlistItemId(customerId, productId);
        this.addedAt = LocalDateTime.now();
    }

    public WishlistItemId getId() {
        return id;
    }

    public void setId(WishlistItemId id) {
        this.id = id;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
