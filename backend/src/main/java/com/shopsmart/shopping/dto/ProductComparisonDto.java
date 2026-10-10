package com.shopsmart.shopping.dto;

import java.util.List;
import java.util.Map;

public class ProductComparisonDto {
    private List<ProductDto> products;
    private List<String> commonAttributeKeys;
    private Map<String, List<Object>> comparisonMatrix;

    public ProductComparisonDto() {}

    public ProductComparisonDto(List<ProductDto> products, List<String> commonAttributeKeys, Map<String, List<Object>> comparisonMatrix) {
        this.products = products;
        this.commonAttributeKeys = commonAttributeKeys;
        this.comparisonMatrix = comparisonMatrix;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public List<String> getCommonAttributeKeys() {
        return commonAttributeKeys;
    }

    public void setCommonAttributeKeys(List<String> commonAttributeKeys) {
        this.commonAttributeKeys = commonAttributeKeys;
    }

    public Map<String, List<Object>> getComparisonMatrix() {
        return comparisonMatrix;
    }

    public void setComparisonMatrix(Map<String, List<Object>> comparisonMatrix) {
        this.comparisonMatrix = comparisonMatrix;
    }
}
