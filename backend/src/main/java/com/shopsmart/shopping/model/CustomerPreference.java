package com.shopsmart.shopping.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "customer_preference")
public class CustomerPreference {

    @Id
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "preferred_categories", columnDefinition = "TEXT")
    private String preferredCategories; // JSON array of category names or IDs

    @Column(name = "preferred_brands", columnDefinition = "TEXT")
    private String preferredBrands; // JSON array of brand names

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    public CustomerPreference() {}

    public CustomerPreference(UUID customerId, String preferredCategories, String preferredBrands, BigDecimal budget) {
        this.customerId = customerId;
        this.preferredCategories = preferredCategories;
        this.preferredBrands = preferredBrands;
        this.budget = budget;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(String preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public String getPreferredBrands() {
        return preferredBrands;
    }

    public void setPreferredBrands(String preferredBrands) {
        this.preferredBrands = preferredBrands;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
}
