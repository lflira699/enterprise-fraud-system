package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseHistoryRepository
        extends JpaRepository<CaseHistory, UUID> {

    Optional<CaseHistory> findByHistoryIdAndCaseId(
            UUID historyId,
            UUID caseId
    );

    List<CaseHistory> findByCaseIdOrderByChangedAtDesc(
            UUID caseId
    );
}