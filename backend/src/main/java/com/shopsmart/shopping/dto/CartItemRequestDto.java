package com.shopsmart.shopping.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CartItemRequestDto {
    @NotNull(message = "Product ID is required.")
    private UUID productId;

    @NotNull(message = "Quantity is required.")
    private Integer quantity;

    public CartItemRequestDto() {}

    public CartItemRequestDto(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
