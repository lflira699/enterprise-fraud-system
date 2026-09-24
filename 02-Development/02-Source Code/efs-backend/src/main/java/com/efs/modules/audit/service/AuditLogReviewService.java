package com.efs.modules.audit.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.entity.AuditEvent;
import com.efs.modules.audit.mapper.AuditEventMapper;
import com.efs.modules.audit.repository.AuditEventRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuditLogReviewService
        implements AuditLogReviewServiceInterface {

    private static final String AUDIT_VIEW_PERMISSION =
            "audit.view";

    private static final String AUDIT_LOG_REVIEW_EVENT_TYPE =
            "AUDIT_LOG_REVIEW";

    private static final String AUDIT_LOG_REVIEW_ENTITY_TYPE =
            "AUDIT_EVENT";

    private static final String AUDIT_LOG_REVIEW_ACTION =
            "REVIEW";

    private static final String AUDIT_LOG_REVIEW_SOURCE_COMPONENT =
            "AUDIT";

    private static final String INVALID_CRITERIA_REASON =
            "INVALID_AUDIT_LOG_SEARCH_CRITERIA";

    private static final String REVIEW_FAILURE_REASON =
            "AUDIT_LOG_REVIEW_FAILED";

    private static final String MISSING_PERMISSION_REASON =
            "MISSING_PERMISSION";

    private static final String DEFAULT_SORT =
            "eventTimestamp";

    private static final String SORT_DIRECTION_ASC =
            "ASC";

    private static final String SORT_DIRECTION_DESC =
            "DESC";

    private static final int MAX_PAGE_SIZE =
            100;

    private final AuditEventRepository auditEventRepository;
    private final AuditEventMapper auditEventMapper;
    private final AuditEventServiceInterface auditEventService;
    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    public AuditLogReviewService(
            AuditEventRepository auditEventRepository,
            AuditEventMapper auditEventMapper,
            AuditEventServiceInterface auditEventService,
            UserAccountLookupServiceInterface
                    userAccountLookupService) {

        this.auditEventRepository =
                auditEventRepository;

        this.auditEventMapper =
                auditEventMapper;

        this.auditEventService =
                auditEventService;

        this.userAccountLookupService =
                userAccountLookupService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditEventResponse> searchAuditEvents(
            UUID userId,
            String from,
            String to,
            String entityType,
            UUID entityId,
            String action,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> criteria =
                buildCriteria(
                        userId,
                        from,
                        to,
                        entityType,
                        entityId,
                        action,
                        page,
                        size,
                        sort,
                        direction
                );

        requireAuditViewPermission(
                securityContext,
                criteria
        );

        UserAccountReference authorizedUser =
                null;

        try {

            authorizedUser =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            validateRequest(
                    from,
                    to,
                    entityType,
                    action,
                    page,
                    size,
                    sort,
                    direction
            );

            LocalDateTime fromTimestamp =
                    parseTimestamp(
                            from,
                            "from"
                    );

            LocalDateTime toTimestamp =
                    parseTimestamp(
                            to,
                            "to"
                    );

            if (
                    fromTimestamp != null
                            && toTimestamp != null
                            && fromTimestamp.isAfter(
                                    toTimestamp
                            )
            ) {
                throw new RequestValidationException(
                        "from must be before or equal to to"
                );
            }

            Sort.Direction sortDirection =
                    SORT_DIRECTION_ASC.equalsIgnoreCase(
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

            UUID organizationId =
                    authorizedUser.organizationId();

            UUID tenantId =
                    authorizedUser.tenantId();

            Specification<AuditEvent> specification =
                    buildSpecification(
                            organizationId,
                            tenantId,
                            userId,
                            fromTimestamp,
                            toTimestamp,
                            entityType,
                            entityId,
                            action
                    );

            Page<AuditEvent> resultPage =
                    auditEventRepository.findAll(
                            specification,
                            pageRequest
                    );

            List<AuditEventResponse> content =
                    resultPage
                            .getContent()
                            .stream()
                            .map(
                                    auditEventMapper::toResponse
                            )
                            .toList();

            PageResponse<AuditEventResponse> response =
                    new PageResponse<>(
                            content,
                            resultPage.getNumber(),
                            resultPage.getSize(),
                            resultPage.getTotalElements(),
                            resultPage.getTotalPages(),
                            resultPage.hasNext(),
                            resultPage.hasPrevious()
                    );

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "SUCCESS",
                    null,
                    criteria,
                    null
            );

            return response;
        }
        catch (RequestValidationException exception) {

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "REJECTED",
                    INVALID_CRITERIA_REASON,
                    criteria,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "FAILURE",
                    REVIEW_FAILURE_REASON,
                    criteria,
                    exception
            );

            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEventResponse getAuditEventById(
            UUID auditEventId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "auditEventId",
                auditEventId == null
                        ? null
                        : auditEventId.toString()
        );

        requireAuditViewPermission(
                securityContext,
                criteria
        );

        UserAccountReference authorizedUser =
                null;

        AuditEvent auditEvent;

        try {

            authorizedUser =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            Specification<AuditEvent> specification =
                    buildAuthorizedScope(
                            authorizedUser
                    ).and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.equal(
                                            root.get(
                                                    "auditEventId"
                                            ),
                                            auditEventId
                                    )
                    );

            auditEvent =
                    auditEventRepository
                            .findOne(
                                    specification
                            )
                            .orElse(
                                    null
                            );
        }
        catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "FAILURE",
                    REVIEW_FAILURE_REASON,
                    criteria,
                    exception
            );

            throw exception;
        }

        recordAudit(
                securityContext,
                authorizedUser,
                "SUCCESS",
                null,
                criteria,
                null
        );

        if (auditEvent == null) {
            throw new ResourceNotFoundException(
                    "Audit event not found: "
                            + auditEventId
            );
        }

        return auditEventMapper.toResponse(
                auditEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByEventType(
            String eventType,
            SecurityContext securityContext) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "eventType",
                eventType
        );

        return executeScopedListRead(
                securityContext,
                criteria,
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.equal(
                                root.get(
                                        "eventType"
                                ),
                                eventType
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByEntity(
            String entityType,
            UUID entityId,
            SecurityContext securityContext) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "entityType",
                entityType
        );

        criteria.put(
                "entityId",
                entityId == null
                        ? null
                        : entityId.toString()
        );

        return executeScopedListRead(
                securityContext,
                criteria,
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.and(
                                criteriaBuilder.equal(
                                        root.get(
                                                "entityType"
                                        ),
                                        entityType
                                ),
                                criteriaBuilder.equal(
                                        root.get(
                                                "entityId"
                                        ),
                                        entityId
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByUserId(
            UUID userId,
            SecurityContext securityContext) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "userId",
                userId == null
                        ? null
                        : userId.toString()
        );

        return executeScopedListRead(
                securityContext,
                criteria,
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.equal(
                                root.get(
                                        "userId"
                                ),
                                userId
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByOrganizationId(
            UUID organizationId,
            SecurityContext securityContext) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "organizationId",
                organizationId == null
                        ? null
                        : organizationId.toString()
        );

        return executeScopedListRead(
                securityContext,
                criteria,
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.equal(
                                root.get(
                                        "organizationId"
                                ),
                                organizationId
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByCorrelationId(
            UUID correlationId,
            SecurityContext securityContext) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "correlationId",
                correlationId == null
                        ? null
                        : correlationId.toString()
        );

        return executeScopedListRead(
                securityContext,
                criteria,
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.equal(
                                root.get(
                                        "correlationId"
                                ),
                                correlationId
                        )
        );
    }

    private List<AuditEventResponse> executeScopedListRead(
            SecurityContext securityContext,
            Map<String, Object> criteria,
            Specification<AuditEvent> requestedFilter) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireAuditViewPermission(
                securityContext,
                criteria
        );

        UserAccountReference authorizedUser =
                null;

        try {

            authorizedUser =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            Specification<AuditEvent> specification =
                    buildAuthorizedScope(
                            authorizedUser
                    ).and(
                            requestedFilter
                    );

            List<AuditEventResponse> response =
                    auditEventRepository
                            .findAll(
                                    specification,
                                    Sort.by(
                                            Sort.Direction.DESC,
                                            DEFAULT_SORT
                                    )
                            )
                            .stream()
                            .map(
                                    auditEventMapper::toResponse
                            )
                            .toList();

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "SUCCESS",
                    null,
                    criteria,
                    null
            );

            return response;
        }
        catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "FAILURE",
                    REVIEW_FAILURE_REASON,
                    criteria,
                    exception
            );

            throw exception;
        }
    }

    private Specification<AuditEvent> buildAuthorizedScope(
            UserAccountReference authorizedUser) {

        return buildSpecification(
                authorizedUser.organizationId(),
                authorizedUser.tenantId(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
    private void requireAuditViewPermission(
            SecurityContext securityContext,
            Map<String, Object> criteria) {

        if (
                !securityContext.hasPermission(
                        AUDIT_VIEW_PERMISSION
                )
        ) {

            recordAudit(
                    securityContext,
                    null,
                    "REJECTED",
                    MISSING_PERMISSION_REASON,
                    criteria,
                    null
            );

            throw new AccessDeniedException(
                    "Missing permission: "
                            + AUDIT_VIEW_PERMISSION
            );
        }
    }

    private void validateRequest(
            String from,
            String to,
            String entityType,
            String action,
            int page,
            int size,
            String sort,
            String direction) {

        if (page < 0) {
            throw new RequestValidationException(
                    "Page must be greater than or equal to 0"
            );
        }

        if (
                size < 1
                        || size > MAX_PAGE_SIZE
        ) {
            throw new RequestValidationException(
                    "Size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }

        if (
                !DEFAULT_SORT.equals(
                        sort
                )
        ) {
            throw new RequestValidationException(
                    "Unsupported audit log sort field: "
                            + sort
            );
        }

        if (
                !SORT_DIRECTION_ASC.equalsIgnoreCase(
                        direction
                )
                        && !SORT_DIRECTION_DESC.equalsIgnoreCase(
                                direction
                        )
        ) {
            throw new RequestValidationException(
                    "Unsupported sort direction: "
                            + direction
            );
        }

        if (
                from != null
                        && from.isBlank()
        ) {
            throw new RequestValidationException(
                    "from cannot be blank"
            );
        }

        if (
                to != null
                        && to.isBlank()
        ) {
            throw new RequestValidationException(
                    "to cannot be blank"
            );
        }

        if (
                entityType != null
                        && entityType.isBlank()
        ) {
            throw new RequestValidationException(
                    "entityType cannot be blank"
            );
        }

        if (
                action != null
                        && action.isBlank()
        ) {
            throw new RequestValidationException(
                    "action cannot be blank"
            );
        }
    }

    private LocalDateTime parseTimestamp(
            String value,
            String fieldName) {

        if (value == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(
                    value
            );
        }
        catch (DateTimeParseException exception) {

            throw new RequestValidationException(
                    fieldName
                            + " must use ISO-8601 local date-time format",
                    exception
            );
        }
    }

    private Specification<AuditEvent> buildSpecification(
            UUID organizationId,
            UUID tenantId,
            UUID userId,
            LocalDateTime from,
            LocalDateTime to,
            String entityType,
            UUID entityId,
            String action) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            predicates.add(
                    criteriaBuilder.equal(
                            root.get(
                                    "organizationId"
                            ),
                            organizationId
                    )
            );

            if (tenantId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get(
                                        "tenantId"
                                ),
                                tenantId
                        )
                );
            }

            if (userId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get(
                                        "userId"
                                ),
                                userId
                        )
                );
            }

            if (from != null) {
                predicates.add(
                        criteriaBuilder
                                .greaterThanOrEqualTo(
                                        root.get(
                                                "eventTimestamp"
                                        ),
                                        from
                                )
                );
            }

            if (to != null) {
                predicates.add(
                        criteriaBuilder
                                .lessThanOrEqualTo(
                                        root.get(
                                                "eventTimestamp"
                                        ),
                                        to
                                )
                );
            }

            if (hasText(entityType)) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get(
                                        "entityType"
                                ),
                                entityType
                        )
                );
            }

            if (entityId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get(
                                        "entityId"
                                ),
                                entityId
                        )
                );
            }

            if (hasText(action)) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get(
                                        "action"
                                ),
                                action
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }

    private Map<String, Object> buildCriteria(
            UUID userId,
            String from,
            String to,
            String entityType,
            UUID entityId,
            String action,
            int page,
            int size,
            String sort,
            String direction) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put(
                "userId",
                userId == null
                        ? null
                        : userId.toString()
        );

        criteria.put(
                "from",
                from
        );

        criteria.put(
                "to",
                to
        );

        criteria.put(
                "entityType",
                entityType
        );

        criteria.put(
                "entityId",
                entityId == null
                        ? null
                        : entityId.toString()
        );

        criteria.put(
                "action",
                action
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

    private void recordAudit(
            SecurityContext securityContext,
            UserAccountReference authorizedUser,
            String eventResult,
            String reason,
            Map<String, Object> criteria,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        if (authorizedUser != null) {

            request.setOrganizationId(
                    authorizedUser.organizationId()
            );

            request.setTenantId(
                    authorizedUser.tenantId()
            );
        }

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                AUDIT_LOG_REVIEW_EVENT_TYPE
        );

        request.setEntityType(
                AUDIT_LOG_REVIEW_ENTITY_TYPE
        );

        request.setEntityId(
                null
        );

        request.setAction(
                AUDIT_LOG_REVIEW_ACTION
        );

        request.setSourceComponent(
                AUDIT_LOG_REVIEW_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                AUDIT_VIEW_PERMISSION
        );

        details.put(
                "criteria",
                new LinkedHashMap<>(
                        criteria
                )
        );

        if (authorizedUser != null) {

            Map<String, Object> scope =
                    new LinkedHashMap<>();

            scope.put(
                    "organizationId",
                    authorizedUser.organizationId()
                            .toString()
            );

            scope.put(
                    "tenantId",
                    authorizedUser.tenantId()
                            == null
                            ? null
                            : authorizedUser.tenantId()
                                    .toString()
            );

            details.put(
                    "scope",
                    scope
            );
        }

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
                            .getName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private boolean hasText(
            String value) {

        return value != null
                && !value.isBlank();
    }
}