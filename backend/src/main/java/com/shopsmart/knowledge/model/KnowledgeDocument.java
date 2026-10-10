package com.shopsmart.knowledge.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "knowledge_document")
public class KnowledgeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String format; // PDF, MARKDOWN, JSON

    @Column(nullable = false)
    private String version;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String region;

    @Column(name = "approval_status", nullable = false)
    private String approvalStatus = "APPROVED"; // APPROVED, PENDING, REJECTED

    public KnowledgeDocument() {}

    public KnowledgeDocument(UUID id, String name, String format, String version, LocalDate effectiveDate, String category, String region, String approvalStatus) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.version = version;
        this.effectiveDate = effectiveDate;
        this.category = category;
        this.region = region;
        this.approvalStatus = approvalStatus != null ? approvalStatus : "APPROVED";
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
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

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
