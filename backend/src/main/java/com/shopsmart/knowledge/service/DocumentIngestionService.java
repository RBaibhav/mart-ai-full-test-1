package com.shopsmart.knowledge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.common.ApiException;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.knowledge.dto.DocumentUploadResponse;
import com.shopsmart.knowledge.model.DocumentChunk;
import com.shopsmart.knowledge.model.KnowledgeDocument;
import com.shopsmart.knowledge.repository.DocumentChunkRepository;
import com.shopsmart.knowledge.repository.KnowledgeDocumentRepository;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.*;

@Service
public class DocumentIngestionService {

    private final KnowledgeDocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final String adminEmail;
    private final ObjectMapper objectMapper = JsonUtil.getMapper();

    public DocumentIngestionService(KnowledgeDocumentRepository documentRepository,
                                    DocumentChunkRepository chunkRepository,
                                    @Value("${shopsmart.admin-email:admin@shopsmart.com}") String adminEmail) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.adminEmail = adminEmail;
    }

    @Transactional
    public DocumentUploadResponse ingestDocument(MultipartFile file, String version, LocalDate effectiveDate,
                                                 String category, String region) {
        CurrentUser user = UserContext.get();
        if (user != null && !adminEmail.equalsIgnoreCase(user.getEmail())) {
            throw new ApiException("FORBIDDEN", "Only the configured demo uploader can upload documents.", HttpStatus.FORBIDDEN);
        }

        if (file == null || file.isEmpty()) {
            throw new ApiException("INVALID_REQUEST", "Document file cannot be empty.");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String format = detectFormat(originalFilename);

        KnowledgeDocument doc = new KnowledgeDocument();
        doc.setName(originalFilename);
        doc.setFormat(format);
        doc.setVersion(version != null ? version : "1.0");
        doc.setEffectiveDate(effectiveDate != null ? effectiveDate : LocalDate.now());
        doc.setCategory(category != null ? category : "General");
        doc.setRegion(region != null ? region : "GLOBAL");
        doc.setApprovalStatus("APPROVED"); // Only approved documents are used in answers

        KnowledgeDocument savedDoc = documentRepository.save(doc);

        List<ChunkData> rawChunks = extractChunks(file, format);
        if (rawChunks.isEmpty()) {
            throw new ApiException("INVALID_REQUEST", "Unable to extract text content from document.");
        }

        int chunkIndex = 0;
        for (ChunkData c : rawChunks) {
            List<Float> embedding = EmbeddingUtil.generateEmbedding(c.text);
            String embeddingJson = EmbeddingUtil.serializeEmbedding(embedding);

            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocumentId(savedDoc.getId());
            chunk.setChunkIndex(chunkIndex++);
            chunk.setSection(c.section);
            chunk.setText(c.text);
            chunk.setEmbedding(embeddingJson);

            chunkRepository.save(chunk);
        }

        return new DocumentUploadResponse(savedDoc.getId(), savedDoc.getName(), "INGESTED", chunkIndex);
    }

    private String detectFormat(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".md") || lower.endsWith(".markdown")) return "MARKDOWN";
        if (lower.endsWith(".json")) return "JSON";
        throw new ApiException("INVALID_REQUEST", "Unsupported file format. Only PDF, Markdown, and JSON are supported.");
    }

    private List<ChunkData> extractChunks(MultipartFile file, String format) {
        try {
            switch (format) {
                case "PDF":
                    return extractPdfChunks(file.getBytes());
                case "MARKDOWN":
                    return extractMarkdownChunks(new String(file.getBytes()));
                case "JSON":
                    return extractJsonChunks(new String(file.getBytes()));
                default:
                    throw new ApiException("INVALID_REQUEST", "Unsupported file format: " + format);
            }
        } catch (ApiException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ApiException("INVALID_REQUEST", "Failed to parse document: " + e.getMessage());
        }
    }

    public List<ChunkData> extractPdfChunks(byte[] bytes) throws Exception {
        try (PDDocument pdDoc = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(pdDoc);
            return splitIntoTextChunks(fullText, "General");
        }
    }

    public List<ChunkData> extractMarkdownChunks(String markdown) {
        List<ChunkData> chunks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(markdown))) {
            String line;
            String currentSection = "General";
            StringBuilder currentText = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    if (currentText.length() > 0) {
                        chunks.add(new ChunkData(currentSection, currentText.toString().trim()));
                        currentText.setLength(0);
                    }
                    currentSection = line.replaceFirst("^#+\\s*", "").trim();
                } else {
                    currentText.append(line).append("\n");
                }
            }
            if (currentText.length() > 0) {
                chunks.add(new ChunkData(currentSection, currentText.toString().trim()));
            }
        } catch (Exception ignored) {}

        if (chunks.isEmpty() && !markdown.isBlank()) {
            chunks.add(new ChunkData("General", markdown.trim()));
        }
        return chunks;
    }

    public List<ChunkData> extractJsonChunks(String jsonStr) {
        List<ChunkData> chunks = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonStr);
            if (root.has("sections") && root.get("sections").isArray()) {
                for (JsonNode sec : root.get("sections")) {
                    String sectionName = sec.has("section") ? sec.get("section").asText() : "Policy";
                    String text = sec.has("text") ? sec.get("text").asText() : sec.toString();
                    chunks.add(new ChunkData(sectionName, text.trim()));
                }
            } else if (root.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String key = field.getKey();
                    if (!key.equalsIgnoreCase("name") && !key.equalsIgnoreCase("version")
                            && !key.equalsIgnoreCase("effectiveDate") && !key.equalsIgnoreCase("approvalStatus")) {
                        chunks.add(new ChunkData(key, field.getValue().asText(field.getValue().toString())));
                    }
                }
            }
        } catch (Exception e) {
            throw new ApiException("INVALID_REQUEST", "Invalid JSON format: " + e.getMessage());
        }
        return chunks;
    }

    private List<ChunkData> splitIntoTextChunks(String text, String defaultSection) {
        List<ChunkData> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n\\s*\n");
        StringBuilder current = new StringBuilder();
        int chunkNum = 1;

        for (String p : paragraphs) {
            String clean = p.trim();
            if (clean.isBlank()) continue;

            if (current.length() + clean.length() > 600) {
                chunks.add(new ChunkData(defaultSection + " Part " + chunkNum++, current.toString().trim()));
                current.setLength(0);
            }
            current.append(clean).append("\n\n");
        }
        if (current.length() > 0) {
            chunks.add(new ChunkData(defaultSection + (chunkNum > 1 ? " Part " + chunkNum : ""), current.toString().trim()));
        }
        return chunks;
    }

    public static class ChunkData {
        public final String section;
        public final String text;

        public ChunkData(String section, String text) {
            this.section = section;
            this.text = text;
        }
    }
}
