package com.shopsmart.shopping.dto;

import java.util.List;

public class WishlistResponseDto {
    private List<ProductDto> items;
    private int totalCount;

    public WishlistResponseDto() {}

    public WishlistResponseDto(List<ProductDto> items) {
        this.items = items;
        this.totalCount = items != null ? items.size() : 0;
    }

    public List<ProductDto> getItems() {
        return items;
    }

    public void setItems(List<ProductDto> items) {
        this.items = items;
        this.totalCount = items != null ? items.size() : 0;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
