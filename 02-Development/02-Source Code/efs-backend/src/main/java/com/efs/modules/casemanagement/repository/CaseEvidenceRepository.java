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

    Optional<CaseEvidence> findByEvidenceIdAndDeletedAtIsNull(
            UUID evidenceId
    );

    List<CaseEvidence> findByCaseIdAndDeletedAtIsNullOrderByUploadedAtDesc(
            UUID caseId
    );

    List<CaseEvidence>
    findByCaseIdAndEvidenceTypeAndDeletedAtIsNullOrderByUploadedAtDesc(
            UUID caseId,
            String evidenceType
    );

    List<CaseEvidence> findByTransactionIdAndDeletedAtIsNullOrderByUploadedAtDesc(
            UUID transactionId
    );
}
