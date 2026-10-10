package com.shopsmart.knowledge.dto;

import java.util.List;

public class KnowledgeResponseDto {
    private String answer;
    private String confidence; // HIGH, MEDIUM, LOW
    private List<SourceCitation> sources;
    private boolean followUpRequired;

    public KnowledgeResponseDto() {}

    public KnowledgeResponseDto(String answer, String confidence, List<SourceCitation> sources, boolean followUpRequired) {
        this.answer = answer;
        this.confidence = confidence;
        this.sources = sources;
        this.followUpRequired = followUpRequired;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public List<SourceCitation> getSources() {
        return sources;
    }

    public void setSources(List<SourceCitation> sources) {
        this.sources = sources;
    }

    public boolean isFollowUpRequired() {
        return followUpRequired;
    }

    public void setFollowUpRequired(boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }

    public static class SourceCitation {
        private String document;
        private String section;
        private String version;

        public SourceCitation() {}

        public SourceCitation(String document, String section, String version) {
            this.document = document;
            this.section = section;
            this.version = version;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
