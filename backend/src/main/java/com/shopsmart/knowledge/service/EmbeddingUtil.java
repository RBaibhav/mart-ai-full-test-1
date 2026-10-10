package com.shopsmart.knowledge.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.common.JsonUtil;

import java.util.*;

public final class EmbeddingUtil {

    private static final int VECTOR_DIM = 384;
    private static final ObjectMapper MAPPER = JsonUtil.getMapper();

    private EmbeddingUtil() {}

    /**
     * Generates a normalized semantic vector (dim=384) for the given text.
     * Uses semantic hashing over character trigrams and token stems.
     */
    public static List<Float> generateEmbedding(String text) {
        float[] vector = new float[VECTOR_DIM];
        if (text == null || text.isBlank()) {
            return toList(vector);
        }

        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] words = cleaned.split("\\s+");

        for (String word : words) {
            if (word.isBlank()) continue;

            // Word-level hash contribution
            int wordHash = Math.abs(word.hashCode()) % VECTOR_DIM;
            vector[wordHash] += 2.0f;

            // Trigram contributions
            String padded = "$" + word + "$";
            for (int i = 0; i < padded.length() - 2; i++) {
                String trigram = padded.substring(i, i + 3);
                int triHash = Math.abs(trigram.hashCode()) % VECTOR_DIM;
                vector[triHash] += 1.0f;
            }
        }

        // Normalize vector (L2 norm)
        float norm = 0.0f;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < VECTOR_DIM; i++) {
                vector[i] /= norm;
            }
        }

        return toList(vector);
    }

    public static String serializeEmbedding(List<Float> embedding) {
        try {
            return MAPPER.writeValueAsString(embedding);
        } catch (Exception e) {
            return "[]";
        }
    }

    public static List<Float> deserializeEmbedding(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return MAPPER.readValue(json, new TypeReference<List<Float>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static double cosineSimilarity(List<Float> v1, List<Float> v2) {
        if (v1 == null || v2 == null || v1.isEmpty() || v2.isEmpty()) return 0.0;
        int minSize = Math.min(v1.size(), v2.size());
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < minSize; i++) {
            float a = v1.get(i);
            float b = v2.get(i);
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }
        if (normA <= 0 || normB <= 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static List<Float> toList(float[] array) {
        List<Float> list = new ArrayList<>(array.length);
        for (float f : array) {
            list.add(f);
        }
        return list;
    }
}
