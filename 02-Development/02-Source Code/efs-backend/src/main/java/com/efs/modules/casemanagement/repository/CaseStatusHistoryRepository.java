package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseStatusHistoryRepository
        extends JpaRepository<CaseStatusHistory, UUID> {

    Optional<CaseStatusHistory> findByHistoryId(
            UUID historyId
    );

    List<CaseStatusHistory> findByCaseIdOrderByChangedAtDesc(
            UUID caseId
    );

    List<CaseStatusHistory>
    findByCaseIdAndCurrentStatusOrderByChangedAtDesc(
            UUID caseId,
            String currentStatus
    );

    List<CaseStatusHistory> findByChangedByOrderByChangedAtDesc(
            UUID changedBy
    );
}