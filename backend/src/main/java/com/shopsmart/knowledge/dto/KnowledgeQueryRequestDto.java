package com.shopsmart.knowledge.dto;

import jakarta.validation.constraints.NotBlank;

public class KnowledgeQueryRequestDto {
    @NotBlank(message = "Question is required.")
    private String question;
    private String category;
    private String region;

    public KnowledgeQueryRequestDto() {}

    public KnowledgeQueryRequestDto(String question, String category, String region) {
        this.question = question;
        this.category = category;
        this.region = region;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
