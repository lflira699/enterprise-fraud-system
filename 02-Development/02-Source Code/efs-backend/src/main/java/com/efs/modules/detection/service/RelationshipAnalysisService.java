package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.modules.detection.entity.RelationshipAnalysis;
import com.efs.modules.detection.mapper.RelationshipAnalysisMapper;
import com.efs.modules.detection.repository.RelationshipAnalysisRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RelationshipAnalysisService
        implements RelationshipAnalysisServiceInterface {

    private final RelationshipAnalysisRepository relationshipAnalysisRepository;
    private final RelationshipAnalysisMapper relationshipAnalysisMapper;

    public RelationshipAnalysisService(
            RelationshipAnalysisRepository relationshipAnalysisRepository,
            RelationshipAnalysisMapper relationshipAnalysisMapper) {

        this.relationshipAnalysisRepository =
                relationshipAnalysisRepository;
        this.relationshipAnalysisMapper =
                relationshipAnalysisMapper;
    }

    @Override
    @Transactional
    public RelationshipAnalysisResponse createRelationshipAnalysis(
            RelationshipAnalysisRequest request) {

        RelationshipAnalysis analysis =
                relationshipAnalysisMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        analysis.setAnalyzedAt(now);
        analysis.setCreatedAt(now);

        RelationshipAnalysis savedAnalysis =
                relationshipAnalysisRepository.save(analysis);

        return relationshipAnalysisMapper.toResponse(
                savedAnalysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipAnalysisResponse getRelationshipAnalysisById(
            UUID relationshipAnalysisId) {

        RelationshipAnalysis analysis =
                relationshipAnalysisRepository
                        .findByRelationshipAnalysisId(
                                relationshipAnalysisId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Relationship analysis not found: "
                                                + relationshipAnalysisId
                                )
                        );

        return relationshipAnalysisMapper.toResponse(
                analysis
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesByCustomer(UUID customerId) {

        return relationshipAnalysisRepository
                .findByCustomerIdOrderByAnalyzedAtDesc(customerId)
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesByTransaction(UUID transactionId) {

        return relationshipAnalysisRepository
                .findByTransactionIdOrderByAnalyzedAtDesc(
                        transactionId
                )
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesByCorrelation(UUID correlationId) {

        return relationshipAnalysisRepository
                .findByCorrelationIdOrderByAnalyzedAtDesc(
                        correlationId
                )
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesByType(String relationshipType) {

        return relationshipAnalysisRepository
                .findByRelationshipTypeOrderByAnalyzedAtDesc(
                        relationshipType
                )
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesBySource(String sourceEntityKey) {

        return relationshipAnalysisRepository
                .findBySourceEntityKeyOrderByAnalyzedAtDesc(
                        sourceEntityKey
                )
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesByTarget(String targetEntityKey) {

        return relationshipAnalysisRepository
                .findByTargetEntityKeyOrderByAnalyzedAtDesc(
                        targetEntityKey
                )
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipAnalysisResponse>
    getAnalysesByStatus(String analysisStatus) {

        return relationshipAnalysisRepository
                .findByAnalysisStatusOrderByAnalyzedAtDesc(
                        analysisStatus
                )
                .stream()
                .map(relationshipAnalysisMapper::toResponse)
                .toList();
    }
}