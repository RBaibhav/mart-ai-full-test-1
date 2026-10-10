package com.shopsmart.ai.dto;

import java.math.BigDecimal;
import java.util.List;

public class RecommendationResponseDto {
    private String interpretedRequirement;
    private BigDecimal budget;
    private List<ProductRecommendationItem> recommendations;
    private String clarifyingQuestion;

    public RecommendationResponseDto() {}

    public RecommendationResponseDto(String interpretedRequirement, BigDecimal budget,
                                     List<ProductRecommendationItem> recommendations,
                                     String clarifyingQuestion) {
        this.interpretedRequirement = interpretedRequirement;
        this.budget = budget;
        this.recommendations = recommendations;
        this.clarifyingQuestion = clarifyingQuestion;
    }

    public String getInterpretedRequirement() {
        return interpretedRequirement;
    }

    public void setInterpretedRequirement(String interpretedRequirement) {
        this.interpretedRequirement = interpretedRequirement;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public List<ProductRecommendationItem> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<ProductRecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }

    public String getClarifyingQuestion() {
        return clarifyingQuestion;
    }

    public void setClarifyingQuestion(String clarifyingQuestion) {
        this.clarifyingQuestion = clarifyingQuestion;
    }

    public static class ProductRecommendationItem {
        private String productId;
        private String productName;
        private String reason;
        private String availability; // IN_STOCK, OUT_OF_STOCK

        public ProductRecommendationItem() {}

        public ProductRecommendationItem(String productId, String productName, String reason, String availability) {
            this.productId = productId;
            this.productName = productName;
            this.reason = reason;
            this.availability = availability;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }
    }
}
