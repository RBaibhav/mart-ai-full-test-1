package com.shopsmart.ai.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatMessageRequestDto {
    @NotBlank(message = "Message is required.")
    private String message;

    public ChatMessageRequestDto() {}

    public ChatMessageRequestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
