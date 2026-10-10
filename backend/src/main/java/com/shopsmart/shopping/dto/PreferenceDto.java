package com.shopsmart.shopping.dto;

import java.math.BigDecimal;
import java.util.List;

public class PreferenceDto {
    private List<String> categories;
    private List<String> brands;
    private BigDecimal budget;

    public PreferenceDto() {}

    public PreferenceDto(List<String> categories, List<String> brands, BigDecimal budget) {
        this.categories = categories;
        this.brands = brands;
        this.budget = budget;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
}
