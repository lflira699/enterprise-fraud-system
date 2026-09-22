package com.efs.modules.rules.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.rules.dto.RuleActivationRequest;
import com.efs.modules.rules.dto.RuleDeactivationRequest;
import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.dto.RuleTestingRequest;
import com.efs.modules.rules.dto.RuleUpdateRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleAccessServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "00000000-0000-0000-0000-000000000901"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "00000000-0000-0000-0000-000000000902"
            );

    private static final UUID RULE_ID =
            UUID.fromString(
                    "00000000-0000-0000-0000-000000000903"
            );

    private static final UUID RULE_VERSION_ID =
            UUID.fromString(
                    "00000000-0000-0000-0000-000000000904"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "00000000-0000-0000-0000-000000000905"
            );

    @Mock
    private RuleServiceInterface
            ruleService;

    @Mock
    private RuleTestingService
            ruleTestingService;

    @Mock
    private RuleAuthorizationServiceInterface
            ruleAuthorizationService;

    private RuleAccessService
            accessService;

    private SecurityContext
            securityContext;

    private UserAccountReference
            actor;

    @BeforeEach
    void setUp() {

        accessService =
                new RuleAccessService(
                        ruleService,
                        ruleTestingService,
                        ruleAuthorizationService
                );

        securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of(),
                        Set.of()
                );

        actor =
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        null,
                        "rule-access@example.com"
                );
    }

    @Test
    void shouldAuthorizeCreateAndDelegate() {

        RuleRequest request =
                mock(RuleRequest.class);

        RuleResponse response =
                mock(RuleResponse.class);

        when(
                ruleService.createRule(
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.createRule(
                        request,
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.create"
        );
    }

    @Test
    void shouldAuthorizeListAndDelegate() {

        List<RuleResponse> response =
                List.of(
                        mock(RuleResponse.class)
                );

        when(
                ruleService.getRules()
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getRules(
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.view"
        );
    }

    @Test
    void shouldAuthorizeGetByIdAndDelegate() {

        RuleResponse response =
                mock(RuleResponse.class);

        when(
                ruleService.getRuleById(
                        RULE_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getRuleById(
                        RULE_ID,
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.view"
        );
    }

    @Test
    void shouldAuthorizeGetByCodeAndDelegate() {

        RuleResponse response =
                mock(RuleResponse.class);

        when(
                ruleService.getRuleByCode(
                        "RULE-001"
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getRuleByCode(
                        "RULE-001",
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.view"
        );
    }

    @Test
    void shouldAuthorizeStatusFilterAndDelegate() {

        List<RuleResponse> response =
                List.of();

        when(
                ruleService.getRulesByStatus(
                        "ACTIVE"
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getRulesByStatus(
                        "ACTIVE",
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.view"
        );
    }

    @Test
    void shouldAuthorizeCategoryFilterAndDelegate() {

        List<RuleResponse> response =
                List.of();

        when(
                ruleService.getRulesByCategory(
                        "ATO"
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getRulesByCategory(
                        "ATO",
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.view"
        );
    }

    @Test
    void shouldAuthorizeSeverityFilterAndDelegate() {

        List<RuleResponse> response =
                List.of();

        when(
                ruleService.getRulesBySeverity(
                        "HIGH"
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getRulesBySeverity(
                        "HIGH",
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).authorize(
                securityContext,
                "rule.view"
        );
    }

    @Test
    void shouldAuthorizeActorAndDelegateUpdate() {

        RuleUpdateRequest request =
                mock(RuleUpdateRequest.class);

        RuleVersionResponse response =
                mock(RuleVersionResponse.class);

        when(
                request.getChangedBy()
        ).thenReturn(
                USER_ID
        );

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.update"
                )
        ).thenReturn(
                actor
        );

        when(
                ruleService.updateRule(
                        RULE_ID,
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.updateRule(
                        RULE_ID,
                        request,
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).requireActor(
                actor,
                USER_ID,
                "changedBy"
        );
    }

    @Test
    void shouldAuthorizeActorAndDelegateActivation() {

        RuleActivationRequest request =
                mock(RuleActivationRequest.class);

        RuleResponse response =
                mock(RuleResponse.class);

        when(
                request.getChangedBy()
        ).thenReturn(
                USER_ID
        );

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.activate"
                )
        ).thenReturn(
                actor
        );

        when(
                ruleService.activateRule(
                        RULE_ID,
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.activateRule(
                        RULE_ID,
                        request,
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).requireActor(
                actor,
                USER_ID,
                "changedBy"
        );
    }

    @Test
    void shouldAuthorizeActorAndDelegateDeactivation() {

        RuleDeactivationRequest request =
                mock(RuleDeactivationRequest.class);

        RuleResponse response =
                mock(RuleResponse.class);

        when(
                request.getChangedBy()
        ).thenReturn(
                USER_ID
        );

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.deactivate"
                )
        ).thenReturn(
                actor
        );

        when(
                ruleService.deactivateRule(
                        RULE_ID,
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.deactivateRule(
                        RULE_ID,
                        request,
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).requireActor(
                actor,
                USER_ID,
                "changedBy"
        );
    }

    @Test
    void shouldAuthorizeActorAndDelegateRuleTest() {

        RuleTestingRequest request =
                mock(RuleTestingRequest.class);

        RuleSimulationResponse response =
                mock(RuleSimulationResponse.class);

        when(
                request.getExecutedBy()
        ).thenReturn(
                USER_ID
        );

        when(
                request.getSimulationName()
        ).thenReturn(
                "baseline"
        );

        when(
                request.getDatasetReference()
        ).thenReturn(
                "synthetic-dataset"
        );

        when(
                request.getCorrelationId()
        ).thenReturn(
                CORRELATION_ID
        );

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.test"
                )
        ).thenReturn(
                actor
        );

        when(
                ruleTestingService.execute(
                        RULE_ID,
                        RULE_VERSION_ID,
                        "baseline",
                        "synthetic-dataset",
                        USER_ID,
                        CORRELATION_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.testRule(
                        RULE_ID,
                        RULE_VERSION_ID,
                        request,
                        securityContext
                )
        );

        verify(
                ruleAuthorizationService
        ).requireActor(
                actor,
                USER_ID,
                "executedBy"
        );
    }

    @Test
    void shouldNotInvokeTrustedServiceWhenAuthorizationFails() {

        RuleRequest request =
                mock(RuleRequest.class);

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.create"
                )
        ).thenThrow(
                new AccessDeniedException(
                        "denied"
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.createRule(
                        request,
                        securityContext
                )
        );

        verifyNoInteractions(
                ruleService
        );
    }

    @Test
    void shouldNotUpdateWhenActorIntegrityFails() {

        RuleUpdateRequest request =
                mock(RuleUpdateRequest.class);

        UUID forgedActor =
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000999"
                );

        when(
                request.getChangedBy()
        ).thenReturn(
                forgedActor
        );

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.update"
                )
        ).thenReturn(
                actor
        );

        doThrow(
                new AccessDeniedException(
                        "actor mismatch"
                )
        ).when(
                ruleAuthorizationService
        ).requireActor(
                actor,
                forgedActor,
                "changedBy"
        );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.updateRule(
                        RULE_ID,
                        request,
                        securityContext
                )
        );

        verifyNoInteractions(
                ruleService
        );
    }

    @Test
    void shouldNotExecuteRuleTestWhenActorIntegrityFails() {

        RuleTestingRequest request =
                mock(RuleTestingRequest.class);

        UUID forgedActor =
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000998"
                );

        when(
                request.getExecutedBy()
        ).thenReturn(
                forgedActor
        );

        when(
                ruleAuthorizationService.authorize(
                        securityContext,
                        "rule.test"
                )
        ).thenReturn(
                actor
        );

        doThrow(
                new AccessDeniedException(
                        "actor mismatch"
                )
        ).when(
                ruleAuthorizationService
        ).requireActor(
                actor,
                forgedActor,
                "executedBy"
        );

        assertThrows(
                AccessDeniedException.class,
                () -> accessService.testRule(
                        RULE_ID,
                        RULE_VERSION_ID,
                        request,
                        securityContext
                )
        );

        verifyNoInteractions(
                ruleTestingService
        );
    }
}