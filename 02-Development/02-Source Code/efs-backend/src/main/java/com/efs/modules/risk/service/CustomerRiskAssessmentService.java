package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.CustomerHistory;
import com.efs.modules.customer.entity.CustomerRiskProfile;
import com.efs.modules.customer.mapper.CustomerRiskProfileMapper;
import com.efs.modules.customer.repository.CustomerHistoryRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.customer.repository.CustomerRiskProfileRepository;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRiskAssessmentService
        implements CustomerRiskAssessmentServiceInterface {

    private static final String HISTORY_EVENT_TYPE =
            "CUSTOMER_RISK_ASSESSED";

    private static final String AUDIT_EVENT_TYPE =
            "CUSTOMER_RISK_ASSESSED";

    private static final String AUDIT_ENTITY_TYPE =
            "CUSTOMER_RISK_PROFILE";

    private static final String AUDIT_ACTION =
            "CALCULATE";

    private static final String RISK_ENGINE =
            "RISK_ENGINE";

    private static final String DOMAIN_EVENT_TYPE =
            "RiskProfileChanged";

    private static final String DOMAIN_EVENT_SCHEMA_VERSION =
            "1.0";

    private static final String DOMAIN_AGGREGATE_TYPE =
            "CustomerRiskProfile";

    private final CustomerRepository customerRepository;

    private final CustomerRiskProfileRepository
            customerRiskProfileRepository;

    private final CustomerHistoryRepository
            customerHistoryRepository;

    private final CustomerRiskProfileMapper
            customerRiskProfileMapper;

    private final RiskScoringModelResolver
            riskScoringModelResolver;

    private final RiskCalculator riskCalculator;

    private final AuditEventServiceInterface
            auditEventService;

    private final DomainEventOutboxService
            domainEventOutboxService;

    private final CustomerRiskAssessmentAuditService
            customerRiskAssessmentAuditService;

    public CustomerRiskAssessmentService(
            CustomerRepository customerRepository,
            CustomerRiskProfileRepository
                    customerRiskProfileRepository,
            CustomerHistoryRepository
                    customerHistoryRepository,
            CustomerRiskProfileMapper
                    customerRiskProfileMapper,
            RiskScoringModelResolver
                    riskScoringModelResolver,
            RiskCalculator riskCalculator,
            AuditEventServiceInterface auditEventService,
            DomainEventOutboxService
                    domainEventOutboxService,
            CustomerRiskAssessmentAuditService
                    customerRiskAssessmentAuditService) {

        this.customerRepository =
                customerRepository;

        this.customerRiskProfileRepository =
                customerRiskProfileRepository;

        this.customerHistoryRepository =
                customerHistoryRepository;

        this.customerRiskProfileMapper =
                customerRiskProfileMapper;

        this.riskScoringModelResolver =
                riskScoringModelResolver;

        this.riskCalculator =
                riskCalculator;

        this.auditEventService =
                auditEventService;

        this.domainEventOutboxService =
                domainEventOutboxService;

        this.customerRiskAssessmentAuditService =
                customerRiskAssessmentAuditService;
    }

    @Override
    @Transactional
    public CustomerRiskProfileResponse createRiskAssessment(
            UUID customerId,
            CustomerRiskProfileRequest request) {

        try {
            return doCreateRiskAssessment(
                    customerId,
                    request
            );
        }
        catch (IllegalArgumentException exception) {

            if (isMissingEnabledFactor(exception)) {

                customerRiskAssessmentAuditService
                        .recordRejected(
                                customerId,
                                request.getCorrelationId(),
                                "CUSTOMER_INFORMATION_INSUFFICIENT",
                                exception
                        );
            }
            else {

                customerRiskAssessmentAuditService
                        .recordFailure(
                                customerId,
                                request.getCorrelationId(),
                                "CUSTOMER_RISK_ASSESSMENT_FAILED",
                                exception
                        );
            }

            throw exception;
        }
        catch (IllegalStateException exception) {

            if (isRiskConfigurationUnavailable(exception)) {

                customerRiskAssessmentAuditService
                        .recordRejected(
                                customerId,
                                request.getCorrelationId(),
                                "RISK_EVALUATION_RULES_UNAVAILABLE",
                                exception
                        );
            }
            else {

                customerRiskAssessmentAuditService
                        .recordFailure(
                                customerId,
                                request.getCorrelationId(),
                                "CUSTOMER_RISK_ASSESSMENT_FAILED",
                                exception
                        );
            }

            throw exception;
        }
        catch (DuplicateRecordException
                | ResourceNotFoundException exception) {

            throw exception;
        }
        catch (RuntimeException exception) {

            customerRiskAssessmentAuditService
                    .recordFailure(
                            customerId,
                            request.getCorrelationId(),
                            "CUSTOMER_RISK_ASSESSMENT_FAILED",
                            exception
                    );

            throw exception;
        }
    }

    private CustomerRiskProfileResponse doCreateRiskAssessment(
            UUID customerId,
            CustomerRiskProfileRequest request) {

        validateActiveCustomer(customerId);

        Optional<CustomerRiskProfileResponse> reusable =
                findReusableAssessment(
                        customerId,
                        request.getCorrelationId()
                );

        if (reusable.isPresent()) {

            CustomerRiskProfileResponse response =
                    reusable.get();

            recordReuseAudit(
                    customerId,
                    response,
                    request.getCorrelationId()
            );

            return response;
        }

        if (customerRiskProfileRepository
                .existsByCustomerIdAndDeletedAtIsNull(
                        customerId
                )) {

            throw new DuplicateRecordException(
                    "Customer risk profile already exists"
            );
        }

        RiskCalculationResult calculation =
                calculate(request);

        LocalDateTime now =
                LocalDateTime.now();

        CustomerRiskProfile profile =
                new CustomerRiskProfile();

        profile.setCustomerId(customerId);

        applyCalculatedState(
                profile,
                request,
                calculation
        );

        profile.setLastCalculation(now);
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        profile.setCreatedBy(request.getCreatedBy());
        profile.setUpdatedBy(request.getUpdatedBy());

        CustomerRiskProfile savedProfile =
                customerRiskProfileRepository.save(
                        profile
                );

        createHistory(
                customerId,
                null,
                null,
                calculation.overallRiskScore(),
                calculation.riskLevel(),
                request.getCorrelationId(),
                now
        );

        recordSuccessfulAssessmentAudit(
                customerId,
                savedProfile.getProfileId(),
                null,
                null,
                calculation,
                request.getCorrelationId(),
                false
        );

        publishRiskProfileChanged(
                customerId,
                savedProfile.getProfileId(),
                null,
                null,
                calculation,
                request.getCorrelationId(),
                now
        );

        return customerRiskProfileMapper.toResponse(
                savedProfile
        );
    }

    @Override
    @Transactional
    public CustomerRiskProfileResponse updateRiskAssessment(
            UUID customerId,
            CustomerRiskProfileRequest request) {

        try {
            return doUpdateRiskAssessment(
                    customerId,
                    request
            );
        }
        catch (IllegalArgumentException exception) {

            if (isMissingEnabledFactor(exception)) {

                customerRiskAssessmentAuditService
                        .recordRejected(
                                customerId,
                                request.getCorrelationId(),
                                "CUSTOMER_INFORMATION_INSUFFICIENT",
                                exception
                        );
            }
            else {

                customerRiskAssessmentAuditService
                        .recordFailure(
                                customerId,
                                request.getCorrelationId(),
                                "CUSTOMER_RISK_ASSESSMENT_FAILED",
                                exception
                        );
            }

            throw exception;
        }
        catch (IllegalStateException exception) {

            if (isRiskConfigurationUnavailable(exception)) {

                customerRiskAssessmentAuditService
                        .recordRejected(
                                customerId,
                                request.getCorrelationId(),
                                "RISK_EVALUATION_RULES_UNAVAILABLE",
                                exception
                        );
            }
            else {

                customerRiskAssessmentAuditService
                        .recordFailure(
                                customerId,
                                request.getCorrelationId(),
                                "CUSTOMER_RISK_ASSESSMENT_FAILED",
                                exception
                        );
            }

            throw exception;
        }
        catch (ResourceNotFoundException exception) {

            throw exception;
        }
        catch (RuntimeException exception) {

            customerRiskAssessmentAuditService
                    .recordFailure(
                            customerId,
                            request.getCorrelationId(),
                            "CUSTOMER_RISK_ASSESSMENT_FAILED",
                            exception
                    );

            throw exception;
        }
    }

    private CustomerRiskProfileResponse doUpdateRiskAssessment(
            UUID customerId,
            CustomerRiskProfileRequest request) {

        validateActiveCustomer(customerId);

        CustomerRiskProfile profile =
                customerRiskProfileRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Customer risk profile not found: "
                                                        + customerId
                                        )
                        );

        Optional<CustomerRiskProfileResponse> reusable =
                findReusableAssessment(
                        customerId,
                        request.getCorrelationId()
                );

        if (reusable.isPresent()) {

            CustomerRiskProfileResponse response =
                    reusable.get();

            recordReuseAudit(
                    customerId,
                    response,
                    request.getCorrelationId()
            );

            return response;
        }

        BigDecimal previousRiskScore =
                profile.getCurrentRiskScore();

        String previousRiskLevel =
                profile.getRiskLevel();

        RiskCalculationResult calculation =
                calculate(request);

        LocalDateTime now =
                LocalDateTime.now();

        applyCalculatedState(
                profile,
                request,
                calculation
        );

        profile.setLastCalculation(now);
        profile.setUpdatedAt(now);
        profile.setUpdatedBy(request.getUpdatedBy());

        CustomerRiskProfile savedProfile =
                customerRiskProfileRepository.save(
                        profile
                );

        createHistory(
                customerId,
                previousRiskScore,
                previousRiskLevel,
                calculation.overallRiskScore(),
                calculation.riskLevel(),
                request.getCorrelationId(),
                now
        );

        recordSuccessfulAssessmentAudit(
                customerId,
                savedProfile.getProfileId(),
                previousRiskScore,
                previousRiskLevel,
                calculation,
                request.getCorrelationId(),
                false
        );

        publishRiskProfileChanged(
                customerId,
                savedProfile.getProfileId(),
                previousRiskScore,
                previousRiskLevel,
                calculation,
                request.getCorrelationId(),
                now
        );

        return customerRiskProfileMapper.toResponse(
                savedProfile
        );
    }

    private RiskCalculationResult calculate(
            CustomerRiskProfileRequest request) {

        RiskScoringModel model =
                riskScoringModelResolver.resolveCustomer();

        Map<String, BigDecimal> factorScores =
                new LinkedHashMap<>();

        factorScores.put(
                "BEHAVIOR",
                request.getBehaviorScore()
        );

        factorScores.put(
                "FRAUD",
                request.getFraudScore()
        );

        factorScores.put(
                "AML",
                request.getAmlScore()
        );

        factorScores.put(
                "KYC",
                request.getKycScore()
        );

        factorScores.put(
                "DEVICE",
                request.getDeviceScore()
        );

        factorScores.put(
                "SANCTIONS",
                request.getSanctionsScore()
        );

        factorScores.put(
                "PEP",
                request.getPepScore()
        );

        factorScores.put(
                "WATCHLIST",
                request.getWatchlistScore()
        );

        return riskCalculator.calculate(
                model,
                factorScores
        );
    }

    private void applyCalculatedState(
            CustomerRiskProfile profile,
            CustomerRiskProfileRequest request,
            RiskCalculationResult calculation) {

        profile.setCurrentRiskScore(
                calculation.overallRiskScore()
        );

        profile.setRiskLevel(
                calculation.riskLevel()
        );

        profile.setBehaviorScore(
                request.getBehaviorScore()
        );

        profile.setFraudScore(
                request.getFraudScore()
        );

        profile.setAmlScore(
                request.getAmlScore()
        );

        profile.setKycScore(
                request.getKycScore()
        );

        profile.setDeviceScore(
                request.getDeviceScore()
        );

        profile.setSanctionsScore(
                request.getSanctionsScore()
        );

        profile.setPepScore(
                request.getPepScore()
        );

        profile.setWatchlistScore(
                request.getWatchlistScore()
        );
    }

    private Optional<CustomerRiskProfileResponse>
    findReusableAssessment(
            UUID customerId,
            UUID correlationId) {

        if (correlationId == null) {
            return Optional.empty();
        }

        Optional<CustomerHistory> latestAssessment =
                customerHistoryRepository
                        .findFirstByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                HISTORY_EVENT_TYPE
                        );

        if (latestAssessment.isEmpty()) {
            return Optional.empty();
        }

        if (!correlationId
                .toString()
                .equals(
                        latestAssessment
                                .get()
                                .getSourceReference()
                )) {

            return Optional.empty();
        }

        return customerRiskProfileRepository
                .findByCustomerIdAndDeletedAtIsNull(
                        customerId
                )
                .map(
                        customerRiskProfileMapper::toResponse
                );
    }

    private void createHistory(
            UUID customerId,
            BigDecimal previousRiskScore,
            String previousRiskLevel,
            BigDecimal newRiskScore,
            String newRiskLevel,
            UUID correlationId,
            LocalDateTime timestamp) {

        CustomerHistory history =
                new CustomerHistory();

        history.setCustomerId(customerId);

        history.setEventType(
                HISTORY_EVENT_TYPE
        );

        history.setPreviousRiskScore(
                previousRiskScore
        );

        history.setPreviousRiskLevel(
                previousRiskLevel
        );

        history.setNewRiskScore(
                newRiskScore
        );

        history.setNewRiskLevel(
                newRiskLevel
        );

        history.setEventTimestamp(
                timestamp
        );

        history.setSourceReference(
                correlationId.toString()
        );

        history.setCreatedAt(
                timestamp
        );

        customerHistoryRepository.save(
                history
        );
    }

    private void recordSuccessfulAssessmentAudit(
            UUID customerId,
            UUID profileId,
            BigDecimal previousRiskScore,
            String previousRiskLevel,
            RiskCalculationResult calculation,
            UUID correlationId,
            boolean reused) {

        Map<String, Object> factorScores =
                new LinkedHashMap<>();

        Map<String, Object> factorWeights =
                new LinkedHashMap<>();

        calculation.factorContributions()
                .forEach(contribution -> {

                    factorScores.put(
                            contribution.factorCode(),
                            contribution.score()
                    );

                    factorWeights.put(
                            contribution.factorCode(),
                            contribution.weight()
                    );
                });

        Map<String, Object> eventDetails =
                new LinkedHashMap<>();

        eventDetails.put(
                "customerId",
                customerId.toString()
        );

        eventDetails.put(
                "profileId",
                profileId.toString()
        );

        eventDetails.put(
                "modelName",
                calculation.modelName()
        );

        eventDetails.put(
                "modelVersion",
                calculation.modelVersion()
        );

        eventDetails.put(
                "factorScores",
                factorScores
        );

        eventDetails.put(
                "factorWeights",
                factorWeights
        );

        eventDetails.put(
                "previousRiskScore",
                previousRiskScore
        );

        eventDetails.put(
                "previousRiskLevel",
                previousRiskLevel
        );

        eventDetails.put(
                "currentRiskScore",
                calculation.overallRiskScore()
        );

        eventDetails.put(
                "riskLevel",
                calculation.riskLevel()
        );

        eventDetails.put(
                "reused",
                reused
        );

        createAuditEvent(
                profileId,
                correlationId,
                eventDetails
        );
    }

    private void recordReuseAudit(
            UUID customerId,
            CustomerRiskProfileResponse response,
            UUID correlationId) {

        Map<String, Object> eventDetails =
                auditEventService
                        .getAuditEventsByCorrelationId(
                                correlationId
                        )
                        .stream()
                        .filter(
                                audit ->
                                        AUDIT_EVENT_TYPE.equals(
                                                audit.getEventType()
                                        )
                                                && AUDIT_ENTITY_TYPE.equals(
                                                audit.getEntityType()
                                        )
                                                && "SUCCESS".equals(
                                                audit.getEventResult()
                                        )
                        )
                        .map(
                                AuditEventResponse::getEventDetails
                        )
                        .filter(
                                details ->
                                        details != null
                        )
                        .findFirst()
                        .map(
                                LinkedHashMap::new
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Customer risk assessment audit "
                                                        + "not found for correlationId: "
                                                        + correlationId
                                        )
                        );

        eventDetails.put(
                "customerId",
                customerId.toString()
        );

        eventDetails.put(
                "profileId",
                response.getProfileId().toString()
        );

        eventDetails.put(
                "currentRiskScore",
                response.getCurrentRiskScore()
        );

        eventDetails.put(
                "riskLevel",
                response.getRiskLevel()
        );

        eventDetails.put(
                "reused",
                true
        );

        createAuditEvent(
                response.getProfileId(),
                correlationId,
                eventDetails
        );
    }

    private void createAuditEvent(
            UUID profileId,
            UUID correlationId,
            Map<String, Object> eventDetails) {

        AuditEventRequest auditEventRequest =
                new AuditEventRequest();

        auditEventRequest.setEventType(
                AUDIT_EVENT_TYPE
        );

        auditEventRequest.setEntityType(
                AUDIT_ENTITY_TYPE
        );

        auditEventRequest.setEntityId(
                profileId
        );

        auditEventRequest.setAction(
                AUDIT_ACTION
        );

        auditEventRequest.setSourceComponent(
                RISK_ENGINE
        );

        auditEventRequest.setCorrelationId(
                correlationId
        );

        auditEventRequest.setEventResult(
                "SUCCESS"
        );

        auditEventRequest.setEventDetails(
                eventDetails
        );

        auditEventService.createAuditEvent(
                auditEventRequest
        );
    }

    private void publishRiskProfileChanged(
            UUID customerId,
            UUID profileId,
            BigDecimal previousRiskScore,
            String previousRiskLevel,
            RiskCalculationResult calculation,
            UUID correlationId,
            LocalDateTime occurredAt) {

        Map<String, Object> payload =
                new LinkedHashMap<>();

        payload.put(
                "customerId",
                customerId.toString()
        );

        payload.put(
                "riskProfileId",
                profileId.toString()
        );

        payload.put(
                "previousRiskScore",
                previousRiskScore
        );

        payload.put(
                "previousRiskLevel",
                previousRiskLevel
        );

        payload.put(
                "currentRiskScore",
                calculation.overallRiskScore()
        );

        payload.put(
                "riskLevel",
                calculation.riskLevel()
        );

        payload.put(
                "modelName",
                calculation.modelName()
        );

        payload.put(
                "modelVersion",
                calculation.modelVersion()
        );

        DomainEventEnvelope envelope =
                new DomainEventEnvelope();

        envelope.setEventType(
                DOMAIN_EVENT_TYPE
        );

        envelope.setSchemaVersion(
                DOMAIN_EVENT_SCHEMA_VERSION
        );

        envelope.setOccurredAt(
                occurredAt
        );

        envelope.setProducer(
                RISK_ENGINE
        );

        envelope.setCorrelationId(
                correlationId
        );

        envelope.setCausationId(null);
        envelope.setTenantId(null);

        envelope.setPayload(
                payload
        );

        envelope.setMetadata(
                Map.of()
        );

        domainEventOutboxService.persist(
                DOMAIN_AGGREGATE_TYPE,
                profileId,
                envelope
        );
    }

    private boolean isMissingEnabledFactor(
            IllegalArgumentException exception) {

        return exception.getMessage() != null
                && exception.getMessage().startsWith(
                        "Risk score is required for enabled factor:"
                );
    }

    private boolean isRiskConfigurationUnavailable(
            IllegalStateException exception) {

        return exception.getMessage() != null
                && exception.getMessage().startsWith(
                        "Required risk configuration is unavailable:"
                );
    }
    private void validateActiveCustomer(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(
                        customerId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Customer not found: "
                                                + customerId
                                )
                );
    }
}