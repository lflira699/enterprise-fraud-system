package com.efs.modules.risk.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.event.entity.FraudEvent;
import com.efs.modules.event.repository.FraudEventRepository;
import com.efs.modules.risk.dto.EventRiskAssessmentRequest;
import com.efs.modules.risk.dto.EventRiskAssessmentResponse;
import com.efs.modules.risk.entity.EventRiskAssessment;
import com.efs.modules.risk.mapper.EventRiskAssessmentMapper;
import com.efs.modules.risk.repository.EventRiskAssessmentRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EventRiskAssessmentService
        implements EventRiskAssessmentServiceInterface {

    private static final String FRAUD_EVENT_NOT_FOUND =
            "FRAUD_EVENT_NOT_FOUND";

    private static final String EVENT_RISK_NOT_ELIGIBLE =
            "EVENT_RISK_NOT_ELIGIBLE";

    private static final String EVENT_RISK_MODEL_UNAVAILABLE =
            "EVENT_RISK_MODEL_UNAVAILABLE";

    private static final String EVENT_RISK_FACTOR_UNRESOLVED =
            "EVENT_RISK_FACTOR_UNRESOLVED";

    private static final String EVENT_RISK_ASSESSMENT_FAILED =
            "EVENT_RISK_ASSESSMENT_FAILED";

    private final EventRiskAssessmentRepository
            eventRiskAssessmentRepository;

    private final EventRiskAssessmentMapper
            eventRiskAssessmentMapper;

    private final FraudEventRepository
            fraudEventRepository;

    private final RiskScoringModelResolver
            riskScoringModelResolver;

    private final RiskCalculator
            riskCalculator;

    private final SecurityContextProvider
            securityContextProvider;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final EventRiskAssessmentAuditService
            eventRiskAssessmentAuditService;

    public EventRiskAssessmentService(
            EventRiskAssessmentRepository eventRiskAssessmentRepository,
            EventRiskAssessmentMapper eventRiskAssessmentMapper,
            FraudEventRepository fraudEventRepository,
            RiskScoringModelResolver riskScoringModelResolver,
            RiskCalculator riskCalculator,
            SecurityContextProvider securityContextProvider,
            UserAccountLookupServiceInterface userAccountLookupService,
            EventRiskAssessmentAuditService eventRiskAssessmentAuditService) {

        this.eventRiskAssessmentRepository =
                eventRiskAssessmentRepository;

        this.eventRiskAssessmentMapper =
                eventRiskAssessmentMapper;

        this.fraudEventRepository =
                fraudEventRepository;

        this.riskScoringModelResolver =
                riskScoringModelResolver;

        this.riskCalculator =
                riskCalculator;

        this.securityContextProvider =
                securityContextProvider;

        this.userAccountLookupService =
                userAccountLookupService;

        this.eventRiskAssessmentAuditService =
                eventRiskAssessmentAuditService;
    }

    @Override
    @Transactional
    public EventRiskAssessmentResponse assess(
            EventRiskAssessmentRequest request) {

        long startedAt =
                System.nanoTime();

        UUID fraudEventId =
                request == null
                        ? null
                        : request.getFraudEventId();

        UUID correlationId =
                null;

        SecurityContext securityContext =
                null;

        UUID organizationId =
                null;

        String modelId =
                null;

        String modelVersion =
                null;

        boolean modelResolutionStarted =
                false;

        boolean modelResolutionCompleted =
                false;

        boolean factorResolutionStarted =
                false;

        boolean factorResolutionCompleted =
                false;

        try {

            validateRequest(
                    request
            );

            securityContext =
                    securityContextProvider
                            .getCurrentContext();

            UserAccountReference actor =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            validateAuthorizedScope(
                    securityContext,
                    actor
            );

            organizationId =
                    actor.organizationId();

            UUID tenantId =
                    actor.tenantId();

            if (
                    organizationId == null ||
                    tenantId == null
            ) {

                throw new RequestValidationException(
                        "Organization and tenant context are required."
                );
            }

            FraudEvent fraudEvent =
                    fraudEventRepository
                            .findByFraudEventIdAndOrganizationIdAndTenantId(
                                    request.getFraudEventId(),
                                    organizationId,
                                    tenantId
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Fraud event not found."
                                            )
                            );

            correlationId =
                    fraudEvent.getCorrelationId();

            if (correlationId == null) {

                throw new RequestValidationException(
                        "Fraud event correlationId is required."
                );
            }

            modelResolutionStarted =
                    true;

            RiskScoringModel model =
                    riskScoringModelResolver
                            .resolveEvent(
                                    organizationId,
                                    tenantId
                            );

            modelId =
                    model.modelName();

            modelVersion =
                    model.modelVersion();

            modelResolutionCompleted =
                    true;

            factorResolutionStarted =
                    true;

            Map<String, BigDecimal> factorScores =
                    resolveFactorScores(
                            model,
                            request.getFactorScores()
                    );

            factorResolutionCompleted =
                    true;

            RiskCalculationResult calculation =
                    riskCalculator.calculate(
                            model,
                            factorScores
                    );

            EventRiskAssessment reusableAssessment =
                    findReusableAssessment(
                            fraudEvent,
                            calculation,
                            factorScores
                    );

            if (reusableAssessment != null) {

                eventRiskAssessmentAuditService
                        .recordSuccess(
                                reusableAssessment,
                                securityContext,
                                true
                        );

                return eventRiskAssessmentMapper
                        .toResponse(
                                reusableAssessment
                        );
            }

            Map<String, Object> assessmentDetails =
                    buildAssessmentDetails(
                            model,
                            factorScores
                    );

            long processingTimeMs =
                    TimeUnit.NANOSECONDS.toMillis(
                            System.nanoTime()
                                    - startedAt
                    );

            EventRiskAssessment assessment =
                    new EventRiskAssessment(
                            fraudEvent.getFraudEventId(),
                            fraudEvent.getOrganizationId(),
                            fraudEvent.getTenantId(),
                            fraudEvent.getCorrelationId(),
                            calculation.modelName(),
                            calculation.modelVersion(),
                            calculation.overallRiskScore(),
                            calculation.riskLevel(),
                            null,
                            null,
                            assessmentDetails,
                            processingTimeMs
                    );

            EventRiskAssessment savedAssessment =
                    eventRiskAssessmentRepository
                            .saveAndFlush(
                                    assessment
                            );

            eventRiskAssessmentAuditService
                    .recordSuccess(
                            savedAssessment,
                            securityContext,
                            false
                    );

            return eventRiskAssessmentMapper
                    .toResponse(
                            savedAssessment
                    );
        }
        catch (ResourceNotFoundException exception) {

            eventRiskAssessmentAuditService
                    .recordRejected(
                            organizationId,
                            securityContext,
                            fraudEventId,
                            correlationId,
                            modelId,
                            modelVersion,
                            FRAUD_EVENT_NOT_FOUND,
                            exception
                    );

            throw exception;
        }
        catch (AccessDeniedException |
               RequestValidationException exception) {

            eventRiskAssessmentAuditService
                    .recordRejected(
                            organizationId,
                            securityContext,
                            fraudEventId,
                            correlationId,
                            modelId,
                            modelVersion,
                            EVENT_RISK_NOT_ELIGIBLE,
                            exception
                    );

            throw exception;
        }
        catch (IllegalArgumentException exception) {

            if (
                    factorResolutionStarted &&
                    !factorResolutionCompleted
            ) {

                eventRiskAssessmentAuditService
                        .recordRejected(
                            organizationId,
                            securityContext,
                                fraudEventId,
                                correlationId,
                            modelId,
                            modelVersion,
                                EVENT_RISK_FACTOR_UNRESOLVED,
                                exception
                        );

                throw new RequestValidationException(
                        exception.getMessage(),
                        exception
                );
            }

            if (factorResolutionCompleted) {

                eventRiskAssessmentAuditService
                        .recordRejected(
                            organizationId,
                            securityContext,
                                fraudEventId,
                                correlationId,
                            modelId,
                            modelVersion,
                                EVENT_RISK_MODEL_UNAVAILABLE,
                                exception
                        );

                throw new IllegalStateException(
                        exception.getMessage(),
                        exception
                );
            }

            eventRiskAssessmentAuditService
                    .recordFailure(
                            organizationId,
                            securityContext,
                            fraudEventId,
                            correlationId,
                            modelId,
                            modelVersion,
                            EVENT_RISK_ASSESSMENT_FAILED,
                            exception
                    );

            throw exception;
        }
        catch (IllegalStateException exception) {

            if (
                    modelResolutionStarted &&
                    !modelResolutionCompleted
            ) {

                eventRiskAssessmentAuditService
                        .recordRejected(
                            organizationId,
                            securityContext,
                                fraudEventId,
                                correlationId,
                            modelId,
                            modelVersion,
                                EVENT_RISK_MODEL_UNAVAILABLE,
                                exception
                        );

                throw exception;
            }

            if (!modelResolutionStarted) {

                eventRiskAssessmentAuditService
                        .recordRejected(
                            organizationId,
                            securityContext,
                                fraudEventId,
                                correlationId,
                            modelId,
                            modelVersion,
                                EVENT_RISK_NOT_ELIGIBLE,
                                exception
                        );

                throw exception;
            }

            eventRiskAssessmentAuditService
                    .recordFailure(
                            organizationId,
                            securityContext,
                            fraudEventId,
                            correlationId,
                            modelId,
                            modelVersion,
                            EVENT_RISK_ASSESSMENT_FAILED,
                            exception
                    );

            throw exception;
        }
        catch (RuntimeException exception) {

            eventRiskAssessmentAuditService
                    .recordFailure(
                            organizationId,
                            securityContext,
                            fraudEventId,
                            correlationId,
                            modelId,
                            modelVersion,
                            EVENT_RISK_ASSESSMENT_FAILED,
                            exception
                    );

            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventRiskAssessmentResponse
    getEventRiskAssessmentById(
            UUID eventRiskAssessmentId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                eventRiskAssessmentId,
                "eventRiskAssessmentId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "eventRiskAssessmentId",
                eventRiskAssessmentId.toString()
        );

        requireViewPermission(
                eventRiskAssessmentId,
                securityContext,
                details
        );

        UUID organizationId =
                null;

        try {

            UserAccountReference actor =
                    resolveAuthorizedReviewActor(
                            securityContext
                    );

            organizationId =
                    actor.organizationId();

            EventRiskAssessment assessment =
                    eventRiskAssessmentRepository
                            .findByEventRiskAssessmentIdAndOrganizationIdAndTenantId(
                                    eventRiskAssessmentId,
                                    actor.organizationId(),
                                    actor.tenantId()
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Event risk assessment not found."
                                            )
                            );

            EventRiskAssessmentResponse response =
                    eventRiskAssessmentMapper
                            .toResponse(
                                    assessment
                            );

            eventRiskAssessmentAuditService
                    .recordReview(
                            actor.organizationId(),
                            securityContext,
                            eventRiskAssessmentId,
                            "SUCCESS",
                            null,
                            1,
                            details,
                            null
                    );

            return response;
        }
        catch (ResourceNotFoundException exception) {

            eventRiskAssessmentAuditService
                    .recordReview(
                            organizationId,
                            securityContext,
                            eventRiskAssessmentId,
                            "REJECTED",
                            "RISK_ASSESSMENT_NOT_FOUND",
                            null,
                            details,
                            null
                    );

            throw exception;
        }
        catch (AccessDeniedException exception) {

            eventRiskAssessmentAuditService
                    .recordReview(
                            organizationId,
                            securityContext,
                            eventRiskAssessmentId,
                            "REJECTED",
                            null,
                            null,
                            details,
                            null
                    );

            throw exception;
        }
        catch (RuntimeException exception) {

            eventRiskAssessmentAuditService
                    .recordReview(
                            organizationId,
                            securityContext,
                            eventRiskAssessmentId,
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
    @Transactional(readOnly = true)
    public List<EventRiskAssessmentResponse>
    getEventRiskAssessmentsByFraudEventId(
            UUID fraudEventId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                fraudEventId,
                "fraudEventId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "fraudEventId",
                fraudEventId.toString()
        );

        requireViewPermission(
                null,
                securityContext,
                details
        );

        UUID organizationId =
                null;

        try {

            UserAccountReference actor =
                    resolveAuthorizedReviewActor(
                            securityContext
                    );

            organizationId =
                    actor.organizationId();

            List<EventRiskAssessmentResponse> assessments =
                    eventRiskAssessmentRepository
                            .findByFraudEventIdAndOrganizationIdAndTenantIdOrderByAssessmentTimestampDesc(
                                    fraudEventId,
                                    actor.organizationId(),
                                    actor.tenantId()
                            )
                            .stream()
                            .map(
                                    eventRiskAssessmentMapper::toResponse
                            )
                            .toList();

            eventRiskAssessmentAuditService
                    .recordReview(
                            actor.organizationId(),
                            securityContext,
                            null,
                            "SUCCESS",
                            null,
                            assessments.size(),
                            details,
                            null
                    );

            return assessments;
        }
        catch (AccessDeniedException exception) {

            eventRiskAssessmentAuditService
                    .recordReview(
                            organizationId,
                            securityContext,
                            null,
                            "REJECTED",
                            null,
                            null,
                            details,
                            null
                    );

            throw exception;
        }
        catch (RuntimeException exception) {

            eventRiskAssessmentAuditService
                    .recordReview(
                            organizationId,
                            securityContext,
                            null,
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

        if (
                !securityContext.hasPermission(
                        RiskAssessmentServiceInterface
                                .RISK_ASSESSMENT_VIEW_PERMISSION
                )
        ) {

            eventRiskAssessmentAuditService
                    .recordReview(
                            null,
                            securityContext,
                            entityId,
                            "REJECTED",
                            "MISSING_PERMISSION",
                            null,
                            details,
                            null
                    );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + RiskAssessmentServiceInterface
                                    .RISK_ASSESSMENT_VIEW_PERMISSION
            );
        }
    }

    private UserAccountReference resolveAuthorizedReviewActor(
            SecurityContext securityContext) {

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(
                                securityContext.getUserId()
                        );

        validateAuthorizedScope(
                securityContext,
                actor
        );

        if (
                actor.organizationId() == null ||
                actor.tenantId() == null
        ) {

            throw new AccessDeniedException(
                    "Authorized organization and tenant context are required."
            );
        }

        return actor;
    }
    private void validateRequest(
            EventRiskAssessmentRequest request) {

        if (request == null) {

            throw new RequestValidationException(
                    "Event risk assessment request is required."
            );
        }

        if (request.getFraudEventId() == null) {

            throw new RequestValidationException(
                    "fraudEventId is required."
            );
        }

        if (request.getFactorScores() == null) {

            throw new RequestValidationException(
                    "factorScores are required."
            );
        }
    }

    private void validateAuthorizedScope(
            SecurityContext securityContext,
            UserAccountReference actor) {

        if (!Objects.equals(
                securityContext.getTenantId(),
                actor.tenantId()
        )) {

            throw new AccessDeniedException(
                    "Security context tenant is outside the authorized scope."
            );
        }
    }

    private Map<String, BigDecimal> resolveFactorScores(
            RiskScoringModel model,
            Map<String, BigDecimal> requestedScores) {

        Set<String> configuredFactorCodes =
                new LinkedHashSet<>();

        Set<String> enabledFactorCodes =
                new LinkedHashSet<>();

        for (
                RiskScoringModel.Factor factor :
                model.factors()
        ) {

            configuredFactorCodes.add(
                    factor.factorCode()
            );

            if (factor.enabled()) {

                enabledFactorCodes.add(
                        factor.factorCode()
                );
            }
        }

        for (
                Map.Entry<String, BigDecimal> entry :
                requestedScores.entrySet()
        ) {

            String factorCode =
                    entry.getKey();

            if (
                    factorCode == null ||
                    factorCode.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "Event risk factor code is required."
                );
            }

            if (
                    !configuredFactorCodes.contains(
                            factorCode
                    )
            ) {

                throw new IllegalArgumentException(
                        "Unknown event risk factor: "
                                + factorCode
                );
            }

            if (
                    !enabledFactorCodes.contains(
                            factorCode
                    )
            ) {

                throw new IllegalArgumentException(
                        "Disabled event risk factor cannot be supplied: "
                                + factorCode
                );
            }

            if (entry.getValue() == null) {

                throw new IllegalArgumentException(
                        "Event risk factor score is required: "
                                + factorCode
                );
            }
        }

        Map<String, BigDecimal> resolvedScores =
                new LinkedHashMap<>();

        for (String factorCode : enabledFactorCodes) {

            BigDecimal score =
                    requestedScores.get(
                            factorCode
                    );

            if (score == null) {

                throw new IllegalArgumentException(
                        "Risk score is required for enabled factor: "
                                + factorCode
                );
            }

            if (
                    model.scoreMinimum() != null &&
                    model.scoreMaximum() != null &&
                    model.scoreMinimum().compareTo(
                            model.scoreMaximum()
                    ) < 0 &&
                    (
                            score.compareTo(
                                    model.scoreMinimum()
                            ) < 0 ||
                            score.compareTo(
                                    model.scoreMaximum()
                            ) > 0
                    )
            ) {

                throw new IllegalArgumentException(
                        "Event risk factor score is outside the configured model range: "
                                + factorCode
                );
            }

            resolvedScores.put(
                    factorCode,
                    score
            );
        }

        if (
                requestedScores.size() !=
                resolvedScores.size()
        ) {

            throw new IllegalArgumentException(
                    "Event risk factor input does not exactly match the enabled model factors."
            );
        }

        return Map.copyOf(
                resolvedScores
        );
    }

    private Map<String, Object> buildAssessmentDetails(
            RiskScoringModel model,
            Map<String, BigDecimal> factorScores) {

        Map<String, BigDecimal> factorWeights =
                new LinkedHashMap<>();

        for (
                RiskScoringModel.Factor factor :
                model.factors()
        ) {

            if (factor.enabled()) {

                factorWeights.put(
                        factor.factorCode(),
                        factor.weight()
                );
            }
        }

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "factorScores",
                Map.copyOf(
                        factorScores
                )
        );

        details.put(
                "factorWeights",
                Map.copyOf(
                        factorWeights
                )
        );

        return Map.copyOf(
                details
        );
    }

    private EventRiskAssessment findReusableAssessment(
            FraudEvent fraudEvent,
            RiskCalculationResult calculation,
            Map<String, BigDecimal> factorScores) {

        List<EventRiskAssessment> candidates =
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                                fraudEvent.getFraudEventId(),
                                fraudEvent.getOrganizationId(),
                                fraudEvent.getTenantId(),
                                fraudEvent.getCorrelationId(),
                                calculation.modelName(),
                                calculation.modelVersion()
                        );

        EventRiskAssessment exactMatch =
                null;

        int exactMatchCount =
                0;

        for (
                EventRiskAssessment candidate :
                candidates
        ) {

            if (
                    hasEquivalentFactorScores(
                            candidate,
                            factorScores
                    )
            ) {

                exactMatch =
                        candidate;

                exactMatchCount++;
            }
        }

        if (exactMatchCount == 1) {
            return exactMatch;
        }

        return null;
    }

    private boolean hasEquivalentFactorScores(
            EventRiskAssessment assessment,
            Map<String, BigDecimal> expectedScores) {

        Map<String, Object> details =
                assessment.getAssessmentDetails();

        if (details == null) {
            return false;
        }

        Object persistedScoresObject =
                details.get(
                        "factorScores"
                );

        if (
                !(persistedScoresObject
                        instanceof Map<?, ?> persistedScores)
        ) {
            return false;
        }

        if (
                persistedScores.size() !=
                expectedScores.size()
        ) {
            return false;
        }

        for (
                Map.Entry<String, BigDecimal> entry :
                expectedScores.entrySet()
        ) {

            if (
                    !persistedScores.containsKey(
                            entry.getKey()
                    )
            ) {
                return false;
            }

            BigDecimal persistedValue =
                    toBigDecimal(
                            persistedScores.get(
                                    entry.getKey()
                            )
                    );

            if (
                    persistedValue == null ||
                    persistedValue.compareTo(
                            entry.getValue()
                    ) != 0
            ) {
                return false;
            }
        }

        return true;
    }

    private BigDecimal toBigDecimal(
            Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        if (value instanceof Number number) {

            try {

                return new BigDecimal(
                        number.toString()
                );
            }
            catch (NumberFormatException exception) {
                return null;
            }
        }

        if (value instanceof String stringValue) {

            try {

                return new BigDecimal(
                        stringValue
                );
            }
            catch (NumberFormatException exception) {
                return null;
            }
        }

        return null;
    }
}