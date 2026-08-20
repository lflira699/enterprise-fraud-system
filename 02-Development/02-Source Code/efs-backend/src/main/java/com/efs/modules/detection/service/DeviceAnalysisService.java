package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.modules.detection.entity.DeviceAnalysis;
import com.efs.modules.detection.mapper.DeviceAnalysisMapper;
import com.efs.modules.detection.repository.DeviceAnalysisRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceAnalysisService
        implements DeviceAnalysisServiceInterface {

    private final DeviceAnalysisRepository deviceAnalysisRepository;
    private final DeviceAnalysisMapper deviceAnalysisMapper;

    public DeviceAnalysisService(
            DeviceAnalysisRepository deviceAnalysisRepository,
            DeviceAnalysisMapper deviceAnalysisMapper) {

        this.deviceAnalysisRepository =
                deviceAnalysisRepository;
        this.deviceAnalysisMapper =
                deviceAnalysisMapper;
    }

    @Override
    @Transactional
    public DeviceAnalysisResponse createDeviceAnalysis(
            DeviceAnalysisRequest request) {

        DeviceAnalysis analysis =
                deviceAnalysisMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        analysis.setAnalyzedAt(now);
        analysis.setCreatedAt(now);

        DeviceAnalysis savedAnalysis =
                deviceAnalysisRepository.save(analysis);

        return deviceAnalysisMapper.toResponse(
                savedAnalysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceAnalysisResponse getDeviceAnalysisById(
            UUID deviceAnalysisId) {

        DeviceAnalysis analysis =
                deviceAnalysisRepository
                        .findByDeviceAnalysisId(deviceAnalysisId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Device analysis not found: "
                                                + deviceAnalysisId
                                )
                        );

        return deviceAnalysisMapper.toResponse(
                analysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByCustomer(UUID customerId) {

        return deviceAnalysisRepository
                .findByCustomerIdOrderByAnalyzedAtDesc(customerId)
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByTransaction(UUID transactionId) {

        return deviceAnalysisRepository
                .findByTransactionIdOrderByAnalyzedAtDesc(
                        transactionId
                )
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByCorrelation(UUID correlationId) {

        return deviceAnalysisRepository
                .findByCorrelationIdOrderByAnalyzedAtDesc(
                        correlationId
                )
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByDeviceId(String deviceId) {

        return deviceAnalysisRepository
                .findByDeviceIdOrderByAnalyzedAtDesc(deviceId)
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByFingerprint(String deviceFingerprint) {

        return deviceAnalysisRepository
                .findByDeviceFingerprintOrderByAnalyzedAtDesc(
                        deviceFingerprint
                )
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByIpAddress(String ipAddress) {

        return deviceAnalysisRepository
                .findByIpAddressOrderByAnalyzedAtDesc(ipAddress)
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceAnalysisResponse>
    getAnalysesByStatus(String analysisStatus) {

        return deviceAnalysisRepository
                .findByAnalysisStatusOrderByAnalyzedAtDesc(
                        analysisStatus
                )
                .stream()
                .map(deviceAnalysisMapper::toResponse)
                .toList();
    }
}