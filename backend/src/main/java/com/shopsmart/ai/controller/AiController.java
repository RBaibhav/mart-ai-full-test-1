package com.shopsmart.ai.controller;

import com.shopsmart.ai.dto.ChatMessageRequestDto;
import com.shopsmart.ai.dto.ChatResponseDto;
import com.shopsmart.ai.dto.RecommendationResponseDto;
import com.shopsmart.ai.model.AuditRecord;
import com.shopsmart.ai.service.AuditService;
import com.shopsmart.ai.service.CoordinatedChatService;
import com.shopsmart.ai.service.RecommendationService;
import com.shopsmart.knowledge.dto.KnowledgeQueryRequestDto;
import com.shopsmart.knowledge.dto.KnowledgeResponseDto;
import com.shopsmart.knowledge.service.RagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final RecommendationService recommendationService;
    private final RagService ragService;
    private final CoordinatedChatService coordinatedChatService;
    private final AuditService auditService;

    public AiController(RecommendationService recommendationService,
                        RagService ragService,
                        CoordinatedChatService coordinatedChatService,
                        AuditService auditService) {
        this.recommendationService = recommendationService;
        this.ragService = ragService;
        this.coordinatedChatService = coordinatedChatService;
        this.auditService = auditService;
    }

    @PostMapping("/recommendations")
    public ResponseEntity<RecommendationResponseDto> getRecommendations(@Valid @RequestBody ChatMessageRequestDto request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request.getMessage()));
    }

    @PostMapping("/knowledge")
    public ResponseEntity<KnowledgeResponseDto> queryKnowledge(@Valid @RequestBody KnowledgeQueryRequestDto request) {
        return ResponseEntity.ok(ragService.queryKnowledge(request.getQuestion(), request.getCategory(), request.getRegion()));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDto> chat(@Valid @RequestBody ChatMessageRequestDto request) {
        return ResponseEntity.ok(coordinatedChatService.processChat(request.getMessage()));
    }

    @GetMapping("/audit")
    public ResponseEntity<List<AuditRecord>> getAudits() {
        return ResponseEntity.ok(auditService.getAllAudits());
    }
}
