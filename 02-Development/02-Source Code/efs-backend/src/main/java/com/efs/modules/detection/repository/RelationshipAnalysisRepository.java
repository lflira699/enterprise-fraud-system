package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.RelationshipAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RelationshipAnalysisRepository
        extends JpaRepository<RelationshipAnalysis, UUID> {

    Optional<RelationshipAnalysis> findByRelationshipAnalysisId(
            UUID relationshipAnalysisId
    );

    List<RelationshipAnalysis>
    findByCustomerIdOrderByAnalyzedAtDesc(
            UUID customerId
    );

    List<RelationshipAnalysis>
    findByTransactionIdOrderByAnalyzedAtDesc(
            UUID transactionId
    );

    List<RelationshipAnalysis>
    findByCorrelationIdOrderByAnalyzedAtDesc(
            UUID correlationId
    );

    List<RelationshipAnalysis>
    findByRelationshipTypeOrderByAnalyzedAtDesc(
            String relationshipType
    );

    List<RelationshipAnalysis>
    findBySourceEntityKeyOrderByAnalyzedAtDesc(
            String sourceEntityKey
    );

    List<RelationshipAnalysis>
    findByTargetEntityKeyOrderByAnalyzedAtDesc(
            String targetEntityKey
    );

    List<RelationshipAnalysis>
    findByAnalysisStatusOrderByAnalyzedAtDesc(
            String analysisStatus
    );
}