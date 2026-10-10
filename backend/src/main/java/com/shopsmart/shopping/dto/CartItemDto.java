package com.shopsmart.shopping.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartItemDto {
    private UUID productId;
    private String productName;
    private String brand;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private List<String> images;
    private String availability;

    public CartItemDto() {}

    public CartItemDto(UUID productId, String productName, String brand, BigDecimal unitPrice, Integer quantity,
                       BigDecimal subtotal, List<String> images, String availability) {
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.images = images;
        this.availability = availability;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
