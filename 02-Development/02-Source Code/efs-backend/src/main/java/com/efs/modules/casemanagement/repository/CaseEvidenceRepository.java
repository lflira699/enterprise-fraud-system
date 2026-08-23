package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseEvidenceRepository
        extends JpaRepository<CaseEvidence, UUID> {

    Optional<CaseEvidence> findByEvidenceId(
            UUID evidenceId
    );

    List<CaseEvidence> findByCaseIdOrderByUploadedAtDesc(
            UUID caseId
    );

    List<CaseEvidence> findByCaseIdAndEvidenceTypeOrderByUploadedAtDesc(
            UUID caseId,
            String evidenceType
    );

    List<CaseEvidence> findByTransactionIdOrderByUploadedAtDesc(
            UUID transactionId
    );
}