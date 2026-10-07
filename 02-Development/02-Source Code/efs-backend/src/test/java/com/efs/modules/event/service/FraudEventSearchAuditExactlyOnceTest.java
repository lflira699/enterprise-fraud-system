package com.efs.modules.event.service;

import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.event.repository.FraudEventRepository;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudEventSearchAuditExactlyOnceTest {

    @Mock
    private FraudEventRepository fraudEventRepository;

    @Mock
    private TransactionServiceInterface transactionService;

    @Mock
    private UserAccountLookupServiceInterface userAccountLookupService;

    @Mock
    private AuditEventServiceInterface auditEventService;

    @Mock
    private DomainEventOutboxService domainEventOutboxService;

    @Mock
    private SecurityContext securityContext;

    private FraudEventService service;

    @BeforeEach
    void setUp() {
        service = new FraudEventService(
                fraudEventRepository,
                transactionService,
                userAccountLookupService,
                auditEventService,
                domainEventOutboxService
        );
    }

    @Test
    void shouldAuditMissingSearchPermissionExactlyOnce() {

        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        when(
                securityContext.hasPermission(
                        "event.view"
                )
        ).thenReturn(false);

        when(
                securityContext.getUserId()
        ).thenReturn(userId);

        when(
                securityContext.getTenantId()
        ).thenReturn(tenantId);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.searchEvents(
                                null,
                                null,
                                null,
                                null,
                                correlationId,
                                null,
                                null,
                                null,
                                null,
                                0,
                                25,
                                "occurredAt",
                                "DESC",
                                securityContext
                        )
        );

        ArgumentCaptor<AuditEventRequest> auditCaptor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService,
                times(1)
        ).createAuditEvent(
                auditCaptor.capture()
        );

        verify(
                auditEventService,
                never()
        ).createAuditEventRequiresNew(
                any(AuditEventRequest.class)
        );

        AuditEventRequest auditRequest =
                auditCaptor.getValue();

        assertEquals(
                "FRAUD_EVENT_SEARCH",
                auditRequest.getEventType()
        );

        assertEquals(
                "FRAUD_EVENT",
                auditRequest.getEntityType()
        );

        assertEquals(
                "SEARCH",
                auditRequest.getAction()
        );

        assertEquals(
                "EVENT",
                auditRequest.getSourceComponent()
        );

        assertEquals(
                "REJECTED",
                auditRequest.getEventResult()
        );

        assertEquals(
                correlationId,
                auditRequest.getCorrelationId()
        );

        assertEquals(
                tenantId,
                auditRequest.getTenantId()
        );

        assertEquals(
                userId,
                auditRequest.getUserId()
        );

        Map<String, Object> details =
                auditRequest.getEventDetails();

        assertEquals(
                "event.view",
                details.get("permissionCode")
        );

        assertEquals(
                "MISSING_PERMISSION",
                details.get("reason")
        );

        assertEquals(
                correlationId,
                details.get("correlationId")
        );

        assertEquals(
                0,
                details.get("page")
        );

        assertEquals(
                25,
                details.get("size")
        );

        assertEquals(
                "occurredAt",
                details.get("sort")
        );

        assertEquals(
                "DESC",
                details.get("direction")
        );

        assertTrue(
                details.containsKey("transactionId")
        );

        assertTrue(
                details.containsKey("eventType")
        );

        assertTrue(
                details.containsKey("sourceType")
        );

        assertTrue(
                details.containsKey("sourceReference")
        );

        assertTrue(
                details.containsKey("occurredFrom")
        );

        assertTrue(
                details.containsKey("occurredTo")
        );

        assertTrue(
                details.containsKey("receivedFrom")
        );

        assertTrue(
                details.containsKey("receivedTo")
        );

        verifyNoInteractions(
                fraudEventRepository,
                transactionService,
                userAccountLookupService,
                domainEventOutboxService
        );
    }

    @Test
    void internalCanonicalLookupDoesNotUseReviewAuthorizationOrAudit() {

        UUID fraudEventId =
                UUID.randomUUID();

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        com.efs.modules.event.entity.FraudEvent fraudEvent =
                org.mockito.Mockito.mock(
                        com.efs.modules.event.entity.FraudEvent.class
                );

        when(
                fraudEvent.getFraudEventId()
        ).thenReturn(
                fraudEventId
        );

        when(
                fraudEvent.getOrganizationId()
        ).thenReturn(
                organizationId
        );

        when(
                fraudEvent.getTenantId()
        ).thenReturn(
                tenantId
        );

        when(
                fraudEvent.getCorrelationId()
        ).thenReturn(
                correlationId
        );

        when(
                fraudEventRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantId(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                java.util.Optional.of(
                        fraudEvent
                )
        );

        com.efs.modules.event.dto.FraudEventReference result =
                service.lookupCanonicalEvent(
                        fraudEventId,
                        organizationId,
                        tenantId
                );

        assertEquals(
                fraudEventId,
                result.fraudEventId()
        );

        assertEquals(
                organizationId,
                result.organizationId()
        );

        assertEquals(
                tenantId,
                result.tenantId()
        );

        assertEquals(
                correlationId,
                result.correlationId()
        );

        verifyNoInteractions(
                userAccountLookupService
        );

        verifyNoInteractions(
                auditEventService
        );
    }
}