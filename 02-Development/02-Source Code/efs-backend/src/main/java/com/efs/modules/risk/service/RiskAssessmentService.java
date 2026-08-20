package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.entity.RiskAssessment;
import com.efs.modules.risk.mapper.RiskAssessmentMapper;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RiskAssessmentService
        implements RiskAssessmentServiceInterface {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final RiskAssessmentMapper riskAssessmentMapper;

    public RiskAssessmentService(
            RiskAssessmentRepository riskAssessmentRepository,
            RiskAssessmentMapper riskAssessmentMapper) {

        this.riskAssessmentRepository = riskAssessmentRepository;
        this.riskAssessmentMapper = riskAssessmentMapper;
    }

    @Override
    @Transactional
    public RiskAssessmentResponse createRiskAssessment(
            RiskAssessmentRequest request) {

        RiskAssessment assessment =
                riskAssessmentMapper.toEntity(request);

        RiskAssessment savedAssessment =
                riskAssessmentRepository.save(assessment);

        return riskAssessmentMapper.toResponse(
                savedAssessment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessmentResponse getRiskAssessmentById(
            UUID riskAssessmentId) {

        RiskAssessment assessment =
                riskAssessmentRepository
                        .findById(riskAssessmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Risk assessment not found: "
                                                + riskAssessmentId
                                )
                        );

        return riskAssessmentMapper.toResponse(
                assessment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse>
    getAssessmentsByTransaction(UUID transactionId) {

        return riskAssessmentRepository
                .findByTransactionIdOrderByAssessmentTimestampDesc(
                        transactionId
                )
                .stream()
                .map(riskAssessmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessmentResponse
    getLatestAssessmentByTransaction(UUID transactionId) {

        RiskAssessment assessment =
                riskAssessmentRepository
                        .findFirstByTransactionIdOrderByAssessmentTimestampDesc(
                                transactionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Risk assessment not found for transaction: "
                                                + transactionId
                                )
                        );

        return riskAssessmentMapper.toResponse(
                assessment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse>
    getAssessmentsByTransactionAndType(
            UUID transactionId,
            String assessmentType) {

        return riskAssessmentRepository
                .findByTransactionIdAndAssessmentTypeOrderByAssessmentTimestampDesc(
                        transactionId,
                        assessmentType
                )
                .stream()
                .map(riskAssessmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse>
    getAssessmentsByRiskLevel(String riskLevel) {

        return riskAssessmentRepository
                .findByRiskLevelOrderByAssessmentTimestampDesc(
                        riskLevel
                )
                .stream()
                .map(riskAssessmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse>
    getAssessmentsByResult(String assessmentResult) {

        return riskAssessmentRepository
                .findByAssessmentResultOrderByAssessmentTimestampDesc(
                        assessmentResult
                )
                .stream()
                .map(riskAssessmentMapper::toResponse)
                .toList();
    }
}