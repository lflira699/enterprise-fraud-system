package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.ConfigurationChangeItemRequest;
import com.efs.modules.administration.dto.ConfigurationChangeRequestCreateRequest;
import com.efs.modules.administration.dto.ConfigurationChangeRequestResponse;
import com.efs.modules.administration.dto.ConfigurationEffectiveResponse;
import com.efs.modules.administration.dto.ConfigurationVersionResponse;
import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.ConfigurationChangeItem;
import com.efs.modules.administration.entity.ConfigurationChangeRequest;
import com.efs.modules.administration.entity.SystemConfiguration;
import com.efs.modules.administration.repository.ConfigurationChangeItemRepository;
import com.efs.modules.administration.repository.ConfigurationChangeRequestRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationGovernanceServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000001"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000002"
            );

    private static final UUID OTHER_ORGANIZATION_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000003"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000004"
            );

    private static final UUID OTHER_TENANT_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000005"
            );

    private static final UUID CHANGE_REQUEST_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000006"
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
    private com.efs.modules.administration.repository.SystemConfigurationRepository
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
    private com.efs.modules.audit.service.AuditConfigurationChangeServiceInterface
            auditConfigurationChangeService;
    @Mock
    private SecurityContext securityContext;

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
    }

    @Test
    void shouldCreateOrganizationScopedMultiItemRequest() {

        authorize(
                "configuration.manage",
                null
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                null
                        )
        ).thenReturn(
                Optional.of("100")
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                                ORGANIZATION_ID,
                                null
                        )
        ).thenReturn(
                Optional.of("100")
        );

        stubRequestSave();
        stubItemSave();

        ConfigurationChangeRequestResponse response =
                service.createChangeRequest(
                        createRequest(
                                null,
                                List.of(
                                        item(
                                                "EFS.REPORT.MAX_RECORDS",
                                                "250"
                                        ),
                                        item(
                                                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                                                "150"
                                        )
                                )
                        ),
                        securityContext
                );

        assertEquals(
                CHANGE_REQUEST_ID,
                response.getChangeRequestId()
        );

        assertEquals(
                ORGANIZATION_ID,
                response.getOrganizationId()
        );

        assertNull(
                response.getTenantId()
        );

        assertEquals(
                "PENDING_APPROVAL",
                response.getStatus()
        );

        assertEquals(
                2,
                response.getItems().size()
        );

        assertEquals(
                "100",
                response.getItems()
                        .get(0)
                        .getPreviousValue()
        );

        verifyNoInteractions(
                tenantOrganizationLookupService
        );
    }

    @Test
    void shouldCreateTenantScopedRequestAfterAuthoritativeTenantValidation() {

        authorize(
                "configuration.manage",
                null
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
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
        ).thenReturn(
                Optional.of("100")
        );

        stubRequestSave();
        stubItemSave();

        ConfigurationChangeRequestResponse response =
                service.createChangeRequest(
                        createRequest(
                                TENANT_ID,
                                List.of(
                                        item(
                                                "EFS.REPORT.MAX_RECORDS",
                                                "200"
                                        )
                                )
                        ),
                        securityContext
                );

        assertEquals(
                TENANT_ID,
                response.getTenantId()
        );

        verify(
                tenantOrganizationLookupService
        ).getOrganizationIdByTenantId(
                TENANT_ID
        );
    }

    @Test
    void shouldAllowTenantScopedCallerOnlyForOwnTenant() {

        authorize(
                "configuration.manage",
                TENANT_ID
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
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
        ).thenReturn(
                Optional.of("100")
        );

        stubRequestSave();
        stubItemSave();

        ConfigurationChangeRequestResponse response =
                service.createChangeRequest(
                        createRequest(
                                TENANT_ID,
                                List.of(
                                        item(
                                                "EFS.REPORT.MAX_RECORDS",
                                                "175"
                                        )
                                )
                        ),
                        securityContext
                );

        assertEquals(
                TENANT_ID,
                response.getTenantId()
        );
    }

    @Test
    void shouldRejectOrganizationScopeForTenantScopedCaller() {

        authorize(
                "configuration.manage",
                TENANT_ID
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.createChangeRequest(
                                createRequest(
                                        null,
                                        List.of(
                                                item(
                                                        "EFS.REPORT.MAX_RECORDS",
                                                        "200"
                                                )
                                        )
                                ),
                                securityContext
                        )
        );

        verify(
                changeRequestRepository,
                never()
        ).saveAndFlush(
                any(ConfigurationChangeRequest.class)
        );
    }

    @Test
    void shouldRejectTenantOutsideAuthorizedOrganization() {

        authorize(
                "configuration.manage",
                null
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                OTHER_TENANT_ID
                        )
        ).thenReturn(
                OTHER_ORGANIZATION_ID
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.createChangeRequest(
                                createRequest(
                                        OTHER_TENANT_ID,
                                        List.of(
                                                item(
                                                        "EFS.REPORT.MAX_RECORDS",
                                                        "200"
                                                )
                                        )
                                ),
                                securityContext
                        )
        );

        verify(
                changeRequestRepository,
                never()
        ).saveAndFlush(
                any(ConfigurationChangeRequest.class)
        );
    }

    @Test
    void shouldRejectUnknownConfigurationKey() {

        authorize(
                "configuration.manage",
                null
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.createChangeRequest(
                                createRequest(
                                        null,
                                        List.of(
                                                item(
                                                        "EFS.RISK.ACTIVE_MODEL",
                                                        "1.1"
                                                )
                                        )
                                ),
                                securityContext
                        )
        );

        verifyNoInteractions(
                systemConfigurationService
        );
    }

    @Test
    void shouldRejectInvalidConfigurationValue() {

        authorize(
                "configuration.manage",
                null
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.createChangeRequest(
                                createRequest(
                                        null,
                                        List.of(
                                                item(
                                                        "EFS.REPORT.MAX_RECORDS",
                                                        "0"
                                                )
                                        )
                                ),
                                securityContext
                        )
        );

        verifyNoInteractions(
                systemConfigurationService
        );
    }

    @Test
    void shouldRejectDuplicateConfigurationKeys() {

        authorize(
                "configuration.manage",
                null
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                null
                        )
        ).thenReturn(
                Optional.of("100")
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.createChangeRequest(
                                createRequest(
                                        null,
                                        List.of(
                                                item(
                                                        "EFS.REPORT.MAX_RECORDS",
                                                        "200"
                                                ),
                                                item(
                                                        "EFS.REPORT.MAX_RECORDS",
                                                        "300"
                                                )
                                        )
                                ),
                                securityContext
                        )
        );

        verify(
                changeRequestRepository,
                never()
        ).saveAndFlush(
                any(ConfigurationChangeRequest.class)
        );
    }

    @Test
    void shouldRejectCreateBeforeCallerLookupWhenManagePermissionMissing() {

        when(
                securityContext.hasPermission(
                        "configuration.manage"
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.createChangeRequest(
                                createRequest(
                                        null,
                                        List.of(
                                                item(
                                                        "EFS.REPORT.MAX_RECORDS",
                                                        "200"
                                                )
                                        )
                                ),
                                securityContext
                        )
        );

        verifyNoInteractions(
                userAccountLookupService,
                tenantOrganizationLookupService,
                systemConfigurationService,
                changeRequestRepository,
                changeItemRepository
        );
    }

    @Test
    void shouldApprovePendingRequest() {

        authorize(
                "configuration.approve",
                null
        );

        ConfigurationChangeRequest request =
                existingRequest(
                        null,
                        "PENDING_APPROVAL"
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                CHANGE_REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                changeRequestRepository
                        .saveAndFlush(
                                request
                        )
        ).thenReturn(
                request
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                CHANGE_REQUEST_ID
                        )
        ).thenReturn(
                List.of()
        );

        ConfigurationChangeRequestResponse response =
                service.approveChangeRequest(
                        CHANGE_REQUEST_ID,
                        securityContext
                );

        assertEquals(
                "APPROVED",
                response.getStatus()
        );

        assertEquals(
                USER_ID,
                response.getApprovedBy()
        );

        assertTrue(
                response.getApprovedAt() != null
        );
    }

    @Test
    void shouldApproveTenantScopedRequestUsingExactTenantScope() {

        authorize(
                "configuration.approve",
                TENANT_ID
        );

        ConfigurationChangeRequest request =
                existingRequest(
                        TENANT_ID,
                        "PENDING_APPROVAL"
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationIdAndTenantId(
                                CHANGE_REQUEST_ID,
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                changeRequestRepository
                        .saveAndFlush(
                                request
                        )
        ).thenReturn(
                request
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                CHANGE_REQUEST_ID
                        )
        ).thenReturn(
                List.of()
        );

        service.approveChangeRequest(
                CHANGE_REQUEST_ID,
                securityContext
        );

        verify(
                changeRequestRepository
        ).findByChangeRequestIdAndOrganizationIdAndTenantId(
                CHANGE_REQUEST_ID,
                ORGANIZATION_ID,
                TENANT_ID
        );

        verify(
                changeRequestRepository,
                never()
        ).findByChangeRequestIdAndOrganizationId(
                any(UUID.class),
                any(UUID.class)
        );
    }

    @Test
    void shouldRejectApprovalWhenRequestIsNotPending() {

        authorize(
                "configuration.approve",
                null
        );

        ConfigurationChangeRequest request =
                existingRequest(
                        null,
                        "APPROVED"
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                CHANGE_REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.approveChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verify(
                changeRequestRepository,
                never()
        ).saveAndFlush(
                any(ConfigurationChangeRequest.class)
        );
    }

    @Test
    void shouldRejectPendingRequestAndCreateRejectedAudit() {

        authorize(
                "configuration.approve",
                null
        );

        ConfigurationChangeRequest request =
                existingRequest(
                        null,
                        "PENDING_APPROVAL"
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                CHANGE_REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                changeRequestRepository
                        .saveAndFlush(
                                request
                        )
        ).thenReturn(
                request
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                CHANGE_REQUEST_ID
                        )
        ).thenReturn(
                List.of()
        );

        ConfigurationChangeRequestResponse response =
                service.rejectChangeRequest(
                        CHANGE_REQUEST_ID,
                        "Risk validation failed",
                        securityContext
                );

        assertEquals(
                "REJECTED",
                response.getStatus()
        );

        assertEquals(
                USER_ID,
                response.getRejectedBy()
        );

        assertEquals(
                "Risk validation failed",
                response.getRejectionReason()
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

        AuditEventRequest auditRequest =
                captor.getValue();

        assertEquals(
                "SYSTEM_CONFIGURATION_CHANGE",
                auditRequest.getEventType()
        );

        assertEquals(
                "CONFIGURATION_CHANGE_REQUEST",
                auditRequest.getEntityType()
        );

        assertEquals(
                CHANGE_REQUEST_ID,
                auditRequest.getEntityId()
        );

        assertEquals(
                "REJECT",
                auditRequest.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                auditRequest.getSourceComponent()
        );

        assertEquals(
                "REJECTED",
                auditRequest.getEventResult()
        );
    }

    @Test
    void shouldRejectBlankRejectionReason() {

        authorize(
                "configuration.approve",
                null
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.rejectChangeRequest(
                                CHANGE_REQUEST_ID,
                                "   ",
                                securityContext
                        )
        );

        verifyNoInteractions(
                changeRequestRepository,
                changeItemRepository,
                auditEventService
        );
    }

    @Test
    void shouldRejectApproveBeforeCallerLookupWhenPermissionMissing() {

        when(
                securityContext.hasPermission(
                        "configuration.approve"
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.approveChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verifyNoInteractions(
                userAccountLookupService,
                changeRequestRepository,
                changeItemRepository,
                auditEventService
        );
    }

    @Test
    void shouldGetRequestWithViewPermissionAndOrganizationScope() {

        authorize(
                "configuration.view",
                null
        );

        ConfigurationChangeRequest request =
                existingRequest(
                        TENANT_ID,
                        "APPROVED"
                );

        when(
                changeRequestRepository
                        .findByChangeRequestIdAndOrganizationId(
                                CHANGE_REQUEST_ID,
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                CHANGE_REQUEST_ID
                        )
        ).thenReturn(
                List.of()
        );

        ConfigurationChangeRequestResponse response =
                service.getChangeRequest(
                        CHANGE_REQUEST_ID,
                        securityContext
                );

        assertEquals(
                CHANGE_REQUEST_ID,
                response.getChangeRequestId()
        );

        assertEquals(
                TENANT_ID,
                response.getTenantId()
        );
    }

    @Test
    void shouldRejectViewBeforeCallerLookupWhenPermissionMissing() {

        when(
                securityContext.hasPermission(
                        "configuration.view"
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verifyNoInteractions(
                userAccountLookupService,
                changeRequestRepository,
                changeItemRepository
        );
    }

    @Test
    void shouldReturnEffectiveOrganizationConfiguration() {

        authorize(
                "configuration.view",
                null
        );

        SystemConfiguration configuration =
                new SystemConfiguration();

        configuration.setConfigurationKey(
                "EFS.REPORT.MAX_RECORDS"
        );

        configuration.setConfigurationValue(
                "500"
        );

        configuration.setConfigurationType(
                "INTEGER"
        );

        configuration.setOrganizationId(
                null
        );

        configuration.setTenantId(
                null
        );

        configuration.setEncrypted(
                false
        );

        when(
                systemConfigurationService
                        .resolveConfiguration(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                null
                        )
        ).thenReturn(
                Optional.of(
                        configuration
                )
        );

        ConfigurationEffectiveResponse response =
                service.getEffectiveConfiguration(
                        "EFS.REPORT.MAX_RECORDS",
                        null,
                        securityContext
                );

        assertEquals(
                "EFS.REPORT.MAX_RECORDS",
                response.getConfigurationKey()
        );

        assertEquals(
                "500",
                response.getConfigurationValue()
        );

        assertEquals(
                "INTEGER",
                response.getConfigurationType()
        );

        assertEquals(
                "GLOBAL",
                response.getEffectiveScope()
        );

        assertEquals(
                Boolean.TRUE,
                response.getCritical()
        );
    }

    @Test
    void shouldListEffectiveOrganizationConfigurations() {

        authorize(
                "configuration.view",
                null
        );

        SystemConfiguration configuration =
                new SystemConfiguration();

        configuration.setConfigurationKey(
                "EFS.REPORT.MAX_RECORDS"
        );

        configuration.setConfigurationValue(
                "750"
        );

        configuration.setConfigurationType(
                "INTEGER"
        );

        configuration.setEncrypted(
                false
        );

        when(
                systemConfigurationService
                        .resolveConfiguration(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                null
                        )
        ).thenReturn(
                Optional.of(
                        configuration
                )
        );

        List<ConfigurationEffectiveResponse> response =
                service.getEffectiveConfigurations(
                        null,
                        securityContext
                );

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                "EFS.REPORT.MAX_RECORDS",
                response.get(0)
                        .getConfigurationKey()
        );

        assertEquals(
                "750",
                response.get(0)
                        .getConfigurationValue()
        );

        assertEquals(
                "GLOBAL",
                response.get(0)
                        .getEffectiveScope()
        );
    }

    @Test
    void shouldReturnAppliedOrganizationVersionHistory() {

        authorize(
                "configuration.view",
                null
        );

        ConfigurationChangeRequest request =
                existingRequest(
                        null,
                        "APPLIED"
                );

        request.setAppliedBy(
                USER_ID
        );

        ConfigurationChangeItem item =
                new ConfigurationChangeItem();

        item.setChangeRequestId(
                CHANGE_REQUEST_ID
        );

        item.setConfigurationKey(
                "EFS.REPORT.MAX_RECORDS"
        );

        item.setProposedValue(
                "900"
        );

        item.setConfigurationType(
                "INTEGER"
        );

        item.setEncrypted(
                false
        );

        item.setVersionNumber(
                1
        );

        when(
                changeRequestRepository
                        .findByOrganizationIdAndStatusOrderByRequestedAtDesc(
                                ORGANIZATION_ID,
                                "APPLIED"
                        )
        ).thenReturn(
                List.of(
                        request
                )
        );

        when(
                changeItemRepository
                        .findByChangeRequestIdOrderByCreatedAtAsc(
                                CHANGE_REQUEST_ID
                        )
        ).thenReturn(
                List.of(
                        item
                )
        );

        List<ConfigurationVersionResponse> response =
                service.getAppliedVersions(
                        "EFS.REPORT.MAX_RECORDS",
                        null,
                        securityContext
                );

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                CHANGE_REQUEST_ID,
                response.get(0)
                        .getChangeRequestId()
        );

        assertEquals(
                "EFS.REPORT.MAX_RECORDS",
                response.get(0)
                        .getConfigurationKey()
        );

        assertEquals(
                "900",
                response.get(0)
                        .getConfigurationValue()
        );

        assertEquals(
                Integer.valueOf(1),
                response.get(0)
                        .getVersionNumber()
        );

        assertEquals(
                ORGANIZATION_ID,
                response.get(0)
                        .getOrganizationId()
        );

        assertEquals(
                USER_ID,
                response.get(0)
                        .getAppliedBy()
        );
    }

    @Test
    void shouldRejectEffectiveReadBeforeCallerLookupWhenViewPermissionMissing() {

        when(
                securityContext.hasPermission(
                        "configuration.view"
                )
        ).thenReturn(
                false
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getEffectiveConfigurations(
                                null,
                                securityContext
                        )
        );

        verifyNoInteractions(
                userAccountLookupService,
                systemConfigurationService
        );
    }
    private void authorize(
            String permission,
            UUID callerTenantId) {

        when(
                securityContext.hasPermission(
                        permission
                )
        ).thenReturn(true);

        when(
                securityContext.getUserId()
        ).thenReturn(
                USER_ID
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
                        callerTenantId,
                        "uc044@example.com"
                )
        );
    }

    private void stubRequestSave() {

        when(
                changeRequestRepository
                        .saveAndFlush(
                                any(ConfigurationChangeRequest.class)
                        )
        ).thenAnswer(
                invocation -> {

                    ConfigurationChangeRequest request =
                            invocation.getArgument(0);

                    request.setChangeRequestId(
                            CHANGE_REQUEST_ID
                    );

                    return request;
                }
        );
    }

    private void stubItemSave() {

        when(
                changeItemRepository
                        .saveAllAndFlush(
                                anyList()
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );
    }

    private ConfigurationChangeRequestCreateRequest
    createRequest(
            UUID tenantId,
            List<ConfigurationChangeItemRequest> items) {

        ConfigurationChangeRequestCreateRequest request =
                new ConfigurationChangeRequestCreateRequest();

        request.setTenantId(
                tenantId
        );

        request.setJustification(
                "Controlled configuration change"
        );

        request.setAffectedEnvironment(
                "Development"
        );

        request.setRiskAssessment(
                "Validated operational risk"
        );

        request.setExpectedResult(
                "Configuration override applied after approval"
        );

        request.setRollbackPlan(
                "Restore previous approved value"
        );

        request.setItems(
                items
        );

        return request;
    }

    private ConfigurationChangeItemRequest item(
            String key,
            String value) {

        return new ConfigurationChangeItemRequest(
                key,
                value
        );
    }

    private ConfigurationChangeRequest existingRequest(
            UUID tenantId,
            String status) {

        ConfigurationChangeRequest request =
                new ConfigurationChangeRequest();

        request.setChangeRequestId(
                CHANGE_REQUEST_ID
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
                status
        );

        request.setJustification(
                "Controlled configuration change"
        );

        request.setAffectedEnvironment(
                "Development"
        );

        request.setRiskAssessment(
                "Validated operational risk"
        );

        request.setExpectedResult(
                "Expected result"
        );

        request.setRollbackPlan(
                "Rollback plan"
        );

        request.setRequestedAt(
                LocalDateTime.now()
        );

        request.setUpdatedAt(
                LocalDateTime.now()
        );

        return request;
    }
}