package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.modules.detection.entity.NetworkAnalysis;
import com.efs.modules.detection.mapper.NetworkAnalysisMapper;
import com.efs.modules.detection.repository.NetworkAnalysisRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NetworkAnalysisService
        implements NetworkAnalysisServiceInterface {

    private final NetworkAnalysisRepository networkAnalysisRepository;
    private final NetworkAnalysisMapper networkAnalysisMapper;

    public NetworkAnalysisService(
            NetworkAnalysisRepository networkAnalysisRepository,
            NetworkAnalysisMapper networkAnalysisMapper) {

        this.networkAnalysisRepository =
                networkAnalysisRepository;
        this.networkAnalysisMapper =
                networkAnalysisMapper;
    }

    @Override
    @Transactional
    public NetworkAnalysisResponse createNetworkAnalysis(
            NetworkAnalysisRequest request) {

        NetworkAnalysis analysis =
                networkAnalysisMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        analysis.setAnalyzedAt(now);
        analysis.setCreatedAt(now);

        NetworkAnalysis savedAnalysis =
                networkAnalysisRepository.save(analysis);

        return networkAnalysisMapper.toResponse(
                savedAnalysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NetworkAnalysisResponse getNetworkAnalysisById(
            UUID networkAnalysisId) {

        NetworkAnalysis analysis =
                networkAnalysisRepository
                        .findByNetworkAnalysisId(networkAnalysisId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Network analysis not found: "
                                                + networkAnalysisId
                                )
                        );

        return networkAnalysisMapper.toResponse(
                analysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NetworkAnalysisResponse>
    getAnalysesByCustomer(UUID customerId) {

        return networkAnalysisRepository
                .findByCustomerIdOrderByAnalyzedAtDesc(customerId)
                .stream()
                .map(networkAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NetworkAnalysisResponse>
    getAnalysesByTransaction(UUID transactionId) {

        return networkAnalysisRepository
                .findByTransactionIdOrderByAnalyzedAtDesc(
                        transactionId
                )
                .stream()
                .map(networkAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NetworkAnalysisResponse>
    getAnalysesByCorrelation(UUID correlationId) {

        return networkAnalysisRepository
                .findByCorrelationIdOrderByAnalyzedAtDesc(
                        correlationId
                )
                .stream()
                .map(networkAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NetworkAnalysisResponse>
    getAnalysesByType(String networkType) {

        return networkAnalysisRepository
                .findByNetworkTypeOrderByAnalyzedAtDesc(
                        networkType
                )
                .stream()
                .map(networkAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NetworkAnalysisResponse>
    getAnalysesByStatus(String analysisStatus) {

        return networkAnalysisRepository
                .findByAnalysisStatusOrderByAnalyzedAtDesc(
                        analysisStatus
                )
                .stream()
                .map(networkAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NetworkAnalysisResponse>
    getAnalysesByKey(String networkKey) {

        return networkAnalysisRepository
                .findByNetworkKeyOrderByAnalyzedAtDesc(
                        networkKey
                )
                .stream()
                .map(networkAnalysisMapper::toResponse)
                .toList();
    }
}