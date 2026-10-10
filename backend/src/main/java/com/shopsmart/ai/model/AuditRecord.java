package com.shopsmart.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_record")
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String request;

    @Column(name = "ai_response", columnDefinition = "TEXT", nullable = false)
    private String aiResponse;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "tool_activity", columnDefinition = "TEXT")
    private String toolActivity; // JSON Array of tools invoked and parameters

    @Column(name = "source_references", columnDefinition = "TEXT")
    private String sourceReferences; // JSON Array of documents / chunks cited

    @Column(nullable = false)
    private String outcome; // SUCCESS, NO_MATCH, CLARIFICATION_REQUIRED, ERROR

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditRecord() {}

    public AuditRecord(UUID id, UUID customerId, String request, String aiResponse, String modelName, String toolActivity, String sourceReferences, String outcome) {
        this.id = id;
        this.customerId = customerId;
        this.request = request;
        this.aiResponse = aiResponse;
        this.modelName = modelName;
        this.toolActivity = toolActivity;
        this.sourceReferences = sourceReferences;
        this.outcome = outcome;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getToolActivity() {
        return toolActivity;
    }

    public void setToolActivity(String toolActivity) {
        this.toolActivity = toolActivity;
    }

    public String getSourceReferences() {
        return sourceReferences;
    }

    public void setSourceReferences(String sourceReferences) {
        this.sourceReferences = sourceReferences;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
