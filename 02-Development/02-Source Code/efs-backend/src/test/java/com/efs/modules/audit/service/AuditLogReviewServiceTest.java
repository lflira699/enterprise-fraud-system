package com.efs.modules.audit.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.entity.AuditEvent;
import com.efs.modules.audit.mapper.AuditEventMapper;
import com.efs.modules.audit.repository.AuditEventRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuditLogReviewServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "10101010-1010-1010-1010-101010101010"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "12121212-1212-1212-1212-121212121212"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "13131313-1313-1313-1313-131313131313"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "14141414-1414-1414-1414-141414141414"
            );

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "15151515-1515-1515-1515-151515151515"
            );

    private AuditEventRepository repository;
    private AuditEventServiceInterface auditEventService;
    private UserAccountLookupServiceInterface
            userAccountLookupService;
    private AuditLogReviewService service;

    @BeforeEach
    void setUp() {

        repository =
                mock(
                        AuditEventRepository.class
                );

        auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        userAccountLookupService =
                mock(
                        UserAccountLookupServiceInterface.class
                );

        service =
                new AuditLogReviewService(
                        repository,
                        new AuditEventMapper(),
                        auditEventService,
                        userAccountLookupService
                );
    }

    @Test
    void shouldReturnFilteredPageAndRecordSuccessAudit() {

        SecurityContext securityContext =
                context(
                        TENANT_ID,
                        true
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                authorizedUser(
                        TENANT_ID
                )
        );

        AuditEvent event =
                auditEvent(
                        TENANT_ID
                );

        Pageable returnedPageable =
                PageRequest.of(
                        1,
                        10,
                        Sort.by(
                                Sort.Direction.ASC,
                                "eventTimestamp"
                        )
                );

        Page<AuditEvent> repositoryPage =
                new PageImpl<>(
                        List.of(
                                event
                        ),
                        returnedPageable,
                        11
                );

        when(
                repository.findAll(
                        any(Specification.class),
                        any(Pageable.class)
                )
        ).thenReturn(
                repositoryPage
        );

        PageResponse<AuditEventResponse> response =
                service.searchAuditEvents(
                        USER_ID,
                        "2026-09-01T00:00:00",
                        "2026-09-16T23:59:59",
                        "CASE",
                        ENTITY_ID,
                        "UPDATE",
                        1,
                        10,
                        "eventTimestamp",
                        "ASC",
                        securityContext
                );

        assertEquals(
                1,
                response.getContent().size()
        );

        assertEquals(
                AUDIT_EVENT_ID,
                response.getContent()
                        .get(0)
                        .getAuditEventId()
        );

        assertEquals(
                1,
                response.getPage()
        );

        assertEquals(
                10,
                response.getSize()
        );

        assertEquals(
                11L,
                response.getTotalElements()
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(repository)
                .findAll(
                        any(Specification.class),
                        pageableCaptor.capture()
                );

        Pageable requestedPageable =
                pageableCaptor.getValue();

        assertEquals(
                1,
                requestedPageable.getPageNumber()
        );

        assertEquals(
                10,
                requestedPageable.getPageSize()
        );

        Sort.Order sortOrder =
                requestedPageable
                        .getSort()
                        .getOrderFor(
                                "eventTimestamp"
                        );

        assertEquals(
                Sort.Direction.ASC,
                sortOrder.getDirection()
        );

        AuditEventRequest audit =
                captureAudit();

        assertEquals(
                ORGANIZATION_ID,
                audit.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                audit.getTenantId()
        );

        assertEquals(
                USER_ID,
                audit.getUserId()
        );

        assertEquals(
                SESSION_ID,
                audit.getSessionId()
        );

        assertEquals(
                "AUDIT_LOG_REVIEW",
                audit.getEventType()
        );

        assertEquals(
                "AUDIT_EVENT",
                audit.getEntityType()
        );

        assertNull(
                audit.getEntityId()
        );

        assertEquals(
                "REVIEW",
                audit.getAction()
        );

        assertEquals(
                "AUDIT",
                audit.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                "audit.view",
                audit.getEventDetails()
                        .get("permissionCode")
        );

        Map<?, ?> criteria =
                (Map<?, ?>)
                        audit.getEventDetails()
                                .get("criteria");

        assertEquals(
                USER_ID.toString(),
                criteria.get("userId")
        );

        assertEquals(
                "2026-09-01T00:00:00",
                criteria.get("from")
        );

        assertEquals(
                "2026-09-16T23:59:59",
                criteria.get("to")
        );

        assertEquals(
                "CASE",
                criteria.get("entityType")
        );

        assertEquals(
                ENTITY_ID.toString(),
                criteria.get("entityId")
        );

        assertEquals(
                "UPDATE",
                criteria.get("action")
        );

        assertEquals(
                1,
                criteria.get("page")
        );

        assertEquals(
                10,
                criteria.get("size")
        );

        assertEquals(
                "eventTimestamp",
                criteria.get("sort")
        );

        assertEquals(
                "ASC",
                criteria.get("direction")
        );

        Map<?, ?> scope =
                (Map<?, ?>)
                        audit.getEventDetails()
                                .get("scope");

        assertEquals(
                ORGANIZATION_ID.toString(),
                scope.get("organizationId")
        );

        assertEquals(
                TENANT_ID.toString(),
                scope.get("tenantId")
        );

        assertEquals(
                false,
                audit.getEventDetails()
                        .containsKey("results")
        );

        assertEquals(
                false,
                audit.getEventDetails()
                        .containsKey("content")
        );

        verify(
                auditEventService,
                never()
        ).createAuditEvent(
                any()
        );
    }

    @Test
    void shouldApplyOrganizationAndTenantScope() {

        prepareEmptySearch(
                TENANT_ID
        );

        service.searchAuditEvents(
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                25,
                "eventTimestamp",
                "DESC",
                context(
                        TENANT_ID,
                        true
                )
        );

        Specification<AuditEvent> specification =
                captureSpecification();

        assertScope(
                specification,
                ORGANIZATION_ID,
                TENANT_ID
        );
    }

    @Test
    void shouldUseOrganizationScopeWhenTenantIsNull() {

        prepareEmptySearch(
                null
        );

        service.searchAuditEvents(
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                25,
                "eventTimestamp",
                "DESC",
                context(
                        null,
                        true
                )
        );

        Specification<AuditEvent> specification =
                captureSpecification();

        assertScope(
                specification,
                ORGANIZATION_ID,
                null
        );
    }

    @Test
    void shouldReturnEmptyPageAsSuccess() {

        prepareEmptySearch(
                TENANT_ID
        );

        PageResponse<AuditEventResponse> response =
                service.searchAuditEvents(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        25,
                        "eventTimestamp",
                        "DESC",
                        context(
                                TENANT_ID,
                                true
                        )
                );

        assertTrue(
                response.getContent().isEmpty()
        );

        assertEquals(
                0L,
                response.getTotalElements()
        );

        AuditEventRequest audit =
                captureAudit();

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertNull(
                audit.getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void shouldRejectMissingPermissionBeforeAuthorizedLookup() {

        SecurityContext securityContext =
                context(
                        TENANT_ID,
                        false
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.searchAuditEvents(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                25,
                                "eventTimestamp",
                                "DESC",
                                securityContext
                        )
        );

        verifyNoInteractions(
                userAccountLookupService,
                repository
        );

        AuditEventRequest audit =
                captureAudit();

        assertEquals(
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "MISSING_PERMISSION",
                audit.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                "audit.view",
                audit.getEventDetails()
                        .get("permissionCode")
        );

        assertNull(
                audit.getOrganizationId()
        );

        assertNull(
                audit.getTenantId()
        );
    }

    @Test
    void shouldRejectFromAfterTo() {

        prepareAuthorizedUser(
                TENANT_ID
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.searchAuditEvents(
                                null,
                                "2026-09-16T12:00:00",
                                "2026-09-15T12:00:00",
                                null,
                                null,
                                null,
                                0,
                                25,
                                "eventTimestamp",
                                "DESC",
                                context(
                                        TENANT_ID,
                                        true
                                )
                        )
        );

        verify(
                repository,
                never()
        ).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

        assertRejectedCriteriaAudit();
    }

    @Test
    void shouldRejectInvalidTimestamp() {

        prepareAuthorizedUser(
                TENANT_ID
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.searchAuditEvents(
                                null,
                                "not-a-timestamp",
                                null,
                                null,
                                null,
                                null,
                                0,
                                25,
                                "eventTimestamp",
                                "DESC",
                                context(
                                        TENANT_ID,
                                        true
                                )
                        )
        );

        verify(
                repository,
                never()
        ).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

        assertRejectedCriteriaAudit();
    }

    @Test
    void shouldRejectInvalidPageSize() {

        prepareAuthorizedUser(
                TENANT_ID
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.searchAuditEvents(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                101,
                                "eventTimestamp",
                                "DESC",
                                context(
                                        TENANT_ID,
                                        true
                                )
                        )
        );

        verify(
                repository,
                never()
        ).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

        assertRejectedCriteriaAudit();
    }

    @Test
    void shouldRejectUnsupportedSort() {

        prepareAuthorizedUser(
                TENANT_ID
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.searchAuditEvents(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                25,
                                "userId",
                                "DESC",
                                context(
                                        TENANT_ID,
                                        true
                                )
                        )
        );

        verify(
                repository,
                never()
        ).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

        assertRejectedCriteriaAudit();
    }

    @Test
    void shouldRejectUnsupportedDirection() {

        prepareAuthorizedUser(
                TENANT_ID
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.searchAuditEvents(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                25,
                                "eventTimestamp",
                                "SIDEWAYS",
                                context(
                                        TENANT_ID,
                                        true
                                )
                        )
        );

        verify(
                repository,
                never()
        ).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

        assertRejectedCriteriaAudit();
    }

    @Test
    void shouldAuditUnexpectedRepositoryFailure() {

        prepareAuthorizedUser(
                TENANT_ID
        );

        RuntimeException failure =
                new IllegalStateException(
                        "repository failure"
                );

        when(
                repository.findAll(
                        any(Specification.class),
                        any(Pageable.class)
                )
        ).thenThrow(
                failure
        );

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                service.searchAuditEvents(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0,
                                        25,
                                        "eventTimestamp",
                                        "DESC",
                                        context(
                                                TENANT_ID,
                                                true
                                        )
                                )
                );

        assertSame(
                failure,
                thrown
        );

        AuditEventRequest audit =
                captureAudit();

        assertEquals(
                "FAILURE",
                audit.getEventResult()
        );

        assertEquals(
                "AUDIT_LOG_REVIEW_FAILED",
                audit.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                IllegalStateException.class.getName(),
                audit.getEventDetails()
                        .get("errorType")
        );

        assertEquals(
                "repository failure",
                audit.getEventDetails()
                        .get("errorMessage")
        );
    }

    private void prepareAuthorizedUser(
            UUID tenantId) {

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                authorizedUser(
                        tenantId
                )
        );
    }

    private void prepareEmptySearch(
            UUID tenantId) {

        prepareAuthorizedUser(
                tenantId
        );

        when(
                repository.findAll(
                        any(Specification.class),
                        any(Pageable.class)
                )
        ).thenReturn(
                Page.empty()
        );
    }

    private UserAccountReference authorizedUser(
            UUID tenantId) {

        return new UserAccountReference(
                USER_ID,
                ORGANIZATION_ID,
                tenantId,
                "audit.review@example.com"
        );
    }

    private SecurityContext context(
            UUID tenantId,
            boolean auditView) {

        return new SecurityContext(
                USER_ID,
                tenantId,
                SESSION_ID,
                Set.of(),
                auditView
                        ? Set.of(
                                "audit.view"
                        )
                        : Set.of(),
                Set.of()
        );
    }

    private AuditEvent auditEvent(
            UUID tenantId) {

        AuditEvent event =
                new AuditEvent();

        event.setAuditEventId(
                AUDIT_EVENT_ID
        );

        event.setEventTimestamp(
                LocalDateTime.of(
                        2026,
                        9,
                        10,
                        12,
                        0
                )
        );

        event.setOrganizationId(
                ORGANIZATION_ID
        );

        event.setTenantId(
                tenantId
        );

        event.setUserId(
                USER_ID
        );

        event.setEventType(
                "CASE_REVIEW"
        );

        event.setEntityType(
                "CASE"
        );

        event.setEntityId(
                ENTITY_ID
        );

        event.setAction(
                "UPDATE"
        );

        event.setSourceComponent(
                "CASE"
        );

        event.setEventResult(
                "SUCCESS"
        );

        event.setEventDetails(
                Map.of(
                        "source",
                        "test"
                )
        );

        return event;
    }

    private AuditEventRequest captureAudit() {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        return captor.getValue();
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private Specification<AuditEvent>
    captureSpecification() {

        ArgumentCaptor<Specification> captor =
                ArgumentCaptor.forClass(
                        Specification.class
                );

        verify(repository)
                .findAll(
                        captor.capture(),
                        any(Pageable.class)
                );

        return (Specification<AuditEvent>)
                captor.getValue();
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private void assertScope(
            Specification<AuditEvent> specification,
            UUID organizationId,
            UUID tenantId) {

        Root root =
                mock(
                        Root.class
                );

        CriteriaQuery query =
                mock(
                        CriteriaQuery.class
                );

        CriteriaBuilder criteriaBuilder =
                mock(
                        CriteriaBuilder.class
                );

        Path organizationPath =
                mock(
                        Path.class
                );

        Path tenantPath =
                mock(
                        Path.class
                );

        Predicate organizationPredicate =
                mock(
                        Predicate.class
                );

        Predicate tenantPredicate =
                mock(
                        Predicate.class
                );

        Predicate combinedPredicate =
                mock(
                        Predicate.class
                );

        when(
                root.get(
                        "organizationId"
                )
        ).thenReturn(
                organizationPath
        );

        when(
                criteriaBuilder.equal(
                        organizationPath,
                        organizationId
                )
        ).thenReturn(
                organizationPredicate
        );

        if (tenantId != null) {

            when(
                    root.get(
                            "tenantId"
                    )
            ).thenReturn(
                    tenantPath
            );

            when(
                    criteriaBuilder.equal(
                            tenantPath,
                            tenantId
                    )
            ).thenReturn(
                    tenantPredicate
            );
        }

        when(
                criteriaBuilder.and(
                        any(Predicate[].class)
                )
        ).thenReturn(
                combinedPredicate
        );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertSame(
                combinedPredicate,
                result
        );

        verify(criteriaBuilder)
                .equal(
                        organizationPath,
                        organizationId
                );

        if (tenantId != null) {

            verify(criteriaBuilder)
                    .equal(
                            tenantPath,
                            tenantId
                    );
        }
        else {

            verify(
                    root,
                    never()
            ).get(
                    "tenantId"
            );
        }
    }

    private void assertRejectedCriteriaAudit() {

        AuditEventRequest audit =
                captureAudit();

        assertEquals(
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "INVALID_AUDIT_LOG_SEARCH_CRITERIA",
                audit.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                ORGANIZATION_ID,
                audit.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                audit.getTenantId()
        );
    }
}