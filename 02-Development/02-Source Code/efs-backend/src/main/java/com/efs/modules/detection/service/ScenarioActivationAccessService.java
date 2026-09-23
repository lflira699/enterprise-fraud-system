package com.efs.modules.detection.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ScenarioActivationAccessService
        implements ScenarioActivationAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "scenario.activation.view";

    private static final String CREATE_PERMISSION =
            "scenario.activation.create";

    private final ScenarioActivationServiceInterface
            scenarioActivationService;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    public ScenarioActivationAccessService(
            ScenarioActivationServiceInterface scenarioActivationService,
            UserAccountLookupServiceInterface userAccountLookupService) {

        this.scenarioActivationService =
                scenarioActivationService;

        this.userAccountLookupService =
                userAccountLookupService;
    }

    @Override
    @Transactional
    public ScenarioActivationResponse createScenarioActivation(
            ScenarioActivationRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        UserAccountReference actor =
                authorize(
                        securityContext,
                        CREATE_PERMISSION
                );

        return scenarioActivationService
                .createScenarioActivation(
                        request,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public ScenarioActivationResponse getScenarioActivationById(
            UUID activationId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getScenarioActivationById(
                        activationId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioActivationResponse>
    getActivationsByScenario(
            UUID scenarioId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getActivationsByScenario(
                        scenarioId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioActivationResponse>
    getActivationsByScenarioVersion(
            UUID scenarioVersionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getActivationsByScenarioVersion(
                        scenarioVersionId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioActivationResponse>
    getActivationsByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getActivationsByTransaction(
                        transactionId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioActivationResponse>
    getActivationsByCustomer(
            UUID customerId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getActivationsByCustomer(
                        customerId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioActivationResponse>
    getActivationsByStatus(
            String activationStatus,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getActivationsByStatus(
                        activationStatus,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioActivationResponse>
    getActivationsBySeverity(
            String severity,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioActivationService
                .getActivationsBySeverity(
                        severity,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    private UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Objects.requireNonNull(
                requiredPermission,
                "requiredPermission is required"
        );

        if (!securityContext.hasPermission(
                requiredPermission
        )) {

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + requiredPermission
            );
        }

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(
                                securityContext
                                        .getUserId()
                        );

        if (
            actor == null
                    ||
            actor.organizationId() == null
        ) {

            throw new AccessDeniedException(
                    "Authorized organization scope is not available"
            );
        }

        if (!Objects.equals(
                securityContext.getTenantId(),
                actor.tenantId()
        )) {

            throw new AccessDeniedException(
                    "Authenticated tenant scope mismatch"
            );
        }

        return actor;
    }
}