package com.shopsmart.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExternalAiService {

    private final RestClient restClient;
    private final boolean enabled;
    private final String apiKey;
    private final String model;
    private final String siteUrl;
    private final String appName;

    public ExternalAiService(
            RestClient.Builder restClientBuilder,
            @Value("${shopsmart.ai.enabled:true}") boolean enabled,
            @Value("${shopsmart.ai.api-key:}") String apiKey,
            @Value("${shopsmart.ai.model:openai/gpt-4o-mini}") String model,
            @Value("${shopsmart.ai.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${shopsmart.ai.site-url:http://localhost:5175}") String siteUrl,
            @Value("${shopsmart.ai.app-name:ShopSmart AI}") String appName) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.model = model;
        this.siteUrl = siteUrl;
        this.appName = appName;
    }

    public Optional<String> generateGroundedAnswer(String question, String intent, String groundedContext) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }

        Map<String, Object> request = Map.of(
                "model", model,
                "temperature", 0.2,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are ShopSmart AI. Answer only from the grounded context. "
                                        + "Never invent prices, stock, orders, delivery dates, or policy rules. "
                                    + "For recommendations, the structured Recommendation data is authoritative: "
                                    + "mention only products listed there and never claim the catalog lacks a category "
                                    + "when listed products exist. If the context is insufficient, say that clearly. "
                                    + "Keep answers concise and useful."
                        ),
                        Map.of(
                                "role", "user",
                                "content", "Intent: " + intent + "\nQuestion: " + question
                                        + "\n\nGrounded context:\n" + groundedContext
                        )
                )
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", siteUrl)
                    .header("X-Title", appName)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null || !(response.get("choices") instanceof List<?> choices) || choices.isEmpty()) {
                return Optional.empty();
            }
            Object firstChoice = choices.get(0);
            if (!(firstChoice instanceof Map<?, ?> choice) || !(choice.get("message") instanceof Map<?, ?> message)) {
                return Optional.empty();
            }
            Object content = message.get("content");
            return content instanceof String text && !text.isBlank() ? Optional.of(text.trim()) : Optional.empty();
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }
}
