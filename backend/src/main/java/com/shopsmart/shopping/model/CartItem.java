package com.shopsmart.shopping.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cart_item")
public class CartItem {

    @EmbeddedId
    private CartItemId id;

    @Column(nullable = false)
    private Integer quantity = 1;

    public CartItem() {}

    public CartItem(UUID customerId, UUID productId, Integer quantity) {
        this.id = new CartItemId(customerId, productId);
        this.quantity = quantity;
    }

    public CartItemId getId() {
        return id;
    }

    public void setId(CartItemId id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
