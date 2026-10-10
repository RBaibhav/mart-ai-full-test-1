package com.shopsmart.ai.dto;

import com.shopsmart.knowledge.dto.KnowledgeResponseDto;

import java.util.List;
import java.util.UUID;

public class ChatResponseDto {
    private String answer;
    private String intent; // COORDINATED_ASSISTANCE, RECOMMENDATION, POLICY_LOOKUP, ORDER_TRACKING
    private List<ToolExecutionDto> toolActivity;
    private List<KnowledgeResponseDto.SourceCitation> sources;
    private RecommendationResponseDto recommendations;
    private UUID auditId;

    public ChatResponseDto() {}

    public ChatResponseDto(String answer, String intent, List<ToolExecutionDto> toolActivity,
                           List<KnowledgeResponseDto.SourceCitation> sources,
                           RecommendationResponseDto recommendations, UUID auditId) {
        this.answer = answer;
        this.intent = intent;
        this.toolActivity = toolActivity;
        this.sources = sources;
        this.recommendations = recommendations;
        this.auditId = auditId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<ToolExecutionDto> getToolActivity() {
        return toolActivity;
    }

    public void setToolActivity(List<ToolExecutionDto> toolActivity) {
        this.toolActivity = toolActivity;
    }

    public List<KnowledgeResponseDto.SourceCitation> getSources() {
        return sources;
    }

    public void setSources(List<KnowledgeResponseDto.SourceCitation> sources) {
        this.sources = sources;
    }

    public RecommendationResponseDto getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(RecommendationResponseDto recommendations) {
        this.recommendations = recommendations;
    }

    public UUID getAuditId() {
        return auditId;
    }

    public void setAuditId(UUID auditId) {
        this.auditId = auditId;
    }

    public static class ToolExecutionDto {
        private String tool;
        private String action;
        private String details;

        public ToolExecutionDto() {}

        public ToolExecutionDto(String tool, String action, String details) {
            this.tool = tool;
            this.action = action;
            this.details = details;
        }

        public String getTool() {
            return tool;
        }

        public void setTool(String tool) {
            this.tool = tool;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
