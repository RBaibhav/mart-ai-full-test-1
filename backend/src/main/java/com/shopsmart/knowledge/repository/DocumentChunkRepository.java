package com.shopsmart.knowledge.repository;

import com.shopsmart.knowledge.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
    List<DocumentChunk> findByDocumentId(UUID documentId);
    List<DocumentChunk> findByDocumentIdIn(List<UUID> documentIds);
}
