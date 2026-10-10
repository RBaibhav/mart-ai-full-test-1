package com.shopsmart.ai.service;

import com.shopsmart.ai.model.AuditRecord;
import com.shopsmart.ai.repository.AuditRecordRepository;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditRecordRepository auditRecordRepository;
    private final String defaultModelName;

    public AuditService(AuditRecordRepository auditRecordRepository,
                        @Value("${shopsmart.model-name:shopsmart-gpt-4o-mini}") String defaultModelName) {
        this.auditRecordRepository = auditRecordRepository;
        this.defaultModelName = defaultModelName;
    }

    public AuditRecord recordAudit(String request, String aiResponse, Object toolActivity, Object sourceReferences, String outcome) {
        CurrentUser user = UserContext.get();
        UUID customerId = user != null ? user.getId() : null;

        AuditRecord record = new AuditRecord();
        record.setCustomerId(customerId);
        record.setRequest(request != null ? request : "");
        record.setAiResponse(aiResponse != null ? aiResponse : "");
        record.setModelName(defaultModelName);
        record.setToolActivity(JsonUtil.toJson(toolActivity));
        record.setSourceReferences(JsonUtil.toJson(sourceReferences));
        record.setOutcome(outcome != null ? outcome : "SUCCESS");
        record.setTimestamp(LocalDateTime.now());

        return auditRecordRepository.save(record);
    }

    public List<AuditRecord> getAllAudits() {
        return auditRecordRepository.findAllByOrderByTimestampDesc();
    }
}
