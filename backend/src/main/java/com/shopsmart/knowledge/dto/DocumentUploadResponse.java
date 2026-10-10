package com.shopsmart.knowledge.dto;

import java.util.UUID;

public class DocumentUploadResponse {
    private UUID documentId;
    private String name;
    private String status;
    private int chunkCount;

    public DocumentUploadResponse() {}

    public DocumentUploadResponse(UUID documentId, String name, String status, int chunkCount) {
        this.documentId = documentId;
        this.name = name;
        this.status = status;
        this.chunkCount = chunkCount;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }
}
