package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.BehavioralAnalysisRequest;
import com.efs.modules.detection.dto.BehavioralAnalysisResponse;
import com.efs.modules.detection.entity.BehavioralAnalysis;
import com.efs.modules.detection.mapper.BehavioralAnalysisMapper;
import com.efs.modules.detection.repository.BehavioralAnalysisRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BehavioralAnalysisService
        implements BehavioralAnalysisServiceInterface {

    private final BehavioralAnalysisRepository behavioralAnalysisRepository;
    private final BehavioralAnalysisMapper behavioralAnalysisMapper;

    public BehavioralAnalysisService(
            BehavioralAnalysisRepository behavioralAnalysisRepository,
            BehavioralAnalysisMapper behavioralAnalysisMapper) {

        this.behavioralAnalysisRepository =
                behavioralAnalysisRepository;
        this.behavioralAnalysisMapper =
                behavioralAnalysisMapper;
    }

    @Override
    @Transactional
    public BehavioralAnalysisResponse createBehavioralAnalysis(
            BehavioralAnalysisRequest request) {

        BehavioralAnalysis analysis =
                behavioralAnalysisMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        analysis.setAnalyzedAt(now);
        analysis.setCreatedAt(now);

        BehavioralAnalysis savedAnalysis =
                behavioralAnalysisRepository.save(analysis);

        return behavioralAnalysisMapper.toResponse(
                savedAnalysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BehavioralAnalysisResponse getBehavioralAnalysisById(
            UUID behavioralAnalysisId) {

        BehavioralAnalysis analysis =
                behavioralAnalysisRepository
                        .findByBehavioralAnalysisId(
                                behavioralAnalysisId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Behavioral analysis not found: "
                                                + behavioralAnalysisId
                                )
                        );

        return behavioralAnalysisMapper.toResponse(
                analysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BehavioralAnalysisResponse>
    getAnalysesByCustomer(UUID customerId) {

        return behavioralAnalysisRepository
                .findByCustomerIdOrderByAnalyzedAtDesc(customerId)
                .stream()
                .map(behavioralAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BehavioralAnalysisResponse>
    getAnalysesByTransaction(UUID transactionId) {

        return behavioralAnalysisRepository
                .findByTransactionIdOrderByAnalyzedAtDesc(
                        transactionId
                )
                .stream()
                .map(behavioralAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BehavioralAnalysisResponse>
    getAnalysesByCorrelation(UUID correlationId) {

        return behavioralAnalysisRepository
                .findByCorrelationIdOrderByAnalyzedAtDesc(
                        correlationId
                )
                .stream()
                .map(behavioralAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BehavioralAnalysisResponse>
    getAnalysesByStatus(String analysisStatus) {

        return behavioralAnalysisRepository
                .findByAnalysisStatusOrderByAnalyzedAtDesc(
                        analysisStatus
                )
                .stream()
                .map(behavioralAnalysisMapper::toResponse)
                .toList();
    }
}