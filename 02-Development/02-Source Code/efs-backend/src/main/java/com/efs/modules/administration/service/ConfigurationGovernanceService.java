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
import com.efs.modules.administration.repository.SystemConfigurationRepository;
import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditConfigurationChangeServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ConfigurationGovernanceService
        implements ConfigurationGovernanceServiceInterface {

    private static final String STATUS_PENDING_APPROVAL =
            "PENDING_APPROVAL";

    private static final String STATUS_APPROVED =
            "APPROVED";

    private static final String STATUS_APPLIED =
            "APPLIED";

    private static final String STATUS_REJECTED =
            "REJECTED";

    private static final String PERMISSION_VIEW =
            "configuration.view";

    private static final String PERMISSION_MANAGE =
            "configuration.manage";

    private static final String PERMISSION_APPROVE =
            "configuration.approve";

    private final ConfigurationChangeRequestRepository
            changeRequestRepository;

    private final ConfigurationChangeItemRepository
            changeItemRepository;

    private final ConfigurationDefinitionCatalog
            definitionCatalog;

    private final SystemConfigurationServiceInterface
            systemConfigurationService;

    private final SystemConfigurationRepository
            systemConfigurationRepository;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    private final AuditEventServiceInterface
            auditEventService;

    private final AuditConfigurationChangeServiceInterface
            auditConfigurationChangeService;


    public ConfigurationGovernanceService(
            ConfigurationChangeRequestRepository changeRequestRepository,
            ConfigurationChangeItemRepository changeItemRepository,
            ConfigurationDefinitionCatalog definitionCatalog,
            SystemConfigurationServiceInterface systemConfigurationService,
            SystemConfigurationRepository systemConfigurationRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            TenantOrganizationLookupServiceInterface tenantOrganizationLookupService,
            AuditEventServiceInterface auditEventService,
            AuditConfigurationChangeServiceInterface auditConfigurationChangeService) {

        this.changeRequestRepository =
                changeRequestRepository;

        this.changeItemRepository =
                changeItemRepository;

        this.definitionCatalog =
                definitionCatalog;

        this.systemConfigurationService =
                systemConfigurationService;

        this.systemConfigurationRepository =
                systemConfigurationRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;

        this.auditEventService =
                auditEventService;

        this.auditConfigurationChangeService =
                auditConfigurationChangeService;

    }

    @Override
    @Transactional
    public ConfigurationChangeRequestResponse
    createChangeRequest(
            ConfigurationChangeRequestCreateRequest request,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_MANAGE
                );

        validateCreateRequest(
                request
        );

        UUID targetTenantId =
                validateTargetScope(
                        request.getTenantId(),
                        actor
                );

        ConfigurationDefinitionCatalog.Scope scope =
                targetTenantId == null
                        ? ConfigurationDefinitionCatalog.Scope.ORGANIZATION
                        : ConfigurationDefinitionCatalog.Scope.TENANT;

        Set<String> configurationKeys =
                new HashSet<>();

        List<PreparedItem> preparedItems =
                new ArrayList<>();

        for (
                ConfigurationChangeItemRequest itemRequest
                : request.getItems()
        ) {

            if (itemRequest == null) {
                throw new RequestValidationException(
                        "Configuration change item must not be null"
                );
            }

            String configurationKey =
                    normalizeRequiredText(
                            itemRequest.getConfigurationKey(),
                            "configurationKey"
                    );

            String proposedValue =
                    normalizeRequiredText(
                            itemRequest.getProposedValue(),
                            "proposedValue"
                    );

            if (!configurationKeys.add(configurationKey)) {
                throw new RequestValidationException(
                        "Duplicate configuration key in change request: "
                                + configurationKey
                );
            }

            ConfigurationDefinitionCatalog.Definition
                    definition =
                    requireDefinition(
                            configurationKey,
                            scope
                    );

            validateValue(
                    definition,
                    proposedValue
            );

            if (definition.encrypted()) {
                throw new RequestValidationException(
                        "Encrypted configuration is not supported by catalog v1: "
                                + configurationKey
                );
            }

            String previousValue =
                    systemConfigurationService
                            .resolveConfigurationValue(
                                    configurationKey,
                                    actor.organizationId(),
                                    targetTenantId
                            )
                            .orElse(null);

            preparedItems.add(
                    new PreparedItem(
                            definition,
                            previousValue,
                            proposedValue
                    )
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        ConfigurationChangeRequest changeRequest =
                new ConfigurationChangeRequest();

        changeRequest.setOrganizationId(
                actor.organizationId()
        );

        changeRequest.setTenantId(
                targetTenantId
        );

        changeRequest.setRequestedBy(
                actor.userId()
        );

        changeRequest.setStatus(
                STATUS_PENDING_APPROVAL
        );

        changeRequest.setJustification(
                normalizeRequiredText(
                        request.getJustification(),
                        "justification"
                )
        );

        changeRequest.setAffectedEnvironment(
                normalizeRequiredText(
                        request.getAffectedEnvironment(),
                        "affectedEnvironment"
                )
        );

        changeRequest.setRiskAssessment(
                normalizeRequiredText(
                        request.getRiskAssessment(),
                        "riskAssessment"
                )
        );

        changeRequest.setExpectedResult(
                normalizeRequiredText(
                        request.getExpectedResult(),
                        "expectedResult"
                )
        );

        changeRequest.setRollbackPlan(
                normalizeRequiredText(
                        request.getRollbackPlan(),
                        "rollbackPlan"
                )
        );

        changeRequest.setRequestedAt(
                now
        );

        changeRequest.setUpdatedAt(
                now
        );

        ConfigurationChangeRequest savedRequest =
                changeRequestRepository
                        .saveAndFlush(
                                changeRequest
                        );

        List<ConfigurationChangeItem> items =
                preparedItems.stream()
                        .map(
                                preparedItem ->
                                        toEntity(
                                                savedRequest
                                                        .getChangeRequestId(),
                                                preparedItem,
                                                now
                                        )
                        )
                        .toList();

        List<ConfigurationChangeItem> savedItems =
                changeItemRepository
                        .saveAllAndFlush(
                                items
                        );

        return toResponse(
                savedRequest,
                savedItems
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationChangeRequestResponse
    getChangeRequest(
            UUID changeRequestId,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_VIEW
                );

        ConfigurationChangeRequest changeRequest =
                findScopedRequest(
                        requireChangeRequestId(
                                changeRequestId
                        ),
                        actor
                );

        return toResponse(
                changeRequest,
                loadItems(
                        changeRequest.getChangeRequestId()
                )
        );
    }

    @Override
    @Transactional
    public ConfigurationChangeRequestResponse
    approveChangeRequest(
            UUID changeRequestId,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_APPROVE
                );

        ConfigurationChangeRequest changeRequest =
                findScopedRequest(
                        requireChangeRequestId(
                                changeRequestId
                        ),
                        actor
                );

        requirePendingApproval(
                changeRequest
        );

        LocalDateTime now =
                LocalDateTime.now();

        changeRequest.setStatus(
                STATUS_APPROVED
        );

        changeRequest.setApprovedBy(
                actor.userId()
        );

        changeRequest.setApprovedAt(
                now
        );

        changeRequest.setUpdatedAt(
                now
        );

        ConfigurationChangeRequest savedRequest =
                changeRequestRepository
                        .saveAndFlush(
                                changeRequest
                        );

        return toResponse(
                savedRequest,
                loadItems(
                        savedRequest.getChangeRequestId()
                )
        );
    }

    @Override
    @Transactional
    public ConfigurationChangeRequestResponse
    rejectChangeRequest(
            UUID changeRequestId,
            String rejectionReason,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_APPROVE
                );

        String normalizedReason =
                normalizeRequiredText(
                        rejectionReason,
                        "rejectionReason"
                );

        ConfigurationChangeRequest changeRequest =
                findScopedRequest(
                        requireChangeRequestId(
                                changeRequestId
                        ),
                        actor
                );

        requirePendingApproval(
                changeRequest
        );

        LocalDateTime now =
                LocalDateTime.now();

        changeRequest.setStatus(
                STATUS_REJECTED
        );

        changeRequest.setRejectedBy(
                actor.userId()
        );

        changeRequest.setRejectedAt(
                now
        );

        changeRequest.setRejectionReason(
                normalizedReason
        );

        changeRequest.setUpdatedAt(
                now
        );

        ConfigurationChangeRequest savedRequest =
                changeRequestRepository
                        .saveAndFlush(
                                changeRequest
                        );

        recordRejectedAudit(
                savedRequest,
                actor,
                securityContext,
                normalizedReason
        );

        return toResponse(
                savedRequest,
                loadItems(
                        savedRequest.getChangeRequestId()
                )
        );
    }

    @Override
    @Transactional
    public ConfigurationChangeRequestResponse
    publishChangeRequest(
            UUID changeRequestId,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_MANAGE
                );

        ConfigurationChangeRequest changeRequest =
                findScopedRequest(
                        requireChangeRequestId(
                                changeRequestId
                        ),
                        actor
                );

        requireApproved(
                changeRequest
        );

        UUID targetTenantId =
                validateTargetScope(
                        changeRequest.getTenantId(),
                        actor
                );

        AtomicReference<PublicationOutcome> publicationOutcome =
                registerPublicationOutcomeSynchronization(
                        changeRequest.getChangeRequestId(),
                        targetTenantId,
                        actor,
                        securityContext
                );

        try {

        ConfigurationDefinitionCatalog.Scope scope =
                targetTenantId == null
                        ? ConfigurationDefinitionCatalog.Scope.ORGANIZATION
                        : ConfigurationDefinitionCatalog.Scope.TENANT;

        lockPublicationScope(
                targetTenantId,
                actor
        );

        List<ConfigurationChangeItem> items =
                loadItems(
                        changeRequest.getChangeRequestId()
                );

        if (items.isEmpty()) {
            throw new RequestValidationException(
                    "Approved configuration change request has no items"
            );
        }

        List<PreparedPublication> preparedPublications =
                new ArrayList<>();

        for (ConfigurationChangeItem item : items) {

            ConfigurationDefinitionCatalog.Definition definition =
                    requireDefinition(
                            item.getConfigurationKey(),
                            scope
                    );

            validateValue(
                    definition,
                    item.getProposedValue()
            );

            if (
                    !definition.configurationType()
                            .equals(
                                    item.getConfigurationType()
                            )
            ) {
                throw new RequestValidationException(
                        "Configuration type changed after request creation: "
                                + item.getConfigurationKey()
                );
            }

            if (
                    definition.encrypted()
                            != Boolean.TRUE.equals(
                                    item.getEncrypted()
                            )
            ) {
                throw new RequestValidationException(
                        "Configuration encryption contract changed after request creation: "
                                + item.getConfigurationKey()
                );
            }

            Optional<SystemConfiguration> exactConfiguration =
                    lockExactPublishedConfiguration(
                            item.getConfigurationKey(),
                            actor.organizationId(),
                            targetTenantId
                    );

            String currentEffectiveValue =
                    exactConfiguration
                            .map(
                                    SystemConfiguration::getConfigurationValue
                            )
                            .orElseGet(
                                    () ->
                                            systemConfigurationService
                                                    .resolveConfigurationValue(
                                                            item.getConfigurationKey(),
                                                            actor.organizationId(),
                                                            targetTenantId
                                                    )
                                                    .orElse(null)
                            );

            if (
                    !Objects.equals(
                            currentEffectiveValue,
                            item.getPreviousValue()
                    )
            ) {
                throw new RequestValidationException(
                        "Published configuration changed after request creation: "
                                + item.getConfigurationKey()
                );
            }

            int previousVersion =
                    findMaxAppliedVersion(
                            item.getConfigurationKey(),
                            actor.organizationId(),
                            targetTenantId
                    );

            int nextVersion;

            try {
                nextVersion =
                        Math.addExact(
                                previousVersion,
                                1
                        );
            }
            catch (ArithmeticException exception) {
                throw new RequestValidationException(
                        "Configuration version overflow: "
                                + item.getConfigurationKey()
                );
            }

            preparedPublications.add(
                    new PreparedPublication(
                            item,
                            definition,
                            exactConfiguration,
                            nextVersion
                    )
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        List<SystemConfiguration> publishedConfigurations =
                new ArrayList<>();

        for (
                PreparedPublication publication
                : preparedPublications
        ) {

            SystemConfiguration configuration =
                    publication.exactConfiguration()
                            .orElseGet(
                                    SystemConfiguration::new
                            );

            configuration.setConfigurationKey(
                    publication.item()
                            .getConfigurationKey()
            );

            configuration.setConfigurationValue(
                    publication.item()
                            .getProposedValue()
            );

            configuration.setConfigurationType(
                    publication.definition()
                            .configurationType()
            );

            configuration.setOrganizationId(
                    actor.organizationId()
            );

            configuration.setTenantId(
                    targetTenantId
            );

            configuration.setEncrypted(
                    publication.definition()
                            .encrypted()
            );

            configuration.setUpdatedBy(
                    actor.userId()
            );

            configuration.setUpdatedAt(
                    now
            );

            publishedConfigurations.add(
                    configuration
            );

            publication.item()
                    .setVersionNumber(
                            publication.nextVersion()
                    );
        }

        systemConfigurationRepository
                .saveAllAndFlush(
                        publishedConfigurations
                );

        List<ConfigurationChangeItem> savedItems =
                changeItemRepository
                        .saveAllAndFlush(
                                items
                        );

        changeRequest.setStatus(
                STATUS_APPLIED
        );

        changeRequest.setAppliedBy(
                actor.userId()
        );

        changeRequest.setAppliedAt(
                now
        );

        changeRequest.setUpdatedAt(
                now
        );

        ConfigurationChangeRequest savedRequest =
                changeRequestRepository
                        .saveAndFlush(
                                changeRequest
                        );

        AuditEventResponse auditEvent =
                recordPublishedAudit(
                        savedRequest,
                        actor,
                        securityContext,
                        savedItems.size()
                );

        for (
                ConfigurationChangeItem item
                : savedItems
        ) {
            recordPublishedConfigurationChange(
                    auditEvent.getAuditEventId(),
                    savedRequest,
                    item,
                    actor
            );
        }

        return toResponse(
                savedRequest,
                savedItems
        );

        }
        catch (RequestValidationException exception) {

            publicationOutcome.set(
                    new PublicationOutcome(
                            "REJECTED",
                            exception.getClass()
                                    .getSimpleName()
                    )
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            publicationOutcome.set(
                    new PublicationOutcome(
                            "FAILURE",
                            exception.getClass()
                                    .getSimpleName()
                    )
            );

            throw exception;
        }
    }

    private AtomicReference<PublicationOutcome>
    registerPublicationOutcomeSynchronization(
            UUID changeRequestId,
            UUID targetTenantId,
            ActorContext actor,
            SecurityContext securityContext) {

        AtomicReference<PublicationOutcome> reference =
                new AtomicReference<>();

        if (
                !TransactionSynchronizationManager
                        .isSynchronizationActive()
        ) {
            return reference;
        }

        UUID sessionId =
                securityContext == null
                        ? null
                        : securityContext.getSessionId();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(
                            int status) {

                        PublicationOutcome outcome =
                                reference.get();

                        if (
                                status
                                        != TransactionSynchronization
                                        .STATUS_ROLLED_BACK
                                || outcome == null
                        ) {
                            return;
                        }

                        finalizePublicationOutcome(
                                changeRequestId,
                                targetTenantId,
                                actor,
                                sessionId,
                                outcome
                        );
                    }
                }
        );

        return reference;
    }

    private void finalizePublicationOutcome(
            UUID changeRequestId,
            UUID targetTenantId,
            ActorContext actor,
            UUID sessionId,
            PublicationOutcome outcome) {

        if (
                "FAILURE".equals(
                        outcome.eventResult()
                )
        ) {

            String failureReason =
                    "CONFIGURATION_PUBLICATION_FAILED ["
                            + outcome.errorType()
                            + "]";

            int updated =
                    changeRequestRepository
                            .markApprovedRequestFailedRequiresNew(
                                    changeRequestId,
                                    actor.organizationId(),
                                    failureReason
                            );

            if (updated == 0) {
                return;
            }
        }

        recordPublicationOutcomeAuditRequiresNew(
                changeRequestId,
                targetTenantId,
                actor,
                sessionId,
                outcome
        );
    }

    private void recordPublicationOutcomeAuditRequiresNew(
            UUID changeRequestId,
            UUID targetTenantId,
            ActorContext actor,
            UUID sessionId,
            PublicationOutcome outcome) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                PERMISSION_MANAGE
        );

        details.put(
                "status",
                "FAILURE".equals(
                        outcome.eventResult()
                )
                        ? "FAILED"
                        : STATUS_APPROVED
        );

        details.put(
                "errorType",
                outcome.errorType()
        );

        AuditEventRequest auditRequest =
                new AuditEventRequest();

        auditRequest.setOrganizationId(
                actor.organizationId()
        );

        auditRequest.setTenantId(
                targetTenantId
        );

        auditRequest.setUserId(
                actor.userId()
        );

        auditRequest.setSessionId(
                sessionId
        );

        auditRequest.setEventType(
                "SYSTEM_CONFIGURATION_CHANGE"
        );

        auditRequest.setEntityType(
                "CONFIGURATION_CHANGE_REQUEST"
        );

        auditRequest.setEntityId(
                changeRequestId
        );

        auditRequest.setAction(
                "PUBLISH"
        );

        auditRequest.setSourceComponent(
                "ADMINISTRATION"
        );

        auditRequest.setEventResult(
                outcome.eventResult()
        );

        auditRequest.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        auditRequest
                );
    }

    private void lockPublicationScope(
            UUID targetTenantId,
            ActorContext actor) {

        if (targetTenantId == null) {

            tenantOrganizationLookupService
                    .lockOrganization(
                            actor.organizationId()
                    );

            return;
        }

        tenantOrganizationLookupService
                .lockTenant(
                        targetTenantId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationEffectiveResponse>
    getEffectiveConfigurations(
            UUID tenantId,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_VIEW
                );

        UUID targetTenantId =
                validateTargetScope(
                        tenantId,
                        actor
                );

        ConfigurationDefinitionCatalog.Scope scope =
                targetTenantId == null
                        ? ConfigurationDefinitionCatalog.Scope.ORGANIZATION
                        : ConfigurationDefinitionCatalog.Scope.TENANT;

        List<ConfigurationEffectiveResponse> result =
                new ArrayList<>();

        for (
                ConfigurationDefinitionCatalog.Definition definition
                : definitionCatalog.getDefinitions()
        ) {

            if (
                    !definition.allowedScopes()
                            .contains(
                                    scope
                            )
            ) {
                continue;
            }

            systemConfigurationService
                    .resolveConfiguration(
                            definition.configurationKey(),
                            actor.organizationId(),
                            targetTenantId
                    )
                    .map(
                            configuration ->
                                    toEffectiveResponse(
                                            configuration,
                                            definition
                                    )
                    )
                    .ifPresent(
                            result::add
                    );
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationEffectiveResponse
    getEffectiveConfiguration(
            String configurationKey,
            UUID tenantId,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_VIEW
                );

        UUID targetTenantId =
                validateTargetScope(
                        tenantId,
                        actor
                );

        ConfigurationDefinitionCatalog.Scope scope =
                targetTenantId == null
                        ? ConfigurationDefinitionCatalog.Scope.ORGANIZATION
                        : ConfigurationDefinitionCatalog.Scope.TENANT;

        String normalizedKey =
                normalizeRequiredText(
                        configurationKey,
                        "configurationKey"
                );

        ConfigurationDefinitionCatalog.Definition definition =
                requireDefinition(
                        normalizedKey,
                        scope
                );

        SystemConfiguration configuration =
                systemConfigurationService
                        .resolveConfiguration(
                                normalizedKey,
                                actor.organizationId(),
                                targetTenantId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Effective configuration not found: "
                                                        + normalizedKey
                                        )
                        );

        return toEffectiveResponse(
                configuration,
                definition
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationVersionResponse>
    getAppliedVersions(
            String configurationKey,
            UUID tenantId,
            SecurityContext securityContext) {

        ActorContext actor =
                authorize(
                        securityContext,
                        PERMISSION_VIEW
                );

        UUID targetTenantId =
                validateTargetScope(
                        tenantId,
                        actor
                );

        ConfigurationDefinitionCatalog.Scope scope =
                targetTenantId == null
                        ? ConfigurationDefinitionCatalog.Scope.ORGANIZATION
                        : ConfigurationDefinitionCatalog.Scope.TENANT;

        String normalizedKey =
                normalizeRequiredText(
                        configurationKey,
                        "configurationKey"
                );

        requireDefinition(
                normalizedKey,
                scope
        );

        List<ConfigurationChangeRequest> appliedRequests =
                changeRequestRepository
                        .findByOrganizationIdAndStatusOrderByRequestedAtDesc(
                                actor.organizationId(),
                                STATUS_APPLIED
                        );

        List<ConfigurationVersionResponse> result =
                new ArrayList<>();

        for (
                ConfigurationChangeRequest request
                : appliedRequests
        ) {

            if (
                    !Objects.equals(
                            request.getTenantId(),
                            targetTenantId
                    )
            ) {
                continue;
            }

            List<ConfigurationChangeItem> items =
                    loadItems(
                            request.getChangeRequestId()
                    );

            for (
                    ConfigurationChangeItem item
                    : items
            ) {

                if (
                        !normalizedKey.equals(
                                item.getConfigurationKey()
                        )
                                || item.getVersionNumber() == null
                                || item.getVersionNumber() <= 0
                ) {
                    continue;
                }

                result.add(
                        toVersionResponse(
                                request,
                                item
                        )
                );
            }
        }

        result.sort(
                (left, right) ->
                        Integer.compare(
                                right.getVersionNumber(),
                                left.getVersionNumber()
                        )
        );

        return result;
    }

    private ConfigurationEffectiveResponse
    toEffectiveResponse(
            SystemConfiguration configuration,
            ConfigurationDefinitionCatalog.Definition definition) {

        ConfigurationEffectiveResponse response =
                new ConfigurationEffectiveResponse();

        response.setConfigurationKey(
                configuration.getConfigurationKey()
        );

        response.setConfigurationValue(
                Boolean.TRUE.equals(
                        configuration.getEncrypted()
                )
                        ? "[REDACTED]"
                        : configuration.getConfigurationValue()
        );

        response.setConfigurationType(
                configuration.getConfigurationType()
        );

        response.setEffectiveScope(
                configuration.getTenantId() != null
                        ? "TENANT"
                        : configuration.getOrganizationId() != null
                                ? "ORGANIZATION"
                                : "GLOBAL"
        );

        response.setOrganizationId(
                configuration.getOrganizationId()
        );

        response.setTenantId(
                configuration.getTenantId()
        );

        response.setEncrypted(
                configuration.getEncrypted()
        );

        response.setCritical(
                definition.critical()
        );

        response.setUpdatedBy(
                configuration.getUpdatedBy()
        );

        response.setUpdatedAt(
                configuration.getUpdatedAt()
        );

        return response;
    }

    private ConfigurationVersionResponse
    toVersionResponse(
            ConfigurationChangeRequest request,
            ConfigurationChangeItem item) {

        ConfigurationVersionResponse response =
                new ConfigurationVersionResponse();

        response.setChangeRequestId(
                request.getChangeRequestId()
        );

        response.setConfigurationKey(
                item.getConfigurationKey()
        );

        response.setConfigurationValue(
                Boolean.TRUE.equals(
                        item.getEncrypted()
                )
                        ? "[REDACTED]"
                        : item.getProposedValue()
        );

        response.setConfigurationType(
                item.getConfigurationType()
        );

        response.setVersionNumber(
                item.getVersionNumber()
        );

        response.setOrganizationId(
                request.getOrganizationId()
        );

        response.setTenantId(
                request.getTenantId()
        );

        response.setEncrypted(
                item.getEncrypted()
        );

        response.setAppliedBy(
                request.getAppliedBy()
        );

        response.setAppliedAt(
                request.getAppliedAt()
        );

        return response;
    }
    private ActorContext authorize(
            SecurityContext securityContext,
            String permissionCode) {

        if (
                securityContext == null
                        || !securityContext.hasPermission(
                                permissionCode
                        )
        ) {
            throw new AccessDeniedException(
                    "Missing permission: "
                            + permissionCode
            );
        }

        UUID userId =
                securityContext.getUserId();

        if (userId == null) {
            throw new AccessDeniedException(
                    "Authenticated user is required"
            );
        }

        UserAccountReference authorizedUser =
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        );

        if (
                authorizedUser == null
                        || authorizedUser.organizationId()
                        == null
        ) {
            throw new AccessDeniedException(
                    "Authorized organization is required"
            );
        }

        return new ActorContext(
                userId,
                authorizedUser.organizationId(),
                authorizedUser.tenantId()
        );
    }

    private UUID validateTargetScope(
            UUID targetTenantId,
            ActorContext actor) {

        if (targetTenantId == null) {

            if (actor.tenantId() != null) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot manage organization configuration"
                );
            }

            return null;
        }

        if (
                actor.tenantId() != null
                        && !actor.tenantId()
                        .equals(targetTenantId)
        ) {
            throw new AccessDeniedException(
                    "Tenant-scoped user cannot manage another tenant"
            );
        }

        UUID tenantOrganizationId =
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                targetTenantId
                        );

        if (
                !actor.organizationId()
                        .equals(
                                tenantOrganizationId
                        )
        ) {
            throw new AccessDeniedException(
                    "Tenant does not belong to the authorized organization"
            );
        }

        return targetTenantId;
    }

    private void validateCreateRequest(
            ConfigurationChangeRequestCreateRequest request) {

        if (request == null) {
            throw new RequestValidationException(
                    "Configuration change request is required"
            );
        }

        normalizeRequiredText(
                request.getJustification(),
                "justification"
        );

        normalizeRequiredText(
                request.getAffectedEnvironment(),
                "affectedEnvironment"
        );

        normalizeRequiredText(
                request.getRiskAssessment(),
                "riskAssessment"
        );

        normalizeRequiredText(
                request.getExpectedResult(),
                "expectedResult"
        );

        normalizeRequiredText(
                request.getRollbackPlan(),
                "rollbackPlan"
        );

        if (
                request.getItems() == null
                        || request.getItems().isEmpty()
        ) {
            throw new RequestValidationException(
                    "At least one configuration change item is required"
            );
        }
    }

    private ConfigurationDefinitionCatalog.Definition
    requireDefinition(
            String configurationKey,
            ConfigurationDefinitionCatalog.Scope scope) {

        try {
            return definitionCatalog
                    .requireManageableDefinition(
                            configurationKey,
                            scope
                    );
        }
        catch (IllegalArgumentException exception) {
            throw new RequestValidationException(
                    exception.getMessage()
            );
        }
    }

    private void validateValue(
            ConfigurationDefinitionCatalog.Definition definition,
            String proposedValue) {

        try {
            definitionCatalog.validateValue(
                    definition,
                    proposedValue
            );
        }
        catch (IllegalArgumentException exception) {
            throw new RequestValidationException(
                    exception.getMessage()
            );
        }
    }

    private ConfigurationChangeRequest findScopedRequest(
            UUID changeRequestId,
            ActorContext actor) {

        if (actor.tenantId() != null) {

            return changeRequestRepository
                    .findByChangeRequestIdAndOrganizationIdAndTenantId(
                            changeRequestId,
                            actor.organizationId(),
                            actor.tenantId()
                    )
                    .orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "Configuration change request not found: "
                                                    + changeRequestId
                                    )
                    );
        }

        return changeRequestRepository
                .findByChangeRequestIdAndOrganizationId(
                        changeRequestId,
                        actor.organizationId()
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Configuration change request not found: "
                                                + changeRequestId
                                )
                );
    }

    private void requirePendingApproval(
            ConfigurationChangeRequest changeRequest) {

        if (
                !STATUS_PENDING_APPROVAL.equals(
                        changeRequest.getStatus()
                )
        ) {
            throw new RequestValidationException(
                    "Configuration change request must be PENDING_APPROVAL"
            );
        }
    }

    private void requireApproved(
            ConfigurationChangeRequest changeRequest) {

        if (
                !STATUS_APPROVED.equals(
                        changeRequest.getStatus()
                )
        ) {
            throw new RequestValidationException(
                    "Configuration change request must be APPROVED"
            );
        }
    }

    private UUID requireChangeRequestId(
            UUID changeRequestId) {

        if (changeRequestId == null) {
            throw new RequestValidationException(
                    "changeRequestId is required"
            );
        }

        return changeRequestId;
    }

    private String normalizeRequiredText(
            String value,
            String fieldName) {

        if (
                value == null
                        || value.isBlank()
        ) {
            throw new RequestValidationException(
                    fieldName
                            + " must not be null or blank"
            );
        }

        return value.trim();
    }

    private Optional<SystemConfiguration>
    lockExactPublishedConfiguration(
            String configurationKey,
            UUID organizationId,
            UUID tenantId) {

        if (tenantId == null) {
            return systemConfigurationRepository
                    .findOrganizationConfigurationForUpdate(
                            configurationKey,
                            organizationId
                    );
        }

        return systemConfigurationRepository
                .findTenantConfigurationForUpdate(
                        configurationKey,
                        organizationId,
                        tenantId
                );
    }

    private int findMaxAppliedVersion(
            String configurationKey,
            UUID organizationId,
            UUID tenantId) {

        Integer version;

        if (tenantId == null) {

            version =
                    changeItemRepository
                            .findMaxAppliedVersionForOrganization(
                                    configurationKey,
                                    organizationId
                            );
        }
        else {

            version =
                    changeItemRepository
                            .findMaxAppliedVersionForTenant(
                                    configurationKey,
                                    organizationId,
                                    tenantId
                            );
        }

        return version == null
                ? 0
                : version;
    }

    private ConfigurationChangeItem toEntity(
            UUID changeRequestId,
            PreparedItem preparedItem,
            LocalDateTime createdAt) {

        ConfigurationChangeItem item =
                new ConfigurationChangeItem();

        item.setChangeRequestId(
                changeRequestId
        );

        item.setConfigurationKey(
                preparedItem.definition()
                        .configurationKey()
        );

        item.setPreviousValue(
                preparedItem.previousValue()
        );

        item.setProposedValue(
                preparedItem.proposedValue()
        );

        item.setConfigurationType(
                preparedItem.definition()
                        .configurationType()
        );

        item.setEncrypted(
                preparedItem.definition()
                        .encrypted()
        );

        item.setVersionNumber(
                null
        );

        item.setCreatedAt(
                createdAt
        );

        return item;
    }

    private List<ConfigurationChangeItem> loadItems(
            UUID changeRequestId) {

        return changeItemRepository
                .findByChangeRequestIdOrderByCreatedAtAsc(
                        changeRequestId
                );
    }

    private ConfigurationChangeRequestResponse toResponse(
            ConfigurationChangeRequest request,
            List<ConfigurationChangeItem> items) {

        ConfigurationChangeRequestResponse response =
                new ConfigurationChangeRequestResponse();

        response.setChangeRequestId(
                request.getChangeRequestId()
        );

        response.setOrganizationId(
                request.getOrganizationId()
        );

        response.setTenantId(
                request.getTenantId()
        );

        response.setRequestedBy(
                request.getRequestedBy()
        );

        response.setStatus(
                request.getStatus()
        );

        response.setJustification(
                request.getJustification()
        );

        response.setAffectedEnvironment(
                request.getAffectedEnvironment()
        );

        response.setRiskAssessment(
                request.getRiskAssessment()
        );

        response.setExpectedResult(
                request.getExpectedResult()
        );

        response.setRollbackPlan(
                request.getRollbackPlan()
        );

        response.setRequestedAt(
                request.getRequestedAt()
        );

        response.setApprovedBy(
                request.getApprovedBy()
        );

        response.setApprovedAt(
                request.getApprovedAt()
        );

        response.setRejectedBy(
                request.getRejectedBy()
        );

        response.setRejectedAt(
                request.getRejectedAt()
        );

        response.setRejectionReason(
                request.getRejectionReason()
        );

        response.setAppliedBy(
                request.getAppliedBy()
        );

        response.setAppliedAt(
                request.getAppliedAt()
        );

        response.setFailureReason(
                request.getFailureReason()
        );

        response.setUpdatedAt(
                request.getUpdatedAt()
        );

        response.setItems(
                items.stream()
                        .map(
                                this::toResponseItem
                        )
                        .toList()
        );

        return response;
    }

    private ConfigurationChangeRequestResponse.Item
    toResponseItem(
            ConfigurationChangeItem item) {

        ConfigurationChangeRequestResponse.Item response =
                new ConfigurationChangeRequestResponse.Item();

        response.setChangeItemId(
                item.getChangeItemId()
        );

        response.setConfigurationKey(
                item.getConfigurationKey()
        );

        response.setConfigurationType(
                item.getConfigurationType()
        );

        response.setEncrypted(
                item.getEncrypted()
        );

        response.setVersionNumber(
                item.getVersionNumber()
        );

        response.setCreatedAt(
                item.getCreatedAt()
        );

        if (!Boolean.TRUE.equals(item.getEncrypted())) {

            response.setPreviousValue(
                    item.getPreviousValue()
            );

            response.setProposedValue(
                    item.getProposedValue()
            );
        }

        return response;
    }

    private void recordRejectedAudit(
            ConfigurationChangeRequest request,
            ActorContext actor,
            SecurityContext securityContext,
            String rejectionReason) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                PERMISSION_APPROVE
        );

        details.put(
                "status",
                STATUS_REJECTED
        );

        details.put(
                "scope",
                request.getTenantId() == null
                        ? "ORGANIZATION"
                        : "TENANT"
        );

        details.put(
                "rejectionReason",
                rejectionReason
        );

        AuditEventRequest auditRequest =
                new AuditEventRequest();

        auditRequest.setOrganizationId(
                actor.organizationId()
        );

        auditRequest.setTenantId(
                request.getTenantId()
        );

        auditRequest.setUserId(
                actor.userId()
        );

        auditRequest.setSessionId(
                securityContext.getSessionId()
        );

        auditRequest.setEventType(
                "SYSTEM_CONFIGURATION_CHANGE"
        );

        auditRequest.setEntityType(
                "CONFIGURATION_CHANGE_REQUEST"
        );

        auditRequest.setEntityId(
                request.getChangeRequestId()
        );

        auditRequest.setAction(
                "REJECT"
        );

        auditRequest.setSourceComponent(
                "ADMINISTRATION"
        );

        auditRequest.setEventResult(
                "REJECTED"
        );

        auditRequest.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        auditRequest
                );
    }

    private AuditEventResponse recordPublishedAudit(
            ConfigurationChangeRequest request,
            ActorContext actor,
            SecurityContext securityContext,
            int itemCount) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                PERMISSION_MANAGE
        );

        details.put(
                "status",
                STATUS_APPLIED
        );

        details.put(
                "scope",
                request.getTenantId() == null
                        ? "ORGANIZATION"
                        : "TENANT"
        );

        details.put(
                "itemCount",
                itemCount
        );

        details.put(
                "catalogVersion",
                definitionCatalog.getCatalogVersion()
        );

        AuditEventRequest auditRequest =
                new AuditEventRequest();

        auditRequest.setOrganizationId(
                actor.organizationId()
        );

        auditRequest.setTenantId(
                request.getTenantId()
        );

        auditRequest.setUserId(
                actor.userId()
        );

        auditRequest.setSessionId(
                securityContext.getSessionId()
        );

        auditRequest.setEventType(
                "SYSTEM_CONFIGURATION_CHANGE"
        );

        auditRequest.setEntityType(
                "CONFIGURATION_CHANGE_REQUEST"
        );

        auditRequest.setEntityId(
                request.getChangeRequestId()
        );

        auditRequest.setAction(
                "PUBLISH"
        );

        auditRequest.setSourceComponent(
                "ADMINISTRATION"
        );

        auditRequest.setEventResult(
                "SUCCESS"
        );

        auditRequest.setEventDetails(
                details
        );

        return auditEventService
                .createAuditEvent(
                        auditRequest
                );
    }

    private void recordPublishedConfigurationChange(
            UUID auditEventId,
            ConfigurationChangeRequest request,
            ConfigurationChangeItem item,
            ActorContext actor) {

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        if (Boolean.TRUE.equals(item.getEncrypted())) {

            previousValue.put(
                    "value",
                    "[REDACTED]"
            );

            currentValue.put(
                    "value",
                    "[REDACTED]"
            );
        }
        else {

            previousValue.put(
                    "value",
                    item.getPreviousValue()
            );

            currentValue.put(
                    "value",
                    item.getProposedValue()
            );
        }

        AuditConfigurationChangeRequest auditRequest =
                new AuditConfigurationChangeRequest();

        auditRequest.setAuditEventId(
                auditEventId
        );

        auditRequest.setConfigurationKey(
                item.getConfigurationKey()
        );

        auditRequest.setPreviousValue(
                previousValue
        );

        auditRequest.setCurrentValue(
                currentValue
        );

        auditRequest.setChangedBy(
                actor.userId()
        );

        auditRequest.setChangeReason(
                request.getJustification()
        );

        auditConfigurationChangeService
                .createAuditConfigurationChange(
                        auditRequest
                );
    }

    private record ActorContext(
            UUID userId,
            UUID organizationId,
            UUID tenantId) {
    }

    private record PreparedItem(
            ConfigurationDefinitionCatalog.Definition definition,
            String previousValue,
            String proposedValue) {
    }

    private record PublicationOutcome(
            String eventResult,
            String errorType) {
    }

    private record PreparedPublication(
            ConfigurationChangeItem item,
            ConfigurationDefinitionCatalog.Definition definition,
            Optional<SystemConfiguration> exactConfiguration,
            int nextVersion) {
    }
}