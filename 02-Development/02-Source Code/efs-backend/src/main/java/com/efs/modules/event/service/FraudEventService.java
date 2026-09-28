package com.efs.modules.event.service;

import com.fasterxml.jackson.databind.JsonNode;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.event.dto.FraudEventRequest;
import com.efs.modules.event.dto.FraudEventResponse;
import com.efs.modules.event.entity.FraudEvent;
import com.efs.modules.event.mapper.FraudEventMapper;
import com.efs.modules.event.repository.FraudEventRepository;
import com.efs.shared.pagination.PageResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class FraudEventService
        implements FraudEventServiceInterface {
    private static final String FRAUD_EVENT_SEARCH_EVENT_TYPE =
            "FRAUD_EVENT_SEARCH";

    private static final String FRAUD_EVENT_SEARCH_ACTION =
            "SEARCH";

    private static final String INVALID_SEARCH_CRITERIA_REASON =
            "INVALID_SEARCH_CRITERIA";

    private static final String FRAUD_EVENT_SEARCH_FAILED_REASON =
            "FRAUD_EVENT_SEARCH_FAILED";

    private static final int FRAUD_EVENT_MAX_PAGE_SIZE = 100;

    private static final String SORT_OCCURRED_AT =
            "occurredAt";

    private static final String SORT_RECEIVED_AT =
            "receivedAt";

    private static final String SORT_CREATED_AT =
            "createdAt";

    private static final String SORT_ASC =
            "ASC";

    private static final String SORT_DESC =
            "DESC";

    private static final String EVENT_CREATE_PERMISSION =
            "event.create";

    private static final String EVENT_VIEW_PERMISSION =
            "event.view";

    private static final String FRAUD_EVENT_REGISTRATION_EVENT_TYPE =
            "FRAUD_EVENT_REGISTRATION";

    private static final String FRAUD_EVENT_REVIEW_EVENT_TYPE =
            "FRAUD_EVENT_REVIEW";

    private static final String FRAUD_EVENT_ENTITY_TYPE =
            "FRAUD_EVENT";

    private static final String FRAUD_EVENT_REGISTER_ACTION =
            "REGISTER";

    private static final String FRAUD_EVENT_REVIEW_ACTION =
            "REVIEW";

    private static final String FRAUD_EVENT_SOURCE_COMPONENT =
            "EVENT";
    private static final String FRAUD_EVENT_REGISTERED_EVENT_TYPE =
            "FraudEventRegistered";

    private static final String FRAUD_EVENT_REGISTERED_SCHEMA_VERSION =
            "1.0";

    private static final String FRAUD_EVENT_REGISTERED_PRODUCER =
            "Event Module";

    private static final String FRAUD_EVENT_AGGREGATE_TYPE =
            "FraudEvent";

    private final FraudEventRepository fraudEventRepository;
    private final TransactionServiceInterface transactionService;
    private final UserAccountLookupServiceInterface userAccountLookupService;
    private final AuditEventServiceInterface auditEventService;
    private final DomainEventOutboxService domainEventOutboxService;

    public FraudEventService(
            FraudEventRepository fraudEventRepository,
            TransactionServiceInterface transactionService,
            UserAccountLookupServiceInterface userAccountLookupService,
            AuditEventServiceInterface auditEventService,
            DomainEventOutboxService domainEventOutboxService) {

        this.fraudEventRepository =
                fraudEventRepository;

        this.transactionService =
                transactionService;

        this.userAccountLookupService =
                userAccountLookupService;

        this.auditEventService =
                auditEventService;

        this.domainEventOutboxService =
                domainEventOutboxService;
    }

    @Override
    @Transactional
    public FraudEventResponse registerEvent(
            FraudEventRequest request,
            SecurityContext securityContext) {

        if (request == null) {
            throw new RequestValidationException(
                    "Fraud event request is required."
            );
        }

        if (securityContext == null) {
            throw new RequestValidationException(
                    "Security context is required."
            );
        }

        requirePermission(
                securityContext,
                EVENT_CREATE_PERMISSION,
                request.getCorrelationId(),
                null,
                FRAUD_EVENT_REGISTRATION_EVENT_TYPE,
                FRAUD_EVENT_REGISTER_ACTION
        );

        UUID auditOrganizationId = null;
        UUID auditTenantId = securityContext.getTenantId();

        try {

            UserAccountReference actor =
                    userAccountLookupService.getAuthorizedUser(
                            securityContext.getUserId()
                    );

            UUID organizationId =
                    actor.organizationId();

            UUID tenantId =
                    actor.tenantId();

            auditOrganizationId = organizationId;
            auditTenantId = tenantId;

            validateAuthorizedScope(
                    securityContext,
                    actor
            );

            if (organizationId == null || tenantId == null) {
                throw new RequestValidationException(
                        "Organization and tenant context are required."
                );
            }

            if (fraudEventRepository
                    .existsByOrganizationIdAndTenantIdAndIdempotencyKey(
                            organizationId,
                            tenantId,
                            request.getIdempotencyKey())) {

                throw new DuplicateRecordException(
                        "Fraud event already exists for the supplied idempotency key."
                );
            }

            validateTransactionScope(
                    request.getTransactionId(),
                    organizationId,
                    tenantId
            );

            LocalDateTime receivedAt =
                    LocalDateTime.now();

            if (request.getOccurredAt() != null
                    && receivedAt.isBefore(
                            request.getOccurredAt())) {

                throw new RequestValidationException(
                        "Fraud event occurredAt cannot be after receivedAt."
                );
            }

            FraudEvent fraudEvent =
                    new FraudEvent(
                            organizationId,
                            tenantId,
                            request.getTransactionId(),
                            request.getEventType(),
                            request.getSourceType(),
                            request.getSourceReference(),
                            request.getIdempotencyKey(),
                            request.getCorrelationId(),
                            normalizePayload(request.getPayload()),
                            request.getOccurredAt(),
                            receivedAt
                    );

            FraudEvent persisted =
                    fraudEventRepository.saveAndFlush(
                            fraudEvent
                    );

            publishFraudEventRegistered(
                    persisted
            );

            recordFraudEventAudit(
                    securityContext,
                    organizationId,
                    tenantId,
                    persisted.getFraudEventId(),
                    persisted.getCorrelationId(),
                    EVENT_CREATE_PERMISSION,
                    FRAUD_EVENT_REGISTRATION_EVENT_TYPE,
                    FRAUD_EVENT_REGISTER_ACTION,
                    "SUCCESS",
                    null,
                    null,
                    false
            );
            return FraudEventMapper.toResponse(
                    persisted
            );
        } catch (
                DuplicateRecordException
                        | RequestValidationException
                        | ResourceNotFoundException
                        | AccessDeniedException ex) {

            recordFraudEventAudit(
                    securityContext,
                    auditOrganizationId,
                    auditTenantId,
                    null,
                    request.getCorrelationId(),
                    EVENT_CREATE_PERMISSION,
                    FRAUD_EVENT_REGISTRATION_EVENT_TYPE,
                    FRAUD_EVENT_REGISTER_ACTION,
                    "REJECTED",
                    null,
                    null,
                    false
            );

            throw ex;
        } catch (RuntimeException ex) {
            recordFraudEventAudit(
                    securityContext,
                    auditOrganizationId,
                    auditTenantId,
                    null,
                    request.getCorrelationId(),
                    EVENT_CREATE_PERMISSION,
                    FRAUD_EVENT_REGISTRATION_EVENT_TYPE,
                    FRAUD_EVENT_REGISTER_ACTION,
                    "FAILURE",
                    ex.getClass().getSimpleName(),
                    null,
                    true
            );

            throw ex;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public FraudEventResponse getEvent(
            UUID fraudEventId,
            SecurityContext securityContext) {

        if (fraudEventId == null) {
            throw new RequestValidationException(
                    "Fraud event id is required."
            );
        }

        if (securityContext == null) {
            throw new RequestValidationException(
                    "Security context is required."
            );
        }

        requirePermission(
                securityContext,
                EVENT_VIEW_PERMISSION,
                null,
                fraudEventId,
                FRAUD_EVENT_REVIEW_EVENT_TYPE,
                FRAUD_EVENT_REVIEW_ACTION
        );

        UUID auditOrganizationId = null;
        UUID auditTenantId = securityContext.getTenantId();

        try {

            UserAccountReference actor =
                    userAccountLookupService.getAuthorizedUser(
                            securityContext.getUserId()
                    );

            UUID organizationId =
                    actor.organizationId();

            UUID tenantId =
                    actor.tenantId();

            auditOrganizationId = organizationId;
            auditTenantId = tenantId;

            validateAuthorizedScope(
                    securityContext,
                    actor
            );

            FraudEvent fraudEvent =
                    fraudEventRepository
                            .findByFraudEventIdAndOrganizationIdAndTenantId(
                                    fraudEventId,
                                    organizationId,
                                    tenantId
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Fraud event not found."
                                            )
                            );

            recordFraudEventAudit(
                    securityContext,
                    organizationId,
                    tenantId,
                    fraudEvent.getFraudEventId(),
                    fraudEvent.getCorrelationId(),
                    EVENT_VIEW_PERMISSION,
                    FRAUD_EVENT_REVIEW_EVENT_TYPE,
                    FRAUD_EVENT_REVIEW_ACTION,
                    "SUCCESS",
                    null,
                    null,
                    false
            );
            return FraudEventMapper.toResponse(
                    fraudEvent
            );
        } catch (
                RequestValidationException
                        | ResourceNotFoundException
                        | AccessDeniedException ex) {

            recordFraudEventAudit(
                    securityContext,
                    auditOrganizationId,
                    auditTenantId,
                    fraudEventId,
                    null,
                    EVENT_VIEW_PERMISSION,
                    FRAUD_EVENT_REVIEW_EVENT_TYPE,
                    FRAUD_EVENT_REVIEW_ACTION,
                    "REJECTED",
                    null,
                    null,
                    false
            );

            throw ex;
        } catch (RuntimeException ex) {
            recordFraudEventAudit(
                    securityContext,
                    auditOrganizationId,
                    auditTenantId,
                    fraudEventId,
                    null,
                    EVENT_VIEW_PERMISSION,
                    FRAUD_EVENT_REVIEW_EVENT_TYPE,
                    FRAUD_EVENT_REVIEW_ACTION,
                    "FAILURE",
                    ex.getClass().getSimpleName(),
                    null,
                    true
            );

            throw ex;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FraudEventResponse> searchEvents(
            UUID transactionId,
            String eventType,
            String sourceType,
            String sourceReference,
            UUID correlationId,
            LocalDateTime occurredFrom,
            LocalDateTime occurredTo,
            LocalDateTime receivedFrom,
            LocalDateTime receivedTo,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext) {

        if (securityContext == null) {
            throw new RequestValidationException(
                    "Security context is required."
            );
        }

        UUID auditOrganizationId = null;
        UUID auditTenantId =
                securityContext.getTenantId();

        Map<String, Object> searchCriteria =
                buildSearchCriteria(
                        transactionId,
                        eventType,
                        sourceType,
                        sourceReference,
                        correlationId,
                        occurredFrom,
                        occurredTo,
                        receivedFrom,
                        receivedTo,
                        page,
                        size,
                        sort,
                        direction
                );

        try {
            if (!securityContext.hasPermission(
                    EVENT_VIEW_PERMISSION
            )) {
                throw new AccessDeniedException(
                        "Missing required permission: "
                                + EVENT_VIEW_PERMISSION
                );
            }

            UserAccountReference actor =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            UUID organizationId =
                    actor.organizationId();

            UUID tenantId =
                    actor.tenantId();

            auditOrganizationId =
                    organizationId;

            auditTenantId =
                    tenantId;

            validateAuthorizedScope(
                    securityContext,
                    actor
            );

            if (
                    organizationId == null ||
                    tenantId == null
            ) {
                throw new RequestValidationException(
                        "Organization and tenant context are required."
                );
            }

            validateFraudEventSearchRequest(
                    occurredFrom,
                    occurredTo,
                    receivedFrom,
                    receivedTo,
                    page,
                    size,
                    sort,
                    direction
            );

            Sort.Direction sortDirection =
                    SORT_ASC.equals(direction)
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

            Sort searchSort =
                    Sort.by(
                            new Sort.Order(
                                    sortDirection,
                                    sort
                            ),
                            new Sort.Order(
                                    sortDirection,
                                    "fraudEventId"
                            )
                    );

            PageRequest pageRequest =
                    PageRequest.of(
                            page,
                            size,
                            searchSort
                    );

            Specification<FraudEvent> specification =
                    (root, query, criteriaBuilder) -> {

                        List<Predicate> predicates =
                                new ArrayList<>();

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("organizationId"),
                                        organizationId
                                )
                        );

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("tenantId"),
                                        tenantId
                                )
                        );

                        if (transactionId != null) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get("transactionId"),
                                            transactionId
                                    )
                            );
                        }

                        if (eventType != null) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get("eventType"),
                                            eventType
                                    )
                            );
                        }

                        if (sourceType != null) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get("sourceType"),
                                            sourceType
                                    )
                            );
                        }

                        if (sourceReference != null) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get("sourceReference"),
                                            sourceReference
                                    )
                            );
                        }

                        if (correlationId != null) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get("correlationId"),
                                            correlationId
                                    )
                            );
                        }

                        if (occurredFrom != null) {
                            predicates.add(
                                    criteriaBuilder
                                            .greaterThanOrEqualTo(
                                                    root.get(
                                                            "occurredAt"
                                                    ),
                                                    occurredFrom
                                            )
                            );
                        }

                        if (occurredTo != null) {
                            predicates.add(
                                    criteriaBuilder
                                            .lessThanOrEqualTo(
                                                    root.get(
                                                            "occurredAt"
                                                    ),
                                                    occurredTo
                                            )
                            );
                        }

                        if (receivedFrom != null) {
                            predicates.add(
                                    criteriaBuilder
                                            .greaterThanOrEqualTo(
                                                    root.get(
                                                            "receivedAt"
                                                    ),
                                                    receivedFrom
                                            )
                            );
                        }

                        if (receivedTo != null) {
                            predicates.add(
                                    criteriaBuilder
                                            .lessThanOrEqualTo(
                                                    root.get(
                                                            "receivedAt"
                                                    ),
                                                    receivedTo
                                            )
                            );
                        }

                        return criteriaBuilder.and(
                                predicates.toArray(
                                        new Predicate[0]
                                )
                        );
                    };

            Page<FraudEvent> fraudEventPage =
                    fraudEventRepository.findAll(
                            specification,
                            pageRequest
                    );

            List<FraudEventResponse> content =
                    fraudEventPage
                            .getContent()
                            .stream()
                            .map(FraudEventMapper::toResponse)
                            .toList();

            recordFraudEventSearchAudit(
                    securityContext,
                    organizationId,
                    tenantId,
                    correlationId,
                    "SUCCESS",
                    null,
                    null,
                    searchCriteria,
                    false
            );

            return new PageResponse<>(
                    content,
                    fraudEventPage.getNumber(),
                    fraudEventPage.getSize(),
                    fraudEventPage.getTotalElements(),
                    fraudEventPage.getTotalPages(),
                    fraudEventPage.hasNext(),
                    fraudEventPage.hasPrevious()
            );

        } catch (
                RequestValidationException |
                AccessDeniedException ex
        ) {

            String reason =
                    ex instanceof AccessDeniedException
                            ? "MISSING_PERMISSION"
                            : INVALID_SEARCH_CRITERIA_REASON;

            recordFraudEventSearchAudit(
                    securityContext,
                    auditOrganizationId,
                    auditTenantId,
                    correlationId,
                    "REJECTED",
                    reason,
                    null,
                    searchCriteria,
                    false
            );

            throw ex;

        } catch (RuntimeException ex) {

            recordFraudEventSearchAudit(
                    securityContext,
                    auditOrganizationId,
                    auditTenantId,
                    correlationId,
                    "FAILURE",
                    FRAUD_EVENT_SEARCH_FAILED_REASON,
                    ex,
                    searchCriteria,
                    true
            );

            throw ex;
        }
    }

    private void validateFraudEventSearchRequest(
            LocalDateTime occurredFrom,
            LocalDateTime occurredTo,
            LocalDateTime receivedFrom,
            LocalDateTime receivedTo,
            int page,
            int size,
            String sort,
            String direction) {

        if (page < 0) {
            throw new RequestValidationException(
                    "Page must be greater than or equal to 0."
            );
        }

        if (
                size < 1 ||
                size > FRAUD_EVENT_MAX_PAGE_SIZE
        ) {
            throw new RequestValidationException(
                    "Size must be between 1 and 100."
            );
        }

        if (
                !SORT_OCCURRED_AT.equals(sort) &&
                !SORT_RECEIVED_AT.equals(sort) &&
                !SORT_CREATED_AT.equals(sort)
        ) {
            throw new RequestValidationException(
                    "Unsupported fraud event sort field."
            );
        }

        if (
                !SORT_ASC.equals(direction) &&
                !SORT_DESC.equals(direction)
        ) {
            throw new RequestValidationException(
                    "Direction must be ASC or DESC."
            );
        }

        if (
                occurredFrom != null &&
                occurredTo != null &&
                occurredFrom.isAfter(occurredTo)
        ) {
            throw new RequestValidationException(
                    "occurredFrom must be before or equal to occurredTo."
            );
        }

        if (
                receivedFrom != null &&
                receivedTo != null &&
                receivedFrom.isAfter(receivedTo)
        ) {
            throw new RequestValidationException(
                    "receivedFrom must be before or equal to receivedTo."
            );
        }
    }

    private Map<String, Object> buildSearchCriteria(
            UUID transactionId,
            String eventType,
            String sourceType,
            String sourceReference,
            UUID correlationId,
            LocalDateTime occurredFrom,
            LocalDateTime occurredTo,
            LocalDateTime receivedFrom,
            LocalDateTime receivedTo,
            int page,
            int size,
            String sort,
            String direction) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "transactionId",
                transactionId
        );

        criteria.put(
                "eventType",
                eventType
        );

        criteria.put(
                "sourceType",
                sourceType
        );

        criteria.put(
                "sourceReference",
                sourceReference
        );

        criteria.put(
                "correlationId",
                correlationId
        );

        criteria.put(
                "occurredFrom",
                occurredFrom
        );

        criteria.put(
                "occurredTo",
                occurredTo
        );

        criteria.put(
                "receivedFrom",
                receivedFrom
        );

        criteria.put(
                "receivedTo",
                receivedTo
        );

        criteria.put(
                "page",
                page
        );

        criteria.put(
                "size",
                size
        );

        criteria.put(
                "sort",
                sort
        );

        criteria.put(
                "direction",
                direction
        );

        return criteria;
    }

    private void recordFraudEventSearchAudit(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            UUID correlationId,
            String result,
            String reason,
            RuntimeException exception,
            Map<String, Object> searchCriteria,
            boolean requiresNew) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setUserId(
                securityContext != null
                        ? securityContext.getUserId()
                        : null
        );

        request.setEventType(
                FRAUD_EVENT_SEARCH_EVENT_TYPE
        );

        request.setEntityType(
                FRAUD_EVENT_ENTITY_TYPE
        );

        request.setEntityId(
                null
        );

        request.setAction(
                FRAUD_EVENT_SEARCH_ACTION
        );

        request.setSourceComponent(
                FRAUD_EVENT_SOURCE_COMPONENT
        );

        request.setEventResult(
                result
        );

        request.setCorrelationId(
                correlationId
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                EVENT_VIEW_PERMISSION
        );

        details.putAll(
                searchCriteria
        );

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception
                            .getClass()
                            .getSimpleName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        if (requiresNew) {
            auditEventService
                    .createAuditEventRequiresNew(
                            request
                    );
            return;
        }

        auditEventService.createAuditEvent(
                request
        );
    }
    private void requirePermission(
            SecurityContext securityContext,
            String requiredPermission,
            UUID correlationId,
            UUID fraudEventId,
            String eventType,
            String action) {

        if (!securityContext.hasPermission(
                requiredPermission
        )) {
            recordFraudEventAudit(
                    securityContext,
                    null,
                    securityContext.getTenantId(),
                    fraudEventId,
                    correlationId,
                    requiredPermission,
                    eventType,
                    action,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null,
                    false
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + requiredPermission
            );
        }
    }

    private void recordFraudEventAudit(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            UUID fraudEventId,
            UUID correlationId,
            String permissionCode,
            String eventType,
            String action,
            String eventResult,
            String reason,
            RuntimeException exception,
            boolean requiresNew) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                eventType
        );

        request.setEntityType(
                FRAUD_EVENT_ENTITY_TYPE
        );

        request.setEntityId(
                fraudEventId
        );

        request.setAction(
                action
        );

        request.setSourceComponent(
                FRAUD_EVENT_SOURCE_COMPONENT
        );

        request.setCorrelationId(
                correlationId
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                permissionCode
        );

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception.getClass().getSimpleName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        if (requiresNew) {
            auditEventService
                    .createAuditEventRequiresNew(
                            request
                    );

            return;
        }

        auditEventService.createAuditEvent(
                request
        );
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

    private void validateTransactionScope(
            UUID transactionId,
            UUID organizationId,
            UUID tenantId) {

        if (transactionId == null) {
            return;
        }

        TransactionResponse transaction =
                transactionService.getTransactionById(
                        transactionId
                );

        if (!Objects.equals(
                organizationId,
                transaction.getOrganizationId())) {

            throw new ResourceNotFoundException(
                    "Transaction not found."
            );
        }

        if (!Objects.equals(
                tenantId,
                transaction.getTenantId())) {

            throw new ResourceNotFoundException(
                    "Transaction not found."
            );
        }
    }


    private void publishFraudEventRegistered(
            FraudEvent fraudEvent) {

        if (fraudEvent.getFraudEventId() == null) {
            throw new IllegalStateException(
                    "Fraud event id is required for FraudEventRegistered event."
            );
        }

        DomainEventEnvelope envelope =
                new DomainEventEnvelope();

        envelope.setEventType(
                FRAUD_EVENT_REGISTERED_EVENT_TYPE
        );

        envelope.setSchemaVersion(
                FRAUD_EVENT_REGISTERED_SCHEMA_VERSION
        );

        envelope.setOccurredAt(
                fraudEvent.getReceivedAt()
        );

        envelope.setProducer(
                FRAUD_EVENT_REGISTERED_PRODUCER
        );

        envelope.setCorrelationId(
                fraudEvent.getCorrelationId()
        );

        envelope.setCausationId(
                null
        );

        envelope.setTenantId(
                fraudEvent.getTenantId()
        );

        envelope.setPayload(
                Map.of(
                        "fraudEventId",
                        fraudEvent.getFraudEventId()
                                .toString()
                )
        );

        envelope.setMetadata(
                Map.of()
        );

        domainEventOutboxService.persist(
                FRAUD_EVENT_AGGREGATE_TYPE,
                fraudEvent.getFraudEventId(),
                envelope
        );
    }
    private JsonNode normalizePayload(JsonNode payload) {
        return payload.deepCopy();
    }
}
