package com.shopsmart.ai.tools;

import com.shopsmart.knowledge.dto.KnowledgeResponseDto;
import com.shopsmart.knowledge.service.RagService;
import org.springframework.stereotype.Component;

@Component
public class PolicyKnowledgeTool {

    private final RagService ragService;

    public PolicyKnowledgeTool(RagService ragService) {
        this.ragService = ragService;
    }

    public KnowledgeResponseDto queryPolicy(String query, String category, String region) {
        return ragService.queryKnowledge(query, category, region);
    }
}
