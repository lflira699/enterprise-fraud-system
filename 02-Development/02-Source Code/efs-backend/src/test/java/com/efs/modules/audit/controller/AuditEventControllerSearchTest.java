package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.audit.service.AuditLogReviewServiceInterface;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuditEventControllerSearchTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "16161616-1616-1616-1616-161616161616"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "17171717-1717-1717-1717-171717171717"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "18181818-1818-1818-1818-181818181818"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "19191919-1919-1919-1919-191919191919"
            );

    private AuditEventServiceInterface
            auditEventService;

    private AuditLogReviewServiceInterface
            reviewService;

    private SecurityContextProvider
            securityContextProvider;

    private AuditEventController controller;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {

        auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        reviewService =
                mock(
                        AuditLogReviewServiceInterface.class
                );

        securityContextProvider =
                mock(
                        SecurityContextProvider.class
                );

        controller =
                new AuditEventController(
                        auditEventService,
                        reviewService,
                        securityContextProvider
                );

        securityContext =
                new SecurityContext(
                        USER_ID,
                        TENANT_ID,
                        SESSION_ID,
                        Set.of(),
                        Set.of(
                                "audit.view"
                        ),
                        Set.of()
                );

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );
    }

    @AfterEach
    void shouldNotUseLegacyAuditEventServiceForReads() {

        verifyNoInteractions(
                auditEventService
        );
    }

    @Test
    void shouldDelegateSearchUsingDefaultContractValues() {

        PageResponse<AuditEventResponse> expected =
                new PageResponse<>(
                        List.of(),
                        0,
                        25,
                        0,
                        0,
                        false,
                        false
                );

        when(
                reviewService.searchAuditEvents(
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
        ).thenReturn(
                expected
        );

        ResponseEntity<PageResponse<AuditEventResponse>>
                response =
                controller.searchAuditEvents(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        25,
                        "eventTimestamp",
                        "DESC"
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).searchAuditEvents(
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
        );
    }

    @Test
    void shouldDelegateAllProvidedSearchCriteria() {

        PageResponse<AuditEventResponse> expected =
                new PageResponse<>(
                        List.of(),
                        2,
                        50,
                        0,
                        0,
                        false,
                        true
                );

        when(
                reviewService.searchAuditEvents(
                        USER_ID,
                        "2026-09-01T00:00:00",
                        "2026-09-16T23:59:59",
                        "CASE",
                        ENTITY_ID,
                        "REVIEW",
                        2,
                        50,
                        "eventTimestamp",
                        "ASC",
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<PageResponse<AuditEventResponse>>
                response =
                controller.searchAuditEvents(
                        USER_ID,
                        "2026-09-01T00:00:00",
                        "2026-09-16T23:59:59",
                        "CASE",
                        ENTITY_ID,
                        "REVIEW",
                        2,
                        50,
                        "eventTimestamp",
                        "ASC"
                );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                reviewService
        ).searchAuditEvents(
                USER_ID,
                "2026-09-01T00:00:00",
                "2026-09-16T23:59:59",
                "CASE",
                ENTITY_ID,
                "REVIEW",
                2,
                50,
                "eventTimestamp",
                "ASC",
                securityContext
        );
    }
    @Test
    void shouldDelegateLegacyIdReadUsingSecurityContext() {

        UUID auditEventId =
                UUID.randomUUID();

        AuditEventResponse expected =
                mock(
                        AuditEventResponse.class
                );

        when(
                reviewService.getAuditEventById(
                        auditEventId,
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<AuditEventResponse> response =
                controller.getAuditEventById(
                        auditEventId
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).getAuditEventById(
                auditEventId,
                securityContext
        );
    }

    @Test
    void shouldDelegateLegacyEventTypeReadUsingSecurityContext() {

        List<AuditEventResponse> expected =
                List.of();

        when(
                reviewService.getAuditEventsByEventType(
                        "CASE_REVIEW",
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<List<AuditEventResponse>> response =
                controller.getAuditEventsByEventType(
                        "CASE_REVIEW"
                );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).getAuditEventsByEventType(
                "CASE_REVIEW",
                securityContext
        );
    }

    @Test
    void shouldDelegateLegacyEntityReadUsingSecurityContext() {

        List<AuditEventResponse> expected =
                List.of();

        when(
                reviewService.getAuditEventsByEntity(
                        "CASE",
                        ENTITY_ID,
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<List<AuditEventResponse>> response =
                controller.getAuditEventsByEntity(
                        "CASE",
                        ENTITY_ID
                );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).getAuditEventsByEntity(
                "CASE",
                ENTITY_ID,
                securityContext
        );
    }

    @Test
    void shouldDelegateLegacyUserReadUsingSecurityContext() {

        List<AuditEventResponse> expected =
                List.of();

        when(
                reviewService.getAuditEventsByUserId(
                        USER_ID,
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<List<AuditEventResponse>> response =
                controller.getAuditEventsByUserId(
                        USER_ID
                );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).getAuditEventsByUserId(
                USER_ID,
                securityContext
        );
    }

    @Test
    void shouldDelegateLegacyOrganizationReadUsingSecurityContext() {

        UUID organizationId =
                UUID.randomUUID();

        List<AuditEventResponse> expected =
                List.of();

        when(
                reviewService.getAuditEventsByOrganizationId(
                        organizationId,
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<List<AuditEventResponse>> response =
                controller.getAuditEventsByOrganizationId(
                        organizationId
                );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).getAuditEventsByOrganizationId(
                organizationId,
                securityContext
        );
    }

    @Test
    void shouldDelegateLegacyCorrelationReadUsingSecurityContext() {

        UUID correlationId =
                UUID.randomUUID();

        List<AuditEventResponse> expected =
                List.of();

        when(
                reviewService.getAuditEventsByCorrelationId(
                        correlationId,
                        securityContext
                )
        ).thenReturn(
                expected
        );

        ResponseEntity<List<AuditEventResponse>> response =
                controller.getAuditEventsByCorrelationId(
                        correlationId
                );

        assertSame(
                expected,
                response.getBody()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();

        verify(
                reviewService
        ).getAuditEventsByCorrelationId(
                correlationId,
                securityContext
        );
    }
}