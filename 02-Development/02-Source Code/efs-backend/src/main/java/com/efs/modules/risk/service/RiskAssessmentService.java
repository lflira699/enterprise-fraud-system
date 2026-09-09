package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.entity.RiskAssessment;
import com.efs.modules.risk.mapper.RiskAssessmentMapper;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RiskAssessmentService
        implements RiskAssessmentServiceInterface {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    RiskAssessmentService.class
            );

    private static final String REVIEW_EVENT_TYPE =
            "RISK_SCORE_REVIEWED";

    private static final String REVIEW_ENTITY_TYPE =
            "RISK_ASSESSMENT";

    private static final String REVIEW_ACTION =
            "VIEW";

    private static final String REVIEW_SOURCE_COMPONENT =
            "RISK_ENGINE";

    private static final int MAX_PAGE_SIZE = 100;

    private static final String DEFAULT_RISK_SORT =
            "assessmentTimestamp";

    private static final String SORT_DIRECTION_ASC =
            "ASC";

    private static final String SORT_DIRECTION_DESC =
            "DESC";

    private static final String EVENT_TYPE =
            "RiskCalculated";

    private static final String EVENT_SCHEMA_VERSION =
            "1.0";

    private static final String EVENT_PRODUCER =
            "Risk Engine";

    private static final String AGGREGATE_TYPE =
            "RiskAssessment";

    private final RiskAssessmentRepository
            riskAssessmentRepository;

    private final RiskAssessmentMapper
            riskAssessmentMapper;

    private final TransactionRepository
            transactionRepository;

    private final RiskScoringModelResolver
            riskScoringModelResolver;

    private final RiskCalculator
            riskCalculator;

    private final DomainEventOutboxService
            domainEventOutboxService;

    private final AuditEventServiceInterface
            auditEventService;

    public RiskAssessmentService(
            RiskAssessmentRepository riskAssessmentRepository,
            RiskAssessmentMapper riskAssessmentMapper,
            TransactionRepository transactionRepository,
            RiskScoringModelResolver riskScoringModelResolver,
            RiskCalculator riskCalculator,
            DomainEventOutboxService domainEventOutboxService,
            AuditEventServiceInterface auditEventService) {

        this.riskAssessmentRepository =
                riskAssessmentRepository;

        this.riskAssessmentMapper =
                riskAssessmentMapper;

        this.transactionRepository =
                transactionRepository;

        this.riskScoringModelResolver =
                riskScoringModelResolver;

        this.riskCalculator =
                riskCalculator;

        this.domainEventOutboxService =
                domainEventOutboxService;

        this.auditEventService =
                auditEventService;
    }

    @Override
    @Transactional
    public RiskAssessmentResponse createRiskAssessment(
            RiskAssessmentRequest request) {

        long startedAtNanos =
                System.nanoTime();

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                request.getTransactionId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found: "
                                                + request.getTransactionId()
                                )
                        );

        if (transaction.getCorrelationId() == null) {
            throw new IllegalStateException(
                    "Transaction correlationId is required "
                            + "for RiskCalculated event"
            );
        }

        RiskScoringModel model =
                riskScoringModelResolver.resolve(
                        transaction.getOrganizationId(),
                        transaction.getTenantId()
                );

        Map<String, BigDecimal> factorScores =
                buildFactorScores(request);

        RiskCalculationResult calculation =
                riskCalculator.calculate(
                        model,
                        factorScores
                );

        RiskAssessment reusableAssessment =
                findReusableAssessment(
                        request,
                        calculation
                );

        if (reusableAssessment != null) {
            return riskAssessmentMapper.toResponse(
                    reusableAssessment
            );
        }

        RiskAssessment assessment =
                riskAssessmentMapper.toEntity(request);

        assessment.setOverallRiskScore(
                calculation.overallRiskScore()
        );

        assessment.setRiskLevel(
                calculation.riskLevel()
        );

        assessment.setModelName(
                calculation.modelName()
        );

        assessment.setModelVersion(
                calculation.modelVersion()
        );

        assessment.setProcessingTimeMs(
                TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime()
                                - startedAtNanos
                )
        );

        RiskAssessment savedAssessment =
                riskAssessmentRepository.save(
                        assessment
                );

        publishRiskCalculated(
                savedAssessment,
                transaction
        );

        return riskAssessmentMapper.toResponse(
                savedAssessment
        );
    }

    private Map<String, BigDecimal> buildFactorScores(
            RiskAssessmentRequest request) {

        Map<String, BigDecimal> factorScores =
                new HashMap<>();

        factorScores.put(
                "RULES",
                request.getRulesScore()
        );

        factorScores.put(
                "BEHAVIORAL",
                request.getBehavioralScore()
        );

        factorScores.put(
                "CUSTOMER",
                request.getCustomerScore()
        );

        factorScores.put(
                "GEOGRAPHIC",
                request.getGeographicScore()
        );

        factorScores.put(
                "DEVICE",
                request.getDeviceScore()
        );

        return factorScores;
    }

    private RiskAssessment findReusableAssessment(
            RiskAssessmentRequest request,
            RiskCalculationResult calculation) {

        return riskAssessmentRepository
                .findByTransactionIdAndAssessmentTypeOrderByAssessmentTimestampDesc(
                        request.getTransactionId(),
                        request.getAssessmentType()
                )
                .stream()
                .filter(
                        assessment ->
                                Objects.equals(
                                        assessment.getAssessmentStage(),
                                        request.getAssessmentStage()
                                )
                )
                .filter(
                        assessment ->
                                Objects.equals(
                                        assessment.getModelName(),
                                        calculation.modelName()
                                )
                )
                .filter(
                        assessment ->
                                Objects.equals(
                                        assessment.getModelVersion(),
                                        calculation.modelVersion()
                                )
                )
                .filter(
                        assessment ->
                                hasSameFactorContributions(
                                        assessment,
                                        calculation
                                                .factorContributions()
                                )
                )
                .findFirst()
                .orElse(null);
    }

    private boolean hasSameFactorContributions(
            RiskAssessment assessment,
            List<RiskCalculationResult.FactorContribution>
                    factorContributions) {

        for (
                RiskCalculationResult.FactorContribution contribution
                : factorContributions
        ) {

            BigDecimal persistedScore =
                    getPersistedFactorScore(
                            assessment,
                            contribution.factorCode()
                    );

            if (!sameScore(
                    persistedScore,
                    contribution.score()
            )) {
                return false;
            }
        }

        return true;
    }

    private BigDecimal getPersistedFactorScore(
            RiskAssessment assessment,
            String factorCode) {

        return switch (factorCode) {
            case "RULES" ->
                    assessment.getRulesScore();

            case "BEHAVIORAL" ->
                    assessment.getBehavioralScore();

            case "CUSTOMER" ->
                    assessment.getCustomerScore();

            case "GEOGRAPHIC" ->
                    assessment.getGeographicScore();

            case "DEVICE" ->
                    assessment.getDeviceScore();

            default ->
                    null;
        };
    }

    private boolean sameScore(
            BigDecimal first,
            BigDecimal second) {

        if (first == null || second == null) {
            return first == null
                    && second == null;
        }

        return first.compareTo(second) == 0;
    }

    private void publishRiskCalculated(
            RiskAssessment assessment,
            Transaction transaction) {

        DomainEventEnvelope envelope =
                new DomainEventEnvelope();

        envelope.setEventType(
                EVENT_TYPE
        );

        envelope.setSchemaVersion(
                EVENT_SCHEMA_VERSION
        );

        envelope.setOccurredAt(
                assessment.getAssessmentTimestamp()
        );

        envelope.setProducer(
                EVENT_PRODUCER
        );

        envelope.setCorrelationId(
                transaction.getCorrelationId()
        );

        envelope.setTenantId(
                transaction.getTenantId()
        );

        envelope.setPayload(
                Map.of(
                        "riskAssessmentId",
                        assessment.getRiskAssessmentId()
                                .toString()
                )
        );

        envelope.setMetadata(
                Map.of()
        );

        domainEventOutboxService.persist(
                AGGREGATE_TYPE,
                assessment.getRiskAssessmentId(),
                envelope
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

    @Override
    public RiskAssessmentResponse getRiskAssessmentById(
            UUID riskAssessmentId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                riskAssessmentId,
                "riskAssessmentId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "riskAssessmentId",
                riskAssessmentId.toString()
        );

        requireViewPermission(
                riskAssessmentId,
                securityContext,
                details
        );

        try {

            RiskAssessmentResponse response =
                    getRiskAssessmentById(
                            riskAssessmentId
                    );

            recordReviewAudit(
                    riskAssessmentId,
                    securityContext,
                    "SUCCESS",
                    null,
                    1,
                    details,
                    null
            );

            return response;
        }
        catch (ResourceNotFoundException exception) {

            recordReviewAudit(
                    riskAssessmentId,
                    securityContext,
                    "REJECTED",
                    "RISK_ASSESSMENT_NOT_FOUND",
                    null,
                    details,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-029 risk assessment retrieval failed for riskAssessmentId={} userId={}",
                    riskAssessmentId,
                    securityContext.getUserId(),
                    exception
            );

            recordReviewAudit(
                    riskAssessmentId,
                    securityContext,
                    "FAILURE",
                    "RISK_ASSESSMENT_RETRIEVAL_FAILED",
                    null,
                    details,
                    exception
            );

            throw exception;
        }
    }

    @Override
    public List<RiskAssessmentResponse>
    getAssessmentsByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                transactionId,
                "transactionId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "transactionId",
                transactionId.toString()
        );

        requireViewPermission(
                null,
                securityContext,
                details
        );

        try {

            List<RiskAssessmentResponse> assessments =
                    getAssessmentsByTransaction(
                            transactionId
                    );

            recordReviewAudit(
                    null,
                    securityContext,
                    "SUCCESS",
                    null,
                    assessments.size(),
                    details,
                    null
            );

            return assessments;
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-029 risk assessment retrieval failed for transactionId={} userId={}",
                    transactionId,
                    securityContext.getUserId(),
                    exception
            );

            recordReviewAudit(
                    null,
                    securityContext,
                    "FAILURE",
                    "RISK_ASSESSMENT_RETRIEVAL_FAILED",
                    null,
                    details,
                    exception
            );

            throw exception;
        }
    }

    @Override
    public RiskAssessmentResponse
    getLatestAssessmentByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                transactionId,
                "transactionId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "transactionId",
                transactionId.toString()
        );

        requireViewPermission(
                null,
                securityContext,
                details
        );

        try {

            RiskAssessmentResponse response =
                    getLatestAssessmentByTransaction(
                            transactionId
                    );

            details.put(
                    "riskAssessmentId",
                    response.getRiskAssessmentId()
                            .toString()
            );

            recordReviewAudit(
                    response.getRiskAssessmentId(),
                    securityContext,
                    "SUCCESS",
                    null,
                    1,
                    details,
                    null
            );

            return response;
        }
        catch (ResourceNotFoundException exception) {

            recordReviewAudit(
                    null,
                    securityContext,
                    "REJECTED",
                    "RISK_ASSESSMENT_NOT_FOUND",
                    null,
                    details,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-029 latest risk assessment retrieval failed for transactionId={} userId={}",
                    transactionId,
                    securityContext.getUserId(),
                    exception
            );

            recordReviewAudit(
                    null,
                    securityContext,
                    "FAILURE",
                    "RISK_ASSESSMENT_RETRIEVAL_FAILED",
                    null,
                    details,
                    exception
            );

            throw exception;
        }
    }

    @Override
    public List<RiskAssessmentResponse>
    getAssessmentsByTransactionAndType(
            UUID transactionId,
            String assessmentType,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                transactionId,
                "transactionId is required"
        );

        Objects.requireNonNull(
                assessmentType,
                "assessmentType is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "transactionId",
                transactionId.toString()
        );

        details.put(
                "assessmentType",
                assessmentType
        );

        requireViewPermission(
                null,
                securityContext,
                details
        );

        try {

            List<RiskAssessmentResponse> assessments =
                    getAssessmentsByTransactionAndType(
                            transactionId,
                            assessmentType
                    );

            recordReviewAudit(
                    null,
                    securityContext,
                    "SUCCESS",
                    null,
                    assessments.size(),
                    details,
                    null
            );

            return assessments;
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-029 risk assessment retrieval failed for transactionId={} assessmentType={} userId={}",
                    transactionId,
                    assessmentType,
                    securityContext.getUserId(),
                    exception
            );

            recordReviewAudit(
                    null,
                    securityContext,
                    "FAILURE",
                    "RISK_ASSESSMENT_RETRIEVAL_FAILED",
                    null,
                    details,
                    exception
            );

            throw exception;
        }
    }

    @Override
    public PageResponse<RiskAssessmentResponse>
    searchAssessments(
            String riskLevel,
            String assessmentResult,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        if (hasText(riskLevel)) {
            details.put(
                    "riskLevel",
                    riskLevel
            );
        }

        if (hasText(assessmentResult)) {
            details.put(
                    "assessmentResult",
                    assessmentResult
            );
        }

        details.put(
                "page",
                page
        );

        details.put(
                "size",
                size
        );

        details.put(
                "sort",
                sort
        );

        details.put(
                "direction",
                direction
        );

        requireViewPermission(
                null,
                securityContext,
                details
        );

        try {

            PageResponse<RiskAssessmentResponse> response =
                    searchAssessments(
                            riskLevel,
                            assessmentResult,
                            page,
                            size,
                            sort,
                            direction
                    );

            recordReviewAudit(
                    null,
                    securityContext,
                    "SUCCESS",
                    null,
                    response.getContent().size(),
                    details,
                    null
            );

            return response;
        }
        catch (RequestValidationException exception) {
            throw exception;
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-029 risk assessment search failed for userId={}",
                    securityContext.getUserId(),
                    exception
            );

            recordReviewAudit(
                    null,
                    securityContext,
                    "FAILURE",
                    "RISK_ASSESSMENT_RETRIEVAL_FAILED",
                    null,
                    details,
                    exception
            );

            throw exception;
        }
    }

    private void requireViewPermission(
            UUID entityId,
            SecurityContext securityContext,
            Map<String, Object> details) {

        if (!securityContext.hasPermission(
                RISK_ASSESSMENT_VIEW_PERMISSION
        )) {

            recordReviewAudit(
                    entityId,
                    securityContext,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null,
                    details,
                    null
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + RISK_ASSESSMENT_VIEW_PERMISSION
            );
        }
    }

    private void recordReviewAudit(
            UUID entityId,
            SecurityContext securityContext,
            String eventResult,
            String reason,
            Integer resultCount,
            Map<String, Object> criteria,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                REVIEW_EVENT_TYPE
        );

        request.setEntityType(
                REVIEW_ENTITY_TYPE
        );

        request.setEntityId(
                entityId
        );

        request.setAction(
                REVIEW_ACTION
        );

        request.setSourceComponent(
                REVIEW_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                RISK_ASSESSMENT_VIEW_PERMISSION
        );

        if (criteria != null) {
            details.putAll(
                    criteria
            );
        }

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (resultCount != null) {
            details.put(
                    "resultCount",
                    resultCount
            );
        }

        if (exception != null) {

            details.put(
                    "errorType",
                    exception.getClass()
                            .getSimpleName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage() == null
                            ? "Risk assessment retrieval failed"
                            : exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
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