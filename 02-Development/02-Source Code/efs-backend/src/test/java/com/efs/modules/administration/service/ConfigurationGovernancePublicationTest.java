package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.ConfigurationChangeRequestResponse;
import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.ConfigurationChangeItem;
import com.efs.modules.administration.entity.ConfigurationChangeRequest;
import com.efs.modules.administration.entity.SystemConfiguration;
import com.efs.modules.administration.repository.ConfigurationChangeItemRepository;
import com.efs.modules.administration.repository.ConfigurationChangeRequestRepository;
import com.efs.modules.administration.repository.SystemConfigurationRepository;
import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditConfigurationChangeServiceInterface;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationGovernancePublicationTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "44440000-0000-0000-0000-000000000001"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "44440000-0000-0000-0000-000000000002"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "44440000-0000-0000-0000-000000000003"
            );

    private static final UUID CHANGE_REQUEST_ID =
            UUID.fromString(
                    "44440000-0000-0000-0000-000000000004"
            );

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "44440000-0000-0000-0000-000000000005"
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
    void shouldPublishOrganizationOverrideWithVersionOne() {

        authorize(
                null
        );

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        ConfigurationChangeItem item =
                item(
                        "EFS.REPORT.MAX_RECORDS",
                        "INTEGER",
                        "100",
                        "250"
                );

        prepareOrganizationPublication(
                request,
                item,
                Optional.empty(),
                Optional.of("100"),
                0
        );

        ConfigurationChangeRequestResponse response =
                service.publishChangeRequest(
                        CHANGE_REQUEST_ID,
                        securityContext
                );

        assertEquals(
                "APPLIED",
                response.getStatus()
        );

        assertEquals(
                USER_ID,
                response.getAppliedBy()
        );

        assertNotNull(
                response.getAppliedAt()
        );

        assertEquals(
                Integer.valueOf(1),
                response.getItems()
                        .get(0)
                        .getVersionNumber()
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SystemConfiguration>>
                configurationCaptor =
                ArgumentCaptor.forClass(
                        List.class
                );

        verify(
                systemConfigurationRepository
        ).saveAllAndFlush(
                configurationCaptor.capture()
        );

        SystemConfiguration published =
                configurationCaptor
                        .getValue()
                        .get(0);

        assertEquals(
                "EFS.REPORT.MAX_RECORDS",
                published.getConfigurationKey()
        );

        assertEquals(
                "250",
                published.getConfigurationValue()
        );

        assertEquals(
                "INTEGER",
                published.getConfigurationType()
        );

        assertEquals(
                ORGANIZATION_ID,
                published.getOrganizationId()
        );

        assertNull(
                published.getTenantId()
        );

        assertEquals(
                USER_ID,
                published.getUpdatedBy()
        );

        verify(
                auditConfigurationChangeService,
                times(1)
        ).createAuditConfigurationChange(
                any(AuditConfigurationChangeRequest.class)
        );
    }

    @Test
    void shouldPublishTenantOverrideUnderExactLockedScope() {

        authorize(
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

        ConfigurationChangeRequest request =
                approvedRequest(
                        TENANT_ID
                );

        ConfigurationChangeItem item =
                item(
                        "EFS.REPORT.MAX_RECORDS",
                        "INTEGER",
                        "150",
                        "200"
                );

        SystemConfiguration existing =
                new SystemConfiguration();

        existing.setConfigurationKey(
                "EFS.REPORT.MAX_RECORDS"
        );

        existing.setConfigurationValue(
                "150"
        );

        existing.setConfigurationType(
                "INTEGER"
        );

        existing.setOrganizationId(
                ORGANIZATION_ID
        );

        existing.setTenantId(
                TENANT_ID
        );

        existing.setEncrypted(
                false
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
                List.of(item)
        );

        when(
                systemConfigurationRepository
                        .findTenantConfigurationForUpdate(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                changeItemRepository
                        .findMaxAppliedVersionForTenant(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
        ).thenReturn(3);

        stubPersistence(
                request,
                List.of(item)
        );

        ConfigurationChangeRequestResponse response =
                service.publishChangeRequest(
                        CHANGE_REQUEST_ID,
                        securityContext
                );

        assertEquals(
                Integer.valueOf(4),
                response.getItems()
                        .get(0)
                        .getVersionNumber()
        );

        assertEquals(
                "200",
                existing.getConfigurationValue()
        );

        assertSame(
                existing,
                captureSinglePublishedConfiguration()
        );

        verifyNoInteractions(
                systemConfigurationService
        );
    }

    @Test
    void shouldRejectPublicationWhenEffectiveValueChangedAfterRequestCreation() {

        authorize(
                null
        );

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        ConfigurationChangeItem item =
                item(
                        "EFS.REPORT.MAX_RECORDS",
                        "INTEGER",
                        "100",
                        "250"
                );

        SystemConfiguration current =
                new SystemConfiguration();

        current.setConfigurationValue(
                "125"
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
                List.of(item)
        );

        when(
                systemConfigurationRepository
                        .findOrganizationConfigurationForUpdate(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.of(current)
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.publishChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verify(
                systemConfigurationRepository,
                never()
        ).saveAllAndFlush(
                anyList()
        );

        verifyNoInteractions(
                auditConfigurationChangeService
        );

        verify(
                auditEventService,
                never()
        ).createAuditEvent(
                any(AuditEventRequest.class)
        );
    }

    @Test
    void shouldRejectPublicationUnlessRequestIsApproved() {

        authorize(
                null
        );

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        request.setStatus(
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

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.publishChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verifyNoInteractions(
                systemConfigurationRepository,
                auditConfigurationChangeService
        );
    }

    @Test
    void shouldRejectPublicationBeforeCallerLookupWhenManagePermissionMissing() {

        when(
                securityContext.hasPermission(
                        "configuration.manage"
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.publishChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verifyNoInteractions(
                userAccountLookupService,
                changeRequestRepository,
                changeItemRepository,
                systemConfigurationRepository,
                auditEventService,
                auditConfigurationChangeService
        );
    }

    @Test
    void shouldRejectPersistedItemWhoseTypeNoLongerMatchesCatalog() {

        authorize(
                null
        );

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        ConfigurationChangeItem item =
                item(
                        "EFS.REPORT.MAX_RECORDS",
                        "STRING",
                        "100",
                        "250"
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
                List.of(item)
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        service.publishChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        );

        verifyNoInteractions(
                systemConfigurationRepository,
                auditConfigurationChangeService
        );
    }

    @Test
    void shouldPublishMultipleItemsWithOneAuditEventAndOneChangeAuditPerItem() {

        authorize(
                null
        );

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        ConfigurationChangeItem reportItem =
                item(
                        "EFS.REPORT.MAX_RECORDS",
                        "INTEGER",
                        "100",
                        "250"
                );

        ConfigurationChangeItem riskItem =
                item(
                        "EFS.RISK.MODEL.1.1.SCORE.MAX",
                        "STRING",
                        "100",
                        "150"
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
                List.of(
                        reportItem,
                        riskItem
                )
        );

        when(
                systemConfigurationRepository
                        .findOrganizationConfigurationForUpdate(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                systemConfigurationRepository
                        .findOrganizationConfigurationForUpdate(
                                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                Optional.empty()
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

        when(
                changeItemRepository
                        .findMaxAppliedVersionForOrganization(
                                "EFS.REPORT.MAX_RECORDS",
                                ORGANIZATION_ID
                        )
        ).thenReturn(0);

        when(
                changeItemRepository
                        .findMaxAppliedVersionForOrganization(
                                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                                ORGANIZATION_ID
                        )
        ).thenReturn(2);

        stubPersistence(
                request,
                List.of(
                        reportItem,
                        riskItem
                )
        );

        service.publishChangeRequest(
                CHANGE_REQUEST_ID,
                securityContext
        );

        verify(
                auditEventService,
                times(1)
        ).createAuditEvent(
                any(AuditEventRequest.class)
        );

        verify(
                auditConfigurationChangeService,
                times(2)
        ).createAuditConfigurationChange(
                any(AuditConfigurationChangeRequest.class)
        );

        assertEquals(
                Integer.valueOf(1),
                reportItem.getVersionNumber()
        );

        assertEquals(
                Integer.valueOf(3),
                riskItem.getVersionNumber()
        );
    }

    @Test
    void shouldCreatePublicationAuditWithoutConfigurationValues() {

        authorize(
                null
        );

        ConfigurationChangeRequest request =
                approvedRequest(
                        null
                );

        ConfigurationChangeItem item =
                item(
                        "EFS.REPORT.MAX_RECORDS",
                        "INTEGER",
                        "100",
                        "250"
                );

        prepareOrganizationPublication(
                request,
                item,
                Optional.empty(),
                Optional.of("100"),
                0
        );

        ArgumentCaptor<AuditEventRequest> eventCaptor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        ArgumentCaptor<AuditConfigurationChangeRequest>
                changeCaptor =
                ArgumentCaptor.forClass(
                        AuditConfigurationChangeRequest.class
                );

        service.publishChangeRequest(
                CHANGE_REQUEST_ID,
                securityContext
        );

        verify(
                auditEventService
        ).createAuditEvent(
                eventCaptor.capture()
        );

        AuditEventRequest auditEvent =
                eventCaptor.getValue();

        assertEquals(
                "SYSTEM_CONFIGURATION_CHANGE",
                auditEvent.getEventType()
        );

        assertEquals(
                "CONFIGURATION_CHANGE_REQUEST",
                auditEvent.getEntityType()
        );

        assertEquals(
                "PUBLISH",
                auditEvent.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                auditEvent.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                auditEvent.getEventResult()
        );

        assertEquals(
                Set.of(
                        "permissionCode",
                        "status",
                        "scope",
                        "itemCount",
                        "catalogVersion"
                ),
                auditEvent.getEventDetails()
                        .keySet()
        );

        verify(
                auditConfigurationChangeService
        ).createAuditConfigurationChange(
                changeCaptor.capture()
        );

        AuditConfigurationChangeRequest changeAudit =
                changeCaptor.getValue();

        assertEquals(
                AUDIT_EVENT_ID,
                changeAudit.getAuditEventId()
        );

        assertEquals(
                "EFS.REPORT.MAX_RECORDS",
                changeAudit.getConfigurationKey()
        );

        assertEquals(
                "100",
                changeAudit.getPreviousValue()
                        .get("value")
        );

        assertEquals(
                "250",
                changeAudit.getCurrentValue()
                        .get("value")
        );

        assertEquals(
                USER_ID,
                changeAudit.getChangedBy()
        );
    }

    @Test
    void publishMethodShouldBeTransactional() throws Exception {

        Method method =
                ConfigurationGovernanceService.class
                        .getMethod(
                                "publishChangeRequest",
                                UUID.class,
                                SecurityContext.class
                        );

        Transactional transactional =
                method.getAnnotation(
                        Transactional.class
                );

        assertNotNull(
                transactional
        );
    }

    private void authorize(
            UUID callerTenantId) {

        when(
                securityContext.hasPermission(
                        "configuration.manage"
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
                        "uc044-publication@example.com"
                )
        );
    }

    private ConfigurationChangeRequest approvedRequest(
            UUID tenantId) {

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
                "APPROVED"
        );

        request.setJustification(
                "Approved controlled configuration change"
        );

        request.setAffectedEnvironment(
                "Development"
        );

        request.setRiskAssessment(
                "Validated operational risk"
        );

        request.setExpectedResult(
                "Published institutional override"
        );

        request.setRollbackPlan(
                "Restore previous approved value"
        );

        request.setRequestedAt(
                LocalDateTime.now()
        );

        request.setApprovedBy(
                USER_ID
        );

        request.setApprovedAt(
                LocalDateTime.now()
        );

        request.setUpdatedAt(
                LocalDateTime.now()
        );

        return request;
    }

    private ConfigurationChangeItem item(
            String key,
            String type,
            String previousValue,
            String proposedValue) {

        ConfigurationChangeItem item =
                new ConfigurationChangeItem();

        item.setChangeRequestId(
                CHANGE_REQUEST_ID
        );

        item.setConfigurationKey(
                key
        );

        item.setConfigurationType(
                type
        );

        item.setPreviousValue(
                previousValue
        );

        item.setProposedValue(
                proposedValue
        );

        item.setEncrypted(
                false
        );

        item.setCreatedAt(
                LocalDateTime.now()
        );

        return item;
    }

    private void prepareOrganizationPublication(
            ConfigurationChangeRequest request,
            ConfigurationChangeItem item,
            Optional<SystemConfiguration> exactConfiguration,
            Optional<String> effectiveValue,
            int previousVersion) {

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
                List.of(item)
        );

        when(
                systemConfigurationRepository
                        .findOrganizationConfigurationForUpdate(
                                item.getConfigurationKey(),
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                exactConfiguration
        );

        if (exactConfiguration.isEmpty()) {

            when(
                    systemConfigurationService
                            .resolveConfigurationValue(
                                    item.getConfigurationKey(),
                                    ORGANIZATION_ID,
                                    null
                            )
            ).thenReturn(
                    effectiveValue
            );
        }

        when(
                changeItemRepository
                        .findMaxAppliedVersionForOrganization(
                                item.getConfigurationKey(),
                                ORGANIZATION_ID
                        )
        ).thenReturn(
                previousVersion
        );

        stubPersistence(
                request,
                List.of(item)
        );
    }

    private void stubPersistence(
            ConfigurationChangeRequest request,
            List<ConfigurationChangeItem> items) {

        when(
                systemConfigurationRepository
                        .saveAllAndFlush(
                                anyList()
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                changeItemRepository
                        .saveAllAndFlush(
                                anyList()
                        )
        ).thenReturn(
                items
        );

        when(
                changeRequestRepository
                        .saveAndFlush(
                                request
                        )
        ).thenReturn(
                request
        );

        AuditEventResponse auditResponse =
                new AuditEventResponse();

        auditResponse.setAuditEventId(
                AUDIT_EVENT_ID
        );

        when(
                auditEventService
                        .createAuditEvent(
                                any(AuditEventRequest.class)
                        )
        ).thenReturn(
                auditResponse
        );
    }

    private SystemConfiguration
    captureSinglePublishedConfiguration() {

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SystemConfiguration>>
                captor =
                ArgumentCaptor.forClass(
                        List.class
                );

        verify(
                systemConfigurationRepository
        ).saveAllAndFlush(
                captor.capture()
        );

        assertEquals(
                1,
                captor.getValue().size()
        );

        return captor.getValue()
                .get(0);
    }
}