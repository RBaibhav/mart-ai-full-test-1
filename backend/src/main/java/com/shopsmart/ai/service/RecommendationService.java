package com.shopsmart.ai.service;

import com.shopsmart.ai.dto.RecommendationResponseDto;
import com.shopsmart.ai.tools.ProductCatalogTool;
import com.shopsmart.ai.tools.WarehouseInventoryTool;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import com.shopsmart.shopping.model.CustomerPreference;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.CustomerPreferenceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecommendationService {

    private final ProductCatalogTool catalogTool;
    private final WarehouseInventoryTool inventoryTool;
    private final CustomerPreferenceRepository preferenceRepository;
    private final AuditService auditService;

    public RecommendationService(ProductCatalogTool catalogTool,
                                 WarehouseInventoryTool inventoryTool,
                                 CustomerPreferenceRepository preferenceRepository,
                                 AuditService auditService) {
        this.catalogTool = catalogTool;
        this.inventoryTool = inventoryTool;
        this.preferenceRepository = preferenceRepository;
        this.auditService = auditService;
    }

    public RecommendationResponseDto getRecommendations(String message) {
        if (message == null || message.trim().isBlank()) {
            return new RecommendationResponseDto(
                    "Unspecified request",
                    null,
                    Collections.emptyList(),
                    "What kind of product are you looking for, and what is your budget?"
            );
        }

        // 1. Read message and saved preferences
        CurrentUser user = UserContext.get();
        CustomerPreference savedPref = (user != null) ? preferenceRepository.findById(user.getId()).orElse(null) : null;

        // 2. Extract budget, use, and specifications
        BigDecimal budget = extractBudget(message);
        if (budget == null && savedPref != null && savedPref.getBudget() != null) {
            budget = savedPref.getBudget();
        }

        String requirement = interpretRequirement(message);

        // Check if essential details are missing (e.g. no identifiable product or purpose)
        if (isVagueRequest(message)) {
            RecommendationResponseDto clarification = new RecommendationResponseDto(
                    requirement,
                    budget,
                    Collections.emptyList(),
                    "Could you clarify the type of product (e.g. laptop, monitor, headphones) or primary use case you need?"
            );
            auditService.recordAudit(message, JsonUtil.toJson(clarification),
                    List.of(Map.of("tool", "ProductCatalogTool", "status", "SKIPPED_DUE_TO_AMBIGUITY")),
                    Collections.emptyList(), "CLARIFICATION_REQUIRED");
            return clarification;
        }

        // 3. Search catalog products with required filters & budget constraint
        String searchTerm = extractSearchKeyword(message);
        List<Product> matches = catalogTool.searchCatalog(searchTerm, budget);

        // If no matches with specific term, try broader catalog search under budget
        if (matches.isEmpty() && budget != null) {
            matches = catalogTool.searchCatalog("", budget);
        }

        // 4. Check inventory and explain recommendations using actual product specifications
        List<RecommendationResponseDto.ProductRecommendationItem> recommendationItems = new ArrayList<>();
        List<Map<String, Object>> toolLogs = new ArrayList<>();

        for (Product product : matches) {
            String availability = inventoryTool.checkStockStatus(product.getId());
            toolLogs.add(Map.of(
                    "tool", "WarehouseInventoryTool",
                    "productId", product.getId(),
                    "status", availability
            ));

            String reason = buildReasonFromSpecifications(product);
            recommendationItems.add(new RecommendationResponseDto.ProductRecommendationItem(
                    product.getId().toString(),
                    product.getName(),
                    reason,
                    availability
            ));

            if (recommendationItems.size() >= 3) {
                break; // Top 3 recommendations
            }
        }

        // 5. Validate and return structured response
        RecommendationResponseDto response = new RecommendationResponseDto(
                requirement,
                budget,
                recommendationItems,
                null
        );

        auditService.recordAudit(
                message,
                JsonUtil.toJson(response),
                toolLogs,
                recommendationItems.stream().map(RecommendationResponseDto.ProductRecommendationItem::getProductId).toList(),
                recommendationItems.isEmpty() ? "NO_MATCH" : "SUCCESS"
        );

        return response;
    }

    private BigDecimal extractBudget(String text) {
        Pattern pattern = Pattern.compile("(?:\\$|budget(?:\\s+of)?\\s+|under\\s+|max\\s+)?(\\d+(?:\\.\\d+)?)(?:\\s*\\$|\\s*dollars)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        BigDecimal found = null;
        while (matcher.find()) {
            try {
                String numStr = matcher.group(1);
                double val = Double.parseDouble(numStr);
                if (val >= 50) { // realistic product price threshold
                    found = BigDecimal.valueOf(val);
                }
            } catch (Exception ignored) {}
        }
        return found;
    }

    private String interpretRequirement(String message) {
        String trimmed = message.trim();
        if (trimmed.toLowerCase().contains("java") || trimmed.toLowerCase().contains("ai") || trimmed.toLowerCase().contains("laptop")) {
            return "Laptop for Java development and AI learning";
        }
        if (trimmed.toLowerCase().contains("gaming")) {
            return "High performance device for gaming";
        }
        if (trimmed.toLowerCase().contains("audio") || trimmed.toLowerCase().contains("headphone")) {
            return "Audio equipment for clear listening";
        }
        return trimmed.length() > 60 ? trimmed.substring(0, 60) + "..." : trimmed;
    }

    private boolean isVagueRequest(String message) {
        String lower = message.toLowerCase().trim();
        return lower.equals("hi") || lower.equals("hello") || lower.equals("help")
                || lower.equals("i want to buy something") || lower.equals("recommend something");
    }

    private String extractSearchKeyword(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("laptop") || lower.contains("dell") || lower.contains("macbook") || lower.contains("thinkpad")) {
            return "laptop";
        }
        if (lower.contains("headphone") || lower.contains("audio") || lower.contains("sony")) {
            return "headphone";
        }
        if (lower.contains("monitor") || lower.contains("screen") || lower.contains("display")) {
            return "monitor";
        }
        if (lower.contains("phone") || lower.contains("smartphone") || lower.contains("iphone") || lower.contains("samsung")) {
            return "phone";
        }
        return "";
    }

    private String buildReasonFromSpecifications(Product product) {
        Map<String, Object> specs = JsonUtil.toMap(product.getSpecifications());
        List<String> highlights = new ArrayList<>();

        if (specs.containsKey("RAM")) highlights.add(specs.get("RAM").toString() + " RAM");
        if (specs.containsKey("Processor")) highlights.add(specs.get("Processor").toString() + " processor");
        if (specs.containsKey("Storage")) highlights.add(specs.get("Storage").toString() + " storage");
        if (specs.containsKey("Battery")) highlights.add(specs.get("Battery").toString() + " battery life");
        if (specs.containsKey("Noise Cancellation")) highlights.add("Active noise cancellation");

        if (highlights.isEmpty()) {
            return product.getDescription() != null && !product.getDescription().isBlank()
                    ? product.getDescription()
                    : "High quality " + product.getBrand() + " product matching your requirements";
        }

        return String.join(", ", highlights);
    }
}
