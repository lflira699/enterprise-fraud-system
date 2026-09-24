package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import com.efs.modules.detection.mapper.ScenarioEvaluationRuleExecutionMapper;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
import com.efs.modules.detection.repository.ScenarioEvaluationRuleExecutionRepository;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.modules.rules.service.RuleExecutionServiceInterface;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioEvaluationRuleExecutionScopeTest {

    @Mock
    private ScenarioEvaluationRuleExecutionRepository
            repository;

    @Mock
    private ScenarioEvaluationRuleExecutionMapper
            mapper;

    @Mock
    private ScenarioEvaluationRepository
            scenarioEvaluationRepository;

    @Mock
    private RuleExecutionServiceInterface
            ruleExecutionService;

    @Mock
    private TransactionServiceInterface
            transactionService;

    private ScenarioEvaluationRuleExecutionService
            service;

    @BeforeEach
    void setUp() {

        service =
                new ScenarioEvaluationRuleExecutionService(
                        repository,
                        mapper,
                        scenarioEvaluationRepository,
                        ruleExecutionService,
                        transactionService
                );

    }

    @Test
    void childCreateShouldAllowMatchingParentAndRuleExecutionScope() {

        stubSuccessfulCreate();

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID executionTransactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        stubVisibleParent(
                evaluationId,
                UUID.randomUUID(),
                organizationId,
                tenantId
        );

        stubRuleExecutionTransaction(
                executionId,
                executionTransactionId,
                organizationId,
                tenantId
        );

        service.createScenarioEvaluationRuleExecution(
                request(
                        evaluationId,
                        executionId
                ),
                organizationId,
                tenantId
        );

        verify(
                repository
        ).save(
                any(ScenarioEvaluationRuleExecution.class)
        );
    }

    @Test
    void hiddenParentShouldRejectBeforeRuleExecutionResolution() {

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                scenarioEvaluationRepository
                        .findScopedByEvaluationId(
                                evaluationId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createScenarioEvaluationRuleExecution(
                        request(
                                evaluationId,
                                executionId
                        ),
                        organizationId,
                        tenantId
                )
        );

        verify(
                ruleExecutionService,
                never()
        ).getRuleExecutionById(any());

        verify(
                repository,
                never()
        ).save(any());
    }

    @Test
    void crossOrganizationRuleExecutionShouldRejectBeforeSave() {

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID executionTransactionId = UUID.randomUUID();
        UUID parentOrganizationId = UUID.randomUUID();
        UUID executionOrganizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        stubVisibleParent(
                evaluationId,
                UUID.randomUUID(),
                parentOrganizationId,
                tenantId
        );

        stubRuleExecutionTransaction(
                executionId,
                executionTransactionId,
                executionOrganizationId,
                tenantId
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createScenarioEvaluationRuleExecution(
                        request(
                                evaluationId,
                                executionId
                        ),
                        parentOrganizationId,
                        tenantId
                )
        );

        verify(
                repository,
                never()
        ).save(any());
    }

    @Test
    void crossTenantRuleExecutionShouldRejectBeforeSave() {

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID executionTransactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID parentTenantId = UUID.randomUUID();
        UUID executionTenantId = UUID.randomUUID();

        stubVisibleParent(
                evaluationId,
                UUID.randomUUID(),
                organizationId,
                parentTenantId
        );

        stubRuleExecutionTransaction(
                executionId,
                executionTransactionId,
                organizationId,
                executionTenantId
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createScenarioEvaluationRuleExecution(
                        request(
                                evaluationId,
                                executionId
                        ),
                        organizationId,
                        parentTenantId
                )
        );

        verify(
                repository,
                never()
        ).save(any());
    }

    @Test
    void ruleExecutionWithoutTransactionShouldFailClosedBeforeSave() {

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        stubVisibleParent(
                evaluationId,
                UUID.randomUUID(),
                organizationId,
                tenantId
        );

        RuleExecutionResponse response =
                new RuleExecutionResponse();

        response.setExecutionId(
                executionId
        );

        response.setTransactionId(
                null
        );

        when(
                ruleExecutionService.getRuleExecutionById(
                        executionId
                )
        ).thenReturn(
                response
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.createScenarioEvaluationRuleExecution(
                        request(
                                evaluationId,
                                executionId
                        ),
                        organizationId,
                        tenantId
                )
        );

        verify(
                repository,
                never()
        ).save(any());
    }

    @Test
    void ruleExecutionTransactionWithoutOrganizationShouldFailClosedBeforeSave() {

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID executionTransactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        stubVisibleParent(
                evaluationId,
                UUID.randomUUID(),
                organizationId,
                tenantId
        );

        RuleExecutionResponse ruleExecution =
                new RuleExecutionResponse();

        ruleExecution.setExecutionId(
                executionId
        );

        ruleExecution.setTransactionId(
                executionTransactionId
        );

        when(
                ruleExecutionService.getRuleExecutionById(
                        executionId
                )
        ).thenReturn(
                ruleExecution
        );

        TransactionResponse transaction =
                new TransactionResponse();

        transaction.setTransactionId(
                executionTransactionId
        );

        transaction.setOrganizationId(
                null
        );

        transaction.setTenantId(
                tenantId
        );

        when(
                transactionService.getTransactionById(
                        executionTransactionId
                )
        ).thenReturn(
                transaction
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.createScenarioEvaluationRuleExecution(
                        request(
                                evaluationId,
                                executionId
                        ),
                        organizationId,
                        tenantId
                )
        );

        verify(
                repository,
                never()
        ).save(any());
    }

    @Test
    void differentTransactionIdsWithinSameScopeShouldRemainAllowed() {

        stubSuccessfulCreate();

        UUID evaluationId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        UUID parentTransactionId = UUID.randomUUID();
        UUID executionTransactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        stubVisibleParent(
                evaluationId,
                parentTransactionId,
                organizationId,
                tenantId
        );

        stubRuleExecutionTransaction(
                executionId,
                executionTransactionId,
                organizationId,
                tenantId
        );

        service.createScenarioEvaluationRuleExecution(
                request(
                        evaluationId,
                        executionId
                ),
                organizationId,
                tenantId
        );

        verify(
                repository
        ).save(
                any(ScenarioEvaluationRuleExecution.class)
        );
    }

    private void stubSuccessfulCreate() {

        when(
                mapper.toEntity(
                        any(ScenarioEvaluationRuleExecutionRequest.class)
                )
        ).thenReturn(
                new ScenarioEvaluationRuleExecution()
        );

        when(
                repository.save(
                        any(ScenarioEvaluationRuleExecution.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        when(
                mapper.toResponse(
                        any(ScenarioEvaluationRuleExecution.class)
                )
        ).thenReturn(
                new ScenarioEvaluationRuleExecutionResponse()
        );
    }

    private void stubVisibleParent(
            UUID evaluationId,
            UUID transactionId,
            UUID organizationId,
            UUID tenantId) {

        ScenarioEvaluation parent =
                new ScenarioEvaluation();

        parent.setEvaluationId(
                evaluationId
        );

        parent.setTransactionId(
                transactionId
        );

        parent.setOrganizationId(
                organizationId
        );

        parent.setTenantId(
                tenantId
        );

        when(
                scenarioEvaluationRepository
                        .findScopedByEvaluationId(
                                evaluationId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(parent)
        );
    }

    private void stubRuleExecutionTransaction(
            UUID executionId,
            UUID transactionId,
            UUID organizationId,
            UUID tenantId) {

        RuleExecutionResponse ruleExecution =
                new RuleExecutionResponse();

        ruleExecution.setExecutionId(
                executionId
        );

        ruleExecution.setTransactionId(
                transactionId
        );

        when(
                ruleExecutionService.getRuleExecutionById(
                        executionId
                )
        ).thenReturn(
                ruleExecution
        );

        TransactionResponse transaction =
                new TransactionResponse();

        transaction.setTransactionId(
                transactionId
        );

        transaction.setOrganizationId(
                organizationId
        );

        transaction.setTenantId(
                tenantId
        );

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transaction
        );
    }

    private ScenarioEvaluationRuleExecutionRequest request(
            UUID evaluationId,
            UUID executionId) {

        ScenarioEvaluationRuleExecutionRequest request =
                new ScenarioEvaluationRuleExecutionRequest();

        request.setEvaluationId(
                evaluationId
        );

        request.setExecutionId(
                executionId
        );

        return request;
    }
}
