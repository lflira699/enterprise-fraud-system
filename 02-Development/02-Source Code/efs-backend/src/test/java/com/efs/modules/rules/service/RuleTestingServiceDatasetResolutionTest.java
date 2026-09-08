package com.efs.modules.rules.service;

import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.entity.RuleCondition;
import com.efs.modules.rules.entity.RuleSimulation;
import com.efs.modules.rules.entity.RuleVersion;
import com.efs.modules.rules.mapper.RuleSimulationMapper;
import com.efs.modules.rules.repository.RuleConditionRepository;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.modules.rules.repository.RuleSimulationRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
import com.efs.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RuleTestingServiceDatasetResolutionTest {

    @Test
    void shouldResolveControlledDatasetBeforeRuleEvaluation() {

        RuleRepository ruleRepository =
                mock(
                        RuleRepository.class
                );

        RuleVersionRepository ruleVersionRepository =
                mock(
                        RuleVersionRepository.class
                );

        RuleConditionRepository ruleConditionRepository =
                mock(
                        RuleConditionRepository.class
                );

        RuleSimulationRepository ruleSimulationRepository =
                mock(
                        RuleSimulationRepository.class
                );

        RuleSimulationMapper ruleSimulationMapper =
                mock(
                        RuleSimulationMapper.class
                );

        RuleVersionDatasetEvaluator datasetEvaluator =
                mock(
                        RuleVersionDatasetEvaluator.class
                );

        RuleTestDatasetResolver datasetResolver =
                mock(
                        RuleTestDatasetResolver.class
                );

        AuditEventServiceInterface auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        RuleTestingAuditService ruleTestingAuditService =
                mock(
                        RuleTestingAuditService.class
                );

        RuleTestingService service =
                new RuleTestingService(
                        ruleRepository,
                        ruleVersionRepository,
                        ruleConditionRepository,
                        ruleSimulationRepository,
                        ruleSimulationMapper,
                        datasetEvaluator,
                        datasetResolver,
                        auditEventService,
                        ruleTestingAuditService
                );

        UUID ruleId =
                UUID.randomUUID();

        UUID ruleVersionId =
                UUID.randomUUID();

        UUID executedBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID simulationId =
                UUID.randomUUID();

        List<Map<String, Object>> records =
                List.of(
                        Map.of(
                                "transaction.amount",
                                7500
                        ),
                        Map.of(
                                "transaction.amount",
                                2500
                        )
                );

        RuleTestDataset dataset =
                new RuleTestDataset(
                        "dataset://resolved/uc025",
                        records
                );

        Rule rule =
                mock(
                        Rule.class
                );

        RuleVersion ruleVersion =
                mock(
                        RuleVersion.class
                );

        RuleCondition condition =
                mock(
                        RuleCondition.class
                );

        RuleSimulation savedSimulation =
                mock(
                        RuleSimulation.class
                );

        RuleSimulationResponse expectedResponse =
                mock(
                        RuleSimulationResponse.class
                );

        RuleTestDatasetEvaluation evaluation =
                new RuleTestDatasetEvaluation(
                        "dataset://resolved/uc025",
                        2L,
                        1L,
                        1L
                );

        when(
                datasetResolver.resolve(
                        "dataset://requested/uc025"
                )
        ).thenReturn(
                dataset
        );

        when(
                ruleRepository.findByRuleId(
                        ruleId
                )
        ).thenReturn(
                Optional.of(
                        rule
                )
        );

        when(
                rule.getRuleId()
        ).thenReturn(
                ruleId
        );

        when(
                ruleVersionRepository
                        .findByRuleVersionId(
                                ruleVersionId
                        )
        ).thenReturn(
                Optional.of(
                        ruleVersion
                )
        );

        when(
                ruleVersion.getRuleId()
        ).thenReturn(
                ruleId
        );

        when(
                ruleConditionRepository
                        .findByRuleVersionIdOrderByConditionOrderAsc(
                                ruleVersionId
                        )
        ).thenReturn(
                List.of(
                        condition
                )
        );

        when(
                datasetEvaluator.evaluate(
                        ruleVersionId,
                        "dataset://resolved/uc025",
                        records
                )
        ).thenReturn(
                evaluation
        );

        when(
                ruleSimulationRepository.save(
                        any(
                                RuleSimulation.class
                        )
                )
        ).thenReturn(
                savedSimulation
        );

        when(
                savedSimulation.getSimulationId()
        ).thenReturn(
                simulationId
        );

        when(
                ruleSimulationMapper.toResponse(
                        savedSimulation
                )
        ).thenReturn(
                expectedResponse
        );

        RuleSimulationResponse result =
                service.execute(
                        ruleId,
                        ruleVersionId,
                        "Controlled Dataset Test",
                        "dataset://requested/uc025",
                        executedBy,
                        correlationId
                );

        assertSame(
                expectedResponse,
                result
        );

        verify(
                datasetResolver
        ).resolve(
                "dataset://requested/uc025"
        );

        verify(
                datasetEvaluator
        ).evaluate(
                ruleVersionId,
                "dataset://resolved/uc025",
                records
        );

        verify(
                ruleSimulationRepository
        ).save(
                any(
                        RuleSimulation.class
                )
        );
    }

    @Test
    void shouldRejectMissingRuleBeforeResolvingDataset() {

        RuleRepository ruleRepository =
                mock(
                        RuleRepository.class
                );

        RuleVersionRepository ruleVersionRepository =
                mock(
                        RuleVersionRepository.class
                );

        RuleConditionRepository ruleConditionRepository =
                mock(
                        RuleConditionRepository.class
                );

        RuleSimulationRepository ruleSimulationRepository =
                mock(
                        RuleSimulationRepository.class
                );

        RuleSimulationMapper ruleSimulationMapper =
                mock(
                        RuleSimulationMapper.class
                );

        RuleVersionDatasetEvaluator datasetEvaluator =
                mock(
                        RuleVersionDatasetEvaluator.class
                );

        RuleTestDatasetResolver datasetResolver =
                mock(
                        RuleTestDatasetResolver.class
                );

        AuditEventServiceInterface auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        RuleTestingAuditService ruleTestingAuditService =
                mock(
                        RuleTestingAuditService.class
                );

        RuleTestingService service =
                new RuleTestingService(
                        ruleRepository,
                        ruleVersionRepository,
                        ruleConditionRepository,
                        ruleSimulationRepository,
                        ruleSimulationMapper,
                        datasetEvaluator,
                        datasetResolver,
                        auditEventService,
                        ruleTestingAuditService
                );

        UUID ruleId =
                UUID.randomUUID();

        UUID ruleVersionId =
                UUID.randomUUID();

        UUID executedBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        when(
                ruleRepository.findByRuleId(
                        ruleId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                com.efs.shared.exception.ResourceNotFoundException.class,
                () ->
                        service.execute(
                                ruleId,
                                ruleVersionId,
                                "Missing Rule Test",
                                "dataset://must-not-resolve/uc025",
                                executedBy,
                                correlationId
                        )
        );

        verify(
                ruleRepository
        ).findByRuleId(
                ruleId
        );

        verifyNoInteractions(
                ruleVersionRepository,
                ruleConditionRepository,
                datasetResolver,
                ruleSimulationRepository,
                ruleSimulationMapper,
                datasetEvaluator,
                auditEventService,
                ruleTestingAuditService
        );
    }
}
