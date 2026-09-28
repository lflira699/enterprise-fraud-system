package com.efs.modules.event.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.event.dto.FraudEventRequest;
import com.efs.modules.event.entity.FraudEvent;
import com.efs.modules.event.repository.FraudEventRepository;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudEventRegisteredOutboxTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "45000000-0000-0000-0000-000000000001"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "45000000-0000-0000-0000-000000000002"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "45000000-0000-0000-0000-000000000003"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "45000000-0000-0000-0000-000000000004"
            );

    private static final UUID FRAUD_EVENT_ID =
            UUID.fromString(
                    "45000000-0000-0000-0000-000000000005"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "45000000-0000-0000-0000-000000000006"
            );

    @Mock
    private FraudEventRepository fraudEventRepository;

    @Mock
    private TransactionServiceInterface transactionService;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private AuditEventServiceInterface auditEventService;

    @Mock
    private DomainEventOutboxService domainEventOutboxService;

    private FraudEventService service;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {

        service =
                new FraudEventService(
                        fraudEventRepository,
                        transactionService,
                        userAccountLookupService,
                        auditEventService,
                        domainEventOutboxService
                );

        securityContext =
                new SecurityContext(
                        USER_ID,
                        TENANT_ID,
                        SESSION_ID,
                        Set.of(),
                        Set.of("event.create"),
                        Set.of()
                );
    }

    @Test
    void shouldPublishApprovedFraudEventRegisteredEnvelope()
            throws Exception {

        UserAccountReference actor =
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "comp004@example.test"
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(USER_ID)
        ).thenReturn(
                actor
        );

        when(
                fraudEventRepository
                        .existsByOrganizationIdAndTenantIdAndIdempotencyKey(
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "COMP004-IDEMPOTENCY-1"
                        )
        ).thenReturn(
                false
        );

        when(
                fraudEventRepository.saveAndFlush(
                        any(FraudEvent.class)
                )
        ).thenAnswer(
                invocation -> {

                    FraudEvent event =
                            invocation.getArgument(0);

                    Field idField =
                            FraudEvent.class
                                    .getDeclaredField(
                                            "fraudEventId"
                                    );

                    idField.setAccessible(true);
                    idField.set(
                            event,
                            FRAUD_EVENT_ID
                    );

                    return event;
                }
        );

        FraudEventRequest request =
                new FraudEventRequest();

        request.setEventType(
                "LOGIN"
        );

        request.setSourceType(
                "EXTERNAL"
        );

        request.setSourceReference(
                "COMP004-SOURCE-1"
        );

        request.setIdempotencyKey(
                "COMP004-IDEMPOTENCY-1"
        );

        request.setCorrelationId(
                CORRELATION_ID
        );

        request.setPayload(
                new ObjectMapper()
                        .createObjectNode()
                        .put(
                                "signal",
                                "test"
                        )
        );

        request.setOccurredAt(
                LocalDateTime.now()
                        .minusSeconds(1)
        );

        service.registerEvent(
                request,
                securityContext
        );

        ArgumentCaptor<DomainEventEnvelope> envelopeCaptor =
                ArgumentCaptor.forClass(
                        DomainEventEnvelope.class
                );

        verify(
                domainEventOutboxService
        ).persist(
                eq("FraudEvent"),
                eq(FRAUD_EVENT_ID),
                envelopeCaptor.capture()
        );

        DomainEventEnvelope envelope =
                envelopeCaptor.getValue();

        assertEquals(
                "FraudEventRegistered",
                envelope.getEventType()
        );

        assertEquals(
                "1.0",
                envelope.getSchemaVersion()
        );

        assertEquals(
                CORRELATION_ID,
                envelope.getCorrelationId()
        );

        assertEquals(
                TENANT_ID,
                envelope.getTenantId()
        );

        assertNotNull(
                envelope.getOccurredAt()
        );

        Map<String, Object> payload =
                envelope.getPayload();

        assertNotNull(payload);

        assertEquals(
                1,
                payload.size()
        );

        assertEquals(
                FRAUD_EVENT_ID.toString(),
                payload.get("fraudEventId")
        );

        assertFalse(
                payload.containsKey("organizationId")
        );

        assertFalse(
                payload.containsKey("tenantId")
        );

        assertFalse(
                payload.containsKey("transactionId")
        );

        assertFalse(
                payload.containsKey("eventType")
        );

        assertFalse(
                payload.containsKey("sourceType")
        );

        assertFalse(
                payload.containsKey("sourceReference")
        );

        assertFalse(
                payload.containsKey("idempotencyKey")
        );

        assertFalse(
                payload.containsKey("correlationId")
        );

        assertFalse(
                payload.containsKey("normalizedPayload")
        );

        assertFalse(
                payload.containsKey("occurredAt")
        );

        assertFalse(
                payload.containsKey("receivedAt")
        );

        assertFalse(
                payload.containsKey("createdAt")
        );

        verify(
                fraudEventRepository
        ).saveAndFlush(
                any(FraudEvent.class)
        );
    }
}