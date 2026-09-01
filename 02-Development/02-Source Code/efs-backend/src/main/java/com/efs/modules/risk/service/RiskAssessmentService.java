package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.entity.RiskAssessment;
import com.efs.modules.risk.mapper.RiskAssessmentMapper;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.pagination.PageResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RiskAssessmentService
        implements RiskAssessmentServiceInterface {

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_RISK_SORT =
            "assessmentTimestamp";
    private static final String SORT_DIRECTION_ASC =
            "ASC";
    private static final String SORT_DIRECTION_DESC =
            "DESC";

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final RiskAssessmentMapper riskAssessmentMapper;

    public RiskAssessmentService(
            RiskAssessmentRepository riskAssessmentRepository,
            RiskAssessmentMapper riskAssessmentMapper) {

        this.riskAssessmentRepository =
                riskAssessmentRepository;

        this.riskAssessmentMapper =
                riskAssessmentMapper;
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
    getAssessmentsByTransaction(
            UUID transactionId) {

        return riskAssessmentRepository
                .findByTransactionIdOrderByAssessmentTimestampDesc(
                        transactionId
                )
                .stream()
                .map(
                        riskAssessmentMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessmentResponse
    getLatestAssessmentByTransaction(
            UUID transactionId) {

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
                .map(
                        riskAssessmentMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse>
    getAssessmentsByRiskLevel(
            String riskLevel) {

        return riskAssessmentRepository
                .findByRiskLevelOrderByAssessmentTimestampDesc(
                        riskLevel
                )
                .stream()
                .map(
                        riskAssessmentMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse>
    getAssessmentsByResult(
            String assessmentResult) {

        return riskAssessmentRepository
                .findByAssessmentResultOrderByAssessmentTimestampDesc(
                        assessmentResult
                )
                .stream()
                .map(
                        riskAssessmentMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RiskAssessmentResponse>
    searchAssessments(
            String riskLevel,
            String assessmentResult,
            int page,
            int size,
            String sort,
            String direction) {

        validateRiskAssessmentSearchRequest(
                page,
                size,
                sort,
                direction
        );

        Sort.Direction sortDirection =
                SORT_DIRECTION_ASC.equals(
                        direction
                )
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sort
                        )
                );

        Specification<RiskAssessment> specification =
                (
                        root,
                        query,
                        criteriaBuilder
                ) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();

                    if (hasText(riskLevel)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "riskLevel"
                                        ),
                                        riskLevel
                                )
                        );
                    }

                    if (hasText(assessmentResult)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "assessmentResult"
                                        ),
                                        assessmentResult
                                )
                        );
                    }

                    return criteriaBuilder.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
                };

        Page<RiskAssessment> assessmentPage =
                riskAssessmentRepository.findAll(
                        specification,
                        pageRequest
                );

        List<RiskAssessmentResponse> content =
                assessmentPage
                        .getContent()
                        .stream()
                        .map(
                                riskAssessmentMapper::toResponse
                        )
                        .toList();

        return new PageResponse<>(
                content,
                assessmentPage.getNumber(),
                assessmentPage.getSize(),
                assessmentPage.getTotalElements(),
                assessmentPage.getTotalPages(),
                assessmentPage.hasNext(),
                assessmentPage.hasPrevious()
        );
    }

    private void validateRiskAssessmentSearchRequest(
            int page,
            int size,
            String sort,
            String direction) {

        if (page < 0) {
            throw new RequestValidationException(
                    "page must be greater than or equal to 0"
            );
        }

        if (
                size < 1
                        || size > MAX_PAGE_SIZE
        ) {
            throw new RequestValidationException(
                    "size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }

        if (
                !DEFAULT_RISK_SORT.equals(
                        sort
                )
        ) {
            throw new RequestValidationException(
                    "Unsupported sort field: "
                            + sort
            );
        }

        if (
                !SORT_DIRECTION_ASC.equals(
                        direction
                )
                        && !SORT_DIRECTION_DESC.equals(
                                direction
                        )
        ) {
            throw new RequestValidationException(
                    "Unsupported sort direction: "
                            + direction
            );
        }
    }

    private boolean hasText(
            String value) {

        return value != null
                && !value.isBlank();
    }
}