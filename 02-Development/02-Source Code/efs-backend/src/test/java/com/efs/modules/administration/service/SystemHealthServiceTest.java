package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.SystemHealthResponse;
import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemHealthServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111145"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222245"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333345"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444445"
            );

    @Mock
    private HealthEndpoint healthEndpoint;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private AuditEventServiceInterface
            auditEventService;

    @Mock
    private SecurityContext securityContext;

    private SystemHealthService service;

    @BeforeEach
    void setUp() {

        service =
                new SystemHealthService(
                        healthEndpoint,
                        userAccountLookupService,
                        auditEventService
                );
    }

    @Test
    void completeHealthShouldReturnOrderedComponentsAndAuditSuccess() {

        authorize();

        CompositeHealth health =
                org.mockito.Mockito.mock(
                        CompositeHealth.class
                );

        HealthComponent database =
                org.mockito.Mockito.mock(
                        HealthComponent.class
                );

        HealthComponent rabbit =
                org.mockito.Mockito.mock(
                        HealthComponent.class
                );

        when(
                healthEndpoint.health()
        ).thenReturn(
                health
        );

        when(
                health.getStatus()
        ).thenReturn(
                Status.UP
        );

        Map<String, HealthComponent> configuredComponents =
                new LinkedHashMap<>();

        configuredComponents.put(
                "rabbit",
                rabbit
        );

        configuredComponents.put(
                "database",
                database
        );

        when(
                health.getComponents()
        ).thenReturn(
                configuredComponents
        );

        when(
                database.getStatus()
        ).thenReturn(
                Status.UP
        );

        when(
                rabbit.getStatus()
        ).thenReturn(
                Status.UP
        );

        SystemHealthResponse response =
                service.getSystemHealth(
                        securityContext
                );

        assertEquals(
                "UP",
                response.getStatus()
        );

        assertEquals(
                "COMPLETE",
                response.getInformationStatus()
        );

        assertEquals(
                2,
                response.getComponents()
                        .size()
        );

        assertEquals(
                "rabbit",
                response.getComponents()
                        .get(0)
                        .getName()
        );

        assertEquals(
                "database",
                response.getComponents()
                        .get(1)
                        .getName()
        );

        ArgumentCaptor<AuditEventRequest> captor =
                auditCaptor();

        AuditEventRequest audit =
                captor.getValue();

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
                "SYSTEM_HEALTH_MONITORING",
                audit.getEventType()
        );

        assertEquals(
                "SYSTEM_HEALTH",
                audit.getEntityType()
        );

        assertEquals(
                "VIEW",
                audit.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                audit.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                "health.view",
                audit.getEventDetails()
                        .get("permissionCode")
        );

        assertEquals(
                "UP",
                audit.getEventDetails()
                        .get("status")
        );

        assertEquals(
                "COMPLETE",
                audit.getEventDetails()
                        .get("informationStatus")
        );

        assertEquals(
                2,
                audit.getEventDetails()
                        .get("componentCount")
        );
    }

    @Test
    void unavailableComponentShouldReturnRemainingInformationAsPartial() {

        authorize();

        CompositeHealth health =
                org.mockito.Mockito.mock(
                        CompositeHealth.class
                );

        HealthComponent database =
                org.mockito.Mockito.mock(
                        HealthComponent.class
                );

        Map<String, HealthComponent> components =
                new LinkedHashMap<>();

        components.put(
                "rabbit",
                null
        );

        components.put(
                "database",
                database
        );

        when(
                healthEndpoint.health()
        ).thenReturn(
                health
        );

        when(
                health.getStatus()
        ).thenReturn(
                Status.UP
        );

        when(
                health.getComponents()
        ).thenReturn(
                components
        );

        when(
                database.getStatus()
        ).thenReturn(
                Status.UP
        );

        SystemHealthResponse response =
                service.getSystemHealth(
                        securityContext
                );

        assertEquals(
                "UP",
                response.getStatus()
        );

        assertEquals(
                "PARTIAL",
                response.getInformationStatus()
        );

        assertEquals(
                1,
                response.getComponents()
                        .size()
        );

        assertEquals(
                "database",
                response.getComponents()
                        .get(0)
                        .getName()
        );

        ArgumentCaptor<AuditEventRequest> captor =
                auditCaptor();

        assertEquals(
                "SUCCESS",
                captor.getValue()
                        .getEventResult()
        );

        assertEquals(
                "PARTIAL",
                captor.getValue()
                        .getEventDetails()
                        .get("informationStatus")
        );
    }

    @Test
    void downStatusShouldRemainCompleteWhenInformationIsAvailable() {

        authorize();

        CompositeHealth health =
                org.mockito.Mockito.mock(
                        CompositeHealth.class
                );

        HealthComponent database =
                org.mockito.Mockito.mock(
                        HealthComponent.class
                );

        when(
                healthEndpoint.health()
        ).thenReturn(
                health
        );

        when(
                health.getStatus()
        ).thenReturn(
                Status.DOWN
        );

        when(
                health.getComponents()
        ).thenReturn(
                Map.of(
                        "database",
                        database
                )
        );

        when(
                database.getStatus()
        ).thenReturn(
                Status.DOWN
        );

        SystemHealthResponse response =
                service.getSystemHealth(
                        securityContext
                );

        assertEquals(
                "DOWN",
                response.getStatus()
        );

        assertEquals(
                "COMPLETE",
                response.getInformationStatus()
        );

        assertEquals(
                "DOWN",
                response.getComponents()
                        .get(0)
                        .getStatus()
        );

        auditCaptor();
    }

    @Test
    void unavailableHealthInformationShouldReturnUnavailableAndAuditFailure() {

        authorize();

        when(
                healthEndpoint.health()
        ).thenReturn(
                null
        );

        SystemHealthResponse response =
                service.getSystemHealth(
                        securityContext
                );

        assertEquals(
                "UNKNOWN",
                response.getStatus()
        );

        assertEquals(
                "UNAVAILABLE",
                response.getInformationStatus()
        );

        assertTrue(
                response.getComponents()
                        .isEmpty()
        );

        ArgumentCaptor<AuditEventRequest> captor =
                auditCaptor();

        assertEquals(
                "FAILURE",
                captor.getValue()
                        .getEventResult()
        );

        assertEquals(
                "HEALTH_INFORMATION_UNAVAILABLE",
                captor.getValue()
                        .getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void missingPermissionShouldRejectBeforeHealthLookupAndAuditRejection() {

        when(
                securityContext.hasPermission(
                        "health.view"
                )
        ).thenReturn(
                false
        );

        when(
                securityContext.getUserId()
        ).thenReturn(
                USER_ID
        );

        when(
                securityContext.getSessionId()
        ).thenReturn(
                SESSION_ID
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getSystemHealth(
                                securityContext
                        )
        );

        verifyNoInteractions(
                healthEndpoint,
                userAccountLookupService
        );

        ArgumentCaptor<AuditEventRequest> captor =
                auditCaptor();

        assertEquals(
                "REJECTED",
                captor.getValue()
                        .getEventResult()
        );

        assertEquals(
                "MISSING_PERMISSION",
                captor.getValue()
                        .getEventDetails()
                        .get("reason")
        );

        assertEquals(
                USER_ID,
                captor.getValue()
                        .getUserId()
        );

        assertEquals(
                SESSION_ID,
                captor.getValue()
                        .getSessionId()
        );
    }

    @Test
    void processingFailureShouldPropagateAndAuditFailure() {

        authorize();

        IllegalStateException exception =
                new IllegalStateException(
                        "health processing failed"
                );

        when(
                healthEndpoint.health()
        ).thenThrow(
                exception
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.getSystemHealth(
                                        securityContext
                                )
                );

        assertSame(
                exception,
                thrown
        );

        ArgumentCaptor<AuditEventRequest> captor =
                auditCaptor();

        assertEquals(
                "FAILURE",
                captor.getValue()
                        .getEventResult()
        );

        assertEquals(
                "SYSTEM_HEALTH_MONITORING_FAILED",
                captor.getValue()
                        .getEventDetails()
                        .get("reason")
        );

        assertEquals(
                IllegalStateException.class.getName(),
                captor.getValue()
                        .getEventDetails()
                        .get("errorType")
        );
    }

    private void authorize() {

        when(
                securityContext.hasPermission(
                        "health.view"
                )
        ).thenReturn(
                true
        );

        when(
                securityContext.getUserId()
        ).thenReturn(
                USER_ID
        );

        when(
                securityContext.getSessionId()
        ).thenReturn(
                SESSION_ID
        );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "system.health@example.com"
                )
        );
    }

    private ArgumentCaptor<AuditEventRequest>
    auditCaptor() {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        return captor;
    }
}