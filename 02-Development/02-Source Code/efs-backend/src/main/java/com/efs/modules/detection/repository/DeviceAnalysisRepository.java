package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DeviceAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceAnalysisRepository
        extends JpaRepository<DeviceAnalysis, UUID> {

    Optional<DeviceAnalysis> findByDeviceAnalysisId(
            UUID deviceAnalysisId
    );

    List<DeviceAnalysis>
    findByCustomerIdOrderByAnalyzedAtDesc(
            UUID customerId
    );

    List<DeviceAnalysis>
    findByTransactionIdOrderByAnalyzedAtDesc(
            UUID transactionId
    );

    List<DeviceAnalysis>
    findByCorrelationIdOrderByAnalyzedAtDesc(
            UUID correlationId
    );

    List<DeviceAnalysis>
    findByDeviceIdOrderByAnalyzedAtDesc(
            String deviceId
    );

    List<DeviceAnalysis>
    findByDeviceFingerprintOrderByAnalyzedAtDesc(
            String deviceFingerprint
    );

    List<DeviceAnalysis>
    findByIpAddressOrderByAnalyzedAtDesc(
            String ipAddress
    );

    List<DeviceAnalysis>
    findByAnalysisStatusOrderByAnalyzedAtDesc(
            String analysisStatus
    );
}