package com.shopsmart.knowledge.service;

import com.shopsmart.knowledge.dto.KnowledgeResponseDto;
import com.shopsmart.knowledge.model.DocumentChunk;
import com.shopsmart.knowledge.model.KnowledgeDocument;
import com.shopsmart.knowledge.repository.DocumentChunkRepository;
import com.shopsmart.knowledge.repository.KnowledgeDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final KnowledgeDocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;

    public RagService(KnowledgeDocumentRepository documentRepository,
                      DocumentChunkRepository chunkRepository) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
    }

    public KnowledgeResponseDto queryKnowledge(String question, String categoryContext, String regionContext) {
        if (question == null || question.isBlank()) {
            return new KnowledgeResponseDto(
                    "Please provide a question to search policy documents.",
                    "LOW",
                    Collections.emptyList(),
                    true
            );
        }

        // Only approved documents are used in answers (Basic safeguard)
        List<KnowledgeDocument> approvedDocs = documentRepository.findByApprovalStatus("APPROVED");
        if (approvedDocs.isEmpty()) {
            return new KnowledgeResponseDto(
                    "No approved policy documents are currently available.",
                    "LOW",
                    Collections.emptyList(),
                    true
            );
        }

        if (categoryContext != null && !categoryContext.isBlank()) {
            List<KnowledgeDocument> filtered = approvedDocs.stream()
                    .filter(d -> d.getCategory().equalsIgnoreCase(categoryContext))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                approvedDocs = filtered;
            }
        }

        if (regionContext != null && !regionContext.isBlank()) {
            List<KnowledgeDocument> filtered = approvedDocs.stream()
                    .filter(d -> d.getRegion().equalsIgnoreCase(regionContext) || d.getRegion().equalsIgnoreCase("GLOBAL"))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                approvedDocs = filtered;
            }
        }

        Map<UUID, KnowledgeDocument> docMap = approvedDocs.stream()
                .collect(Collectors.toMap(KnowledgeDocument::getId, d -> d));

        List<DocumentChunk> chunks = chunkRepository.findByDocumentIdIn(new ArrayList<>(docMap.keySet()));
        if (chunks.isEmpty()) {
            return new KnowledgeResponseDto(
                    "No policy sections found matching your criteria.",
                    "LOW",
                    Collections.emptyList(),
                    true
            );
        }

        List<Float> queryEmbedding = EmbeddingUtil.generateEmbedding(question);
        String questionLower = question.toLowerCase();

        List<ScoredChunk> scoredChunks = new ArrayList<>();
        for (DocumentChunk chunk : chunks) {
            List<Float> chunkVec = EmbeddingUtil.deserializeEmbedding(chunk.getEmbedding());
            double sim = EmbeddingUtil.cosineSimilarity(queryEmbedding, chunkVec);

            // Boost score if keyword overlap exists
            String textLower = chunk.getText().toLowerCase();
            String sectionLower = chunk.getSection().toLowerCase();
            String[] queryTerms = questionLower.split("\\s+");
            int matches = 0;
            for (String term : queryTerms) {
                if (term.length() > 3 && (textLower.contains(term) || sectionLower.contains(term))) {
                    matches++;
                }
            }
            double combinedScore = sim + (matches * 0.15);

            scoredChunks.add(new ScoredChunk(chunk, combinedScore));
        }

        scoredChunks.sort((a, b) -> Double.compare(b.score, a.score));

        ScoredChunk topChunk = scoredChunks.get(0);
        KnowledgeDocument parentDoc = docMap.get(topChunk.chunk.getDocumentId());

        String confidence;
        boolean followUpRequired = false;
        String answer;
        List<KnowledgeResponseDto.SourceCitation> sources = new ArrayList<>();

        if (topChunk.score >= 0.45) {
            confidence = "HIGH";
            answer = topChunk.chunk.getText();
            sources.add(new KnowledgeResponseDto.SourceCitation(
                    parentDoc != null ? parentDoc.getName() : "Policy Document",
                    topChunk.chunk.getSection(),
                    parentDoc != null ? parentDoc.getVersion() : "1.0"
            ));
        } else if (topChunk.score >= 0.25) {
            confidence = "MEDIUM";
            answer = topChunk.chunk.getText();
            sources.add(new KnowledgeResponseDto.SourceCitation(
                    parentDoc != null ? parentDoc.getName() : "Policy Document",
                    topChunk.chunk.getSection(),
                    parentDoc != null ? parentDoc.getVersion() : "1.0"
            ));
        } else {
            confidence = "LOW";
            answer = "The approved policy documents do not contain sufficient evidence to answer your question definitively. Please provide additional details or consult customer support.";
            followUpRequired = true;
        }

        return new KnowledgeResponseDto(answer, confidence, sources, followUpRequired);
    }

    private static class ScoredChunk {
        final DocumentChunk chunk;
        final double score;

        ScoredChunk(DocumentChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }
}
