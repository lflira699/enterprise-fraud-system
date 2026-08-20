package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.NetworkAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NetworkAnalysisRepository
        extends JpaRepository<NetworkAnalysis, UUID> {

    Optional<NetworkAnalysis> findByNetworkAnalysisId(
            UUID networkAnalysisId
    );

    List<NetworkAnalysis>
    findByCustomerIdOrderByAnalyzedAtDesc(
            UUID customerId
    );

    List<NetworkAnalysis>
    findByTransactionIdOrderByAnalyzedAtDesc(
            UUID transactionId
    );

    List<NetworkAnalysis>
    findByCorrelationIdOrderByAnalyzedAtDesc(
            UUID correlationId
    );

    List<NetworkAnalysis>
    findByNetworkTypeOrderByAnalyzedAtDesc(
            String networkType
    );

    List<NetworkAnalysis>
    findByAnalysisStatusOrderByAnalyzedAtDesc(
            String analysisStatus
    );

    List<NetworkAnalysis>
    findByNetworkKeyOrderByAnalyzedAtDesc(
            String networkKey
    );
}