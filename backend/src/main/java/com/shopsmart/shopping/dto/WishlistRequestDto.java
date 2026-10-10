package com.shopsmart.shopping.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class WishlistRequestDto {
    @NotNull(message = "Product ID is required.")
    private UUID productId;

    public WishlistRequestDto() {}

    public WishlistRequestDto(UUID productId) {
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
