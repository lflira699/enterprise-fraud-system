package com.efs.modules.detection.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioEvaluationAccessServiceTest {

    @Mock
    private ScenarioEvaluationServiceInterface
            scenarioEvaluationService;

    @Mock
    private ScenarioEvaluationRuleExecutionServiceInterface
            scenarioEvaluationRuleExecutionService;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private ScenarioEvaluationAccessService accessService;

    private UUID userId;
    private UUID organizationId;
    private UUID tenantId;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        tenantId = UUID.randomUUID();

        accessService =
                new ScenarioEvaluationAccessService(
                        scenarioEvaluationService,
                        scenarioEvaluationRuleExecutionService,
                        userAccountLookupService
                );
    }

    @Test
    void parentCreateShouldRequireCreatePermissionAndDelegateAuthorizedScope() {

        ScenarioEvaluationRequest request =
                new ScenarioEvaluationRequest();

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.create"
                        ),
                        tenantId
                );

        stubActor(
                organizationId,
                tenantId
        );

        accessService.createScenarioEvaluation(
                request,
                securityContext
        );

        verify(
                scenarioEvaluationService
        ).createScenarioEvaluation(
                eq(request),
                eq(organizationId),
                eq(tenantId)
        );
    }

    @Test
    void childCreateShouldRequireCreatePermissionAndDelegateAuthorizedScope() {

        ScenarioEvaluationRuleExecutionRequest request =
                new ScenarioEvaluationRuleExecutionRequest();

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.create"
                        ),
                        tenantId
                );

        stubActor(
                organizationId,
                tenantId
        );

        accessService.createScenarioEvaluationRuleExecution(
                request,
                securityContext
        );

        verify(
                scenarioEvaluationRuleExecutionService
        ).createScenarioEvaluationRuleExecution(
                eq(request),
                eq(organizationId),
                eq(tenantId)
        );
    }

    @Test
    void parentReadShouldRequireViewPermissionAndDelegateAuthorizedScope() {

        UUID evaluationId =
                UUID.randomUUID();

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.view"
                        ),
                        tenantId
                );

        stubActor(
                organizationId,
                tenantId
        );

        accessService.getScenarioEvaluationById(
                evaluationId,
                securityContext
        );

        verify(
                scenarioEvaluationService
        ).getScenarioEvaluationById(
                eq(evaluationId),
                eq(organizationId),
                eq(tenantId)
        );
    }

    @Test
    void childReadShouldRequireViewPermissionAndDelegateAuthorizedScope() {

        UUID relationId =
                UUID.randomUUID();

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.view"
                        ),
                        tenantId
                );

        stubActor(
                organizationId,
                tenantId
        );

        accessService.getScenarioEvaluationRuleExecutionById(
                relationId,
                securityContext
        );

        verify(
                scenarioEvaluationRuleExecutionService
        ).getScenarioEvaluationRuleExecutionById(
                eq(relationId),
                eq(organizationId),
                eq(tenantId)
        );
    }

    @Test
    void missingCreatePermissionShouldFailBeforeCoreDelegation() {

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.view"
                        ),
                        tenantId
                );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.createScenarioEvaluation(
                        new ScenarioEvaluationRequest(),
                        securityContext
                )
        );

        verifyNoInteractions(
                scenarioEvaluationService,
                scenarioEvaluationRuleExecutionService,
                userAccountLookupService
        );
    }

    @Test
    void missingViewPermissionShouldFailBeforeCoreDelegation() {

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.create"
                        ),
                        tenantId
                );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.getScenarioEvaluationById(
                        UUID.randomUUID(),
                        securityContext
                )
        );

        verifyNoInteractions(
                scenarioEvaluationService,
                scenarioEvaluationRuleExecutionService,
                userAccountLookupService
        );
    }

    @Test
    void authenticatedTenantMismatchShouldFailClosed() {

        UUID contextTenantId =
                UUID.randomUUID();

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.view"
                        ),
                        contextTenantId
                );

        stubActor(
                organizationId,
                tenantId
        );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.getScenarioEvaluationById(
                        UUID.randomUUID(),
                        securityContext
                )
        );

        verifyNoInteractions(
                scenarioEvaluationService,
                scenarioEvaluationRuleExecutionService
        );
    }

    @Test
    void organizationLevelActorShouldDelegateNullTenantScope() {

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.view"
                        ),
                        null
                );

        stubActor(
                organizationId,
                null
        );

        UUID evaluationId =
                UUID.randomUUID();

        accessService.getScenarioEvaluationById(
                evaluationId,
                securityContext
        );

        verify(
                scenarioEvaluationService
        ).getScenarioEvaluationById(
                eq(evaluationId),
                eq(organizationId),
                isNull()
        );
    }

    @Test
    void missingAuthorizedOrganizationShouldFailClosed() {

        SecurityContext securityContext =
                securityContext(
                        Set.of(
                                "scenario.evaluation.view"
                        ),
                        tenantId
                );

        stubActor(
                null,
                tenantId
        );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.getScenarioEvaluationById(
                        UUID.randomUUID(),
                        securityContext
                )
        );

        verifyNoInteractions(
                scenarioEvaluationService,
                scenarioEvaluationRuleExecutionService
        );
    }

    private SecurityContext securityContext(
            Set<String> permissions,
            UUID contextTenantId) {

        return new SecurityContext(
                userId,
                contextTenantId,
                UUID.randomUUID(),
                Set.of(),
                permissions,
                Set.of()
        );
    }

    private void stubActor(
            UUID actorOrganizationId,
            UUID actorTenantId) {

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        )
        ).thenReturn(
                new UserAccountReference(
                        userId,
                        actorOrganizationId,
                        actorTenantId,
                        "scenario.evaluation.security@example.com"
                )
        );
    }
}
