package com.shopsmart.shopping.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDto {
    private List<CartItemDto> items;
    private BigDecimal total;
    private int itemCount;

    public CartResponseDto() {}

    public CartResponseDto(List<CartItemDto> items, BigDecimal total, int itemCount) {
        this.items = items;
        this.total = total;
        this.itemCount = itemCount;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
