package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.BehavioralAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BehavioralAnalysisRepository
        extends JpaRepository<BehavioralAnalysis, UUID> {

    Optional<BehavioralAnalysis> findByBehavioralAnalysisId(
            UUID behavioralAnalysisId
    );

    List<BehavioralAnalysis>
    findByCustomerIdOrderByAnalyzedAtDesc(
            UUID customerId
    );

    List<BehavioralAnalysis>
    findByTransactionIdOrderByAnalyzedAtDesc(
            UUID transactionId
    );

    List<BehavioralAnalysis>
    findByCorrelationIdOrderByAnalyzedAtDesc(
            UUID correlationId
    );

    List<BehavioralAnalysis>
    findByAnalysisStatusOrderByAnalyzedAtDesc(
            String analysisStatus
    );
}