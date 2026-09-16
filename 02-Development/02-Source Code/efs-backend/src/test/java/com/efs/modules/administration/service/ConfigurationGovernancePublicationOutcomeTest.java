package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.ConfigurationChangeRequest;
import com.efs.modules.administration.repository.ConfigurationChangeItemRepository;
import com.efs.modules.administration.repository.ConfigurationChangeRequestRepository;
import com.efs.modules.administration.repository.SystemConfigurationRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditConfigurationChangeServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationGovernancePublicationOutcomeTest {

    private static final UUID REQUEST_ID =
            UUID.fromString(
                    "44444000-0000-0000-0000-000000000001"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "44444000-0000-0000-0000-000000000002"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "44444000-0000-0000-0000-000000000003"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "44444000-0000-0000-0000-000000000004"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "44444000-0000-0000-0000-000000000005"
            );

    @Mock
    private ConfigurationChangeRequestRepository
            changeRequestRepository;

    @Mock
    private ConfigurationChangeItemRepository
            changeItemRepository;

    @Mock
    private SystemConfigurationServiceInterface
            systemConfigurationService;

    @Mock
    private SystemConfigurationRepository
            systemConfigurationRepository;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    @Mock
    private AuditEventServiceInterface
            auditEventService;

    @Mock
    private AuditConfigurationChangeServiceInterface
            auditConfigurationChangeService;

    @Mock
    private SecurityContext
            securityContext;

    private ConfigurationGovernanceService service;

    @BeforeEach
    void setUp() {

        service =
                new ConfigurationGovernanceService(
                        changeRequestRepository,
                        changeItemRepository,
                        new ConfigurationDefinitionCatalog(),
                        systemConfigurationService,
                        systemConfigurationRepository,
                        userAccountLookupService,
                        tenantOrganizationLookupService,
                        auditEventService,
                        auditConfigurationChangeService
                );

        when(
                securityContext.hasPermission(
                        "configuration.manage"
                )
        ).thenReturn(true);

        when(
                securityContext.getUserId()
        ).thenReturn(USER_ID);

        when(
                securityContext.getSessionId()
        ).thenReturn(SESSION_ID);

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        null,
                        "uc044-outcome@example.com"
                )
        );
    }

    @AfterEach
    void tearDown() {

        if (
                TransactionSynchronizationManager
                        .isSynchronizationActive()
        ) {
            TransactionSynchronizationManager
                    .clearSynchronization();
        }
    }

    @Test
    void shouldAuditInvalidTenantPublicationAsRejectedWithoutMarkingRequestFailed() {

        ConfigurationChangeRequest request =
                approvedRequest(
                        TENANT_ID
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                TENANT_ID
                        )
        ).thenReturn(
                ORGANIZATION_ID
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                REQUEST_ID
                        )
        ).thenReturn(
                List.of()
        );

        TransactionSynchronizationManager
                .initSynchronization();

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.publishChangeRequest(
                                REQUEST_ID,
                                securityContext
                        )
        );

        verify(
                tenantOrganizationLookupService
        ).lockTenant(
                TENANT_ID
        );

        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager
                        .getSynchronizations();

        assertEquals(
                1,
                synchronizations.size()
        );

        synchronizations
                .get(0)
                .afterCompletion(
                        TransactionSynchronization
                                .STATUS_ROLLED_BACK
                );

        verify(
                changeRequestRepository,
                never()
        ).markApprovedRequestFailedRequiresNew(
                REQUEST_ID,
                ORGANIZATION_ID,
                "CONFIGURATION_PUBLICATION_FAILED [RequestValidationException]"
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                "SYSTEM_CONFIGURATION_CHANGE",
                audit.getEventType()
        );

        assertEquals(
                "CONFIGURATION_CHANGE_REQUEST",
                audit.getEntityType()
        );

        assertEquals(
                REQUEST_ID,
                audit.getEntityId()
        );

        assertEquals(
                "PUBLISH",
                audit.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                audit.getSourceComponent()
        );

        assertEquals(
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                TENANT_ID,
                audit.getTenantId()
        );

        assertEquals(
                "APPROVED",
                audit.getEventDetails()
                        .get("status")
        );
    }

    @Test
    void shouldMarkProcessingErrorFailedAndAuditFailureAfterOrganizationRollback() {

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        doThrow(
                new IllegalStateException(
                        "Simulated publication processing error"
                )
        ).when(
                tenantOrganizationLookupService
        ).lockOrganization(
                ORGANIZATION_ID
        );

        when(
                changeRequestRepository
                        .markApprovedRequestFailedRequiresNew(
                                REQUEST_ID,
                                ORGANIZATION_ID,
                                "CONFIGURATION_PUBLICATION_FAILED [IllegalStateException]"
                        )
        ).thenReturn(
                1
        );

        TransactionSynchronizationManager
                .initSynchronization();

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.publishChangeRequest(
                                REQUEST_ID,
                                securityContext
                        )
        );

        verify(
                tenantOrganizationLookupService
        ).lockOrganization(
                ORGANIZATION_ID
        );

        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager
                        .getSynchronizations();

        assertEquals(
                1,
                synchronizations.size()
        );

        synchronizations
                .get(0)
                .afterCompletion(
                        TransactionSynchronization
                                .STATUS_ROLLED_BACK
                );

        verify(
                changeRequestRepository
        ).markApprovedRequestFailedRequiresNew(
                REQUEST_ID,
                ORGANIZATION_ID,
                "CONFIGURATION_PUBLICATION_FAILED [IllegalStateException]"
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                "FAILURE",
                audit.getEventResult()
        );

        assertEquals(
                REQUEST_ID,
                audit.getEntityId()
        );

        assertEquals(
                ORGANIZATION_ID,
                audit.getOrganizationId()
        );

        assertNull(
                audit.getTenantId()
        );

        assertEquals(
                "FAILED",
                audit.getEventDetails()
                        .get("status")
        );

        assertEquals(
                "IllegalStateException",
                audit.getEventDetails()
                        .get("errorType")
        );
    }

    @Test
    void shouldNotFinalizePublicationOutcomeWhenTransactionCommits() {

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                REQUEST_ID
                        )
        ).thenReturn(
                List.of()
        );

        TransactionSynchronizationManager
                .initSynchronization();

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.publishChangeRequest(
                                REQUEST_ID,
                                securityContext
                        )
        );

        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager
                        .getSynchronizations();

        assertEquals(
                1,
                synchronizations.size()
        );

        synchronizations
                .get(0)
                .afterCompletion(
                        TransactionSynchronization
                                .STATUS_COMMITTED
                );

        verify(
                changeRequestRepository,
                never()
        ).markApprovedRequestFailedRequiresNew(
                REQUEST_ID,
                ORGANIZATION_ID,
                "CONFIGURATION_PUBLICATION_FAILED [RequestValidationException]"
        );

        verify(
                auditEventService,
                never()
        ).createAuditEventRequiresNew(
                org.mockito.ArgumentMatchers.any(
                        AuditEventRequest.class
                )
        );
    }

    private ConfigurationChangeRequest approvedRequest(
            UUID tenantId) {

        ConfigurationChangeRequest request =
                new ConfigurationChangeRequest();

        request.setChangeRequestId(
                REQUEST_ID
        );

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setTenantId(
                tenantId
        );

        request.setRequestedBy(
                USER_ID
        );

        request.setStatus(
                "APPROVED"
        );

        return request;
    }
}