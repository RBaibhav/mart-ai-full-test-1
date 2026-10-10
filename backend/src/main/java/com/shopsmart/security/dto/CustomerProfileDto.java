package com.shopsmart.security.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CustomerProfileDto {
    private UUID id;
    private String name;
    private String email;
    private String membershipTier;
    private List<String> preferredCategories;
    private List<String> preferredBrands;
    private BigDecimal budget;

    public CustomerProfileDto() {}

    public CustomerProfileDto(UUID id, String name, String email, String membershipTier, List<String> preferredCategories, List<String> preferredBrands, BigDecimal budget) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipTier = membershipTier;
        this.preferredCategories = preferredCategories;
        this.preferredBrands = preferredBrands;
        this.budget = budget;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public List<String> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(List<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public List<String> getPreferredBrands() {
        return preferredBrands;
    }

    public void setPreferredBrands(List<String> preferredBrands) {
        this.preferredBrands = preferredBrands;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
}
