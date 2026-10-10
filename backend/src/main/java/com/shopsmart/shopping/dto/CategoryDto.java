package com.shopsmart.shopping.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryDto {
    private UUID id;
    private String name;
    private UUID parentCategoryId;
    private List<CategoryDto> subcategories = new ArrayList<>();

    public CategoryDto() {}

    public CategoryDto(UUID id, String name, UUID parentCategoryId) {
        this.id = id;
        this.name = name;
        this.parentCategoryId = parentCategoryId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public List<CategoryDto> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryDto> subcategories) {
        this.subcategories = subcategories;
    }
}
