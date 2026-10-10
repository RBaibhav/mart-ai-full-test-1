package com.shopsmart.ai.repository;

import com.shopsmart.ai.model.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, UUID> {
    List<AuditRecord> findAllByOrderByTimestampDesc();
    List<AuditRecord> findByCustomerIdOrderByTimestampDesc(UUID customerId);
}
