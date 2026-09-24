package com.efs.modules.detection.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ScenarioEvaluationAccessService
        implements ScenarioEvaluationAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "scenario.evaluation.view";

    private static final String CREATE_PERMISSION =
            "scenario.evaluation.create";

    private final ScenarioEvaluationServiceInterface
            scenarioEvaluationService;

    private final ScenarioEvaluationRuleExecutionServiceInterface
            scenarioEvaluationRuleExecutionService;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    public ScenarioEvaluationAccessService(
            ScenarioEvaluationServiceInterface scenarioEvaluationService,
            ScenarioEvaluationRuleExecutionServiceInterface
                    scenarioEvaluationRuleExecutionService,
            UserAccountLookupServiceInterface userAccountLookupService) {

        this.scenarioEvaluationService =
                scenarioEvaluationService;

        this.scenarioEvaluationRuleExecutionService =
                scenarioEvaluationRuleExecutionService;

        this.userAccountLookupService =
                userAccountLookupService;
    }

    @Override
    @Transactional
    public ScenarioEvaluationResponse createScenarioEvaluation(
            ScenarioEvaluationRequest request,
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

        return scenarioEvaluationService
                .createScenarioEvaluation(
                        request,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public ScenarioEvaluationResponse getScenarioEvaluationById(
            UUID evaluationId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getScenarioEvaluationById(
                        evaluationId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationResponse>
    getEvaluationsByScenario(
            UUID scenarioId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getEvaluationsByScenario(
                        scenarioId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationResponse>
    getEvaluationsByScenarioVersion(
            UUID scenarioVersionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getEvaluationsByScenarioVersion(
                        scenarioVersionId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationResponse>
    getEvaluationsByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getEvaluationsByTransaction(
                        transactionId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationResponse>
    getEvaluationsByCustomer(
            UUID customerId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getEvaluationsByCustomer(
                        customerId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationResponse>
    getEvaluationsByStatus(
            String evaluationStatus,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getEvaluationsByStatus(
                        evaluationStatus,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationResponse>
    getEvaluationsByMatched(
            Boolean matched,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationService
                .getEvaluationsByMatched(
                        matched,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    @Transactional
    public ScenarioEvaluationRuleExecutionResponse
    createScenarioEvaluationRuleExecution(
            ScenarioEvaluationRuleExecutionRequest request,
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

        return scenarioEvaluationRuleExecutionService
                .createScenarioEvaluationRuleExecution(
                        request,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public ScenarioEvaluationRuleExecutionResponse
    getScenarioEvaluationRuleExecutionById(
            UUID evaluationRuleExecutionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationRuleExecutionService
                .getScenarioEvaluationRuleExecutionById(
                        evaluationRuleExecutionId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationRuleExecutionResponse>
    getRuleExecutionsByEvaluation(
            UUID evaluationId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationRuleExecutionService
                .getRuleExecutionsByEvaluation(
                        evaluationId,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }

    @Override
    public List<ScenarioEvaluationRuleExecutionResponse>
    getEvaluationsByRuleExecution(
            UUID executionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        return scenarioEvaluationRuleExecutionService
                .getEvaluationsByRuleExecution(
                        executionId,
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