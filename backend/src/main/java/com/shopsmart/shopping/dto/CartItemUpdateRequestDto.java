package com.shopsmart.shopping.dto;

import jakarta.validation.constraints.NotNull;

public class CartItemUpdateRequestDto {
    @NotNull(message = "Quantity is required.")
    private Integer quantity;

    public CartItemUpdateRequestDto() {}

    public CartItemUpdateRequestDto(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
