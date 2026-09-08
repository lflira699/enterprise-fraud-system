package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEventRequest;
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
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RuleTestingService {

    private static final String ENTITY_TYPE_RULE_VERSION =
            "RULE_VERSION";

    private static final String SIMULATION_STATUS_COMPLETED =
            "COMPLETED";

    private static final String SIMULATION_SOURCE_CONTROLLED_RULE_TEST =
            "CONTROLLED_RULE_TEST";

    private final RuleRepository ruleRepository;

    private final RuleVersionRepository ruleVersionRepository;

    private final RuleConditionRepository ruleConditionRepository;

    private final RuleSimulationRepository ruleSimulationRepository;

    private final RuleSimulationMapper ruleSimulationMapper;

    private final RuleVersionDatasetEvaluator datasetEvaluator;

    private final RuleTestDatasetResolver datasetResolver;

    private final AuditEventServiceInterface auditEventService;

    private final RuleTestingAuditService ruleTestingAuditService;

    public RuleTestingService(
            RuleRepository ruleRepository,
            RuleVersionRepository ruleVersionRepository,
            RuleConditionRepository ruleConditionRepository,
            RuleSimulationRepository ruleSimulationRepository,
            RuleSimulationMapper ruleSimulationMapper,
            RuleVersionDatasetEvaluator datasetEvaluator,
            RuleTestDatasetResolver datasetResolver,
            AuditEventServiceInterface auditEventService,
            RuleTestingAuditService ruleTestingAuditService) {

        this.ruleRepository =
                ruleRepository;

        this.ruleVersionRepository =
                ruleVersionRepository;

        this.ruleConditionRepository =
                ruleConditionRepository;

        this.ruleSimulationRepository =
                ruleSimulationRepository;

        this.ruleSimulationMapper =
                ruleSimulationMapper;

        this.datasetEvaluator =
                datasetEvaluator;

        this.datasetResolver =
                datasetResolver;

        this.auditEventService =
                auditEventService;

        this.ruleTestingAuditService =
                ruleTestingAuditService;
    }

    /**
     * Canonical UC-025 execution entry point.
     *
     * The caller identifies an EFS-controlled dataset by reference.
     * Raw evaluation facts are resolved internally and are not part
     * of the external Rule Testing contract.
     */
    @Transactional
    public RuleSimulationResponse execute(
            UUID ruleId,
            UUID ruleVersionId,
            String simulationName,
            String datasetReference,
            UUID executedBy,
            UUID correlationId) {

        validateRequest(
                ruleId,
                ruleVersionId,
                simulationName,
                executedBy
        );

        validateRuleBeforeDatasetResolution(
                ruleId,
                ruleVersionId,
                executedBy,
                correlationId
        );

        RuleTestDataset dataset =
                datasetResolver.resolve(
                        datasetReference
                );

        return execute(
                ruleId,
                ruleVersionId,
                simulationName,
                dataset.getDatasetReference(),
                dataset.getRecords(),
                executedBy,
                correlationId
        );
    }

    /**
     * Internal normalized-dataset execution path.
     *
     * This overload exists so evaluation and persistence can operate
     * on already normalized facts. Controllers must use the canonical
     * dataset-reference overload instead.
     */    @Transactional
    RuleSimulationResponse execute(
            UUID ruleId,
            UUID ruleVersionId,
            String simulationName,
            String datasetReference,
            List<Map<String, Object>> records,
            UUID executedBy,
            UUID correlationId) {

        validateRequest(
                ruleId,
                ruleVersionId,
                simulationName,
                executedBy
        );

        Rule rule =
                ruleRepository
                        .findByRuleId(
                                ruleId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule not found: "
                                                + ruleId
                                )
                        );

        RuleVersion ruleVersion =
                ruleVersionRepository
                        .findByRuleVersionId(
                                ruleVersionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule version not found: "
                                                + ruleVersionId
                                )
                        );

        if (!rule.getRuleId().equals(
                ruleVersion.getRuleId()
        )) {

            throw new ValidationException(
                    "Rule version does not belong "
                            + "to the requested rule"
            );
        }

        if (ruleConditionRepository
                .findByRuleVersionIdOrderByConditionOrderAsc(
                        ruleVersionId
                )
                .isEmpty()) {

            ValidationException rejection =
                    new ValidationException(
                            "Rule version is not available "
                                    + "for testing because it "
                                    + "has no conditions"
                    );

            try {
                ruleTestingAuditService.recordRejected(
                        ruleId,
                        ruleVersionId,
                        executedBy,
                        correlationId,
                        "RULE_VERSION_HAS_NO_CONDITIONS"
                );
            } catch (RuntimeException auditFailure) {
                rejection.addSuppressed(
                        auditFailure
                );
            }

            throw rejection;
        }

        LocalDateTime startedAt =
                LocalDateTime.now();

        RuleTestDatasetEvaluation evaluation;

        try {

            evaluation =
                    datasetEvaluator.evaluate(
                            ruleVersionId,
                            datasetReference,
                            records
                    );

        } catch (RuntimeException executionFailure) {

            try {
                ruleTestingAuditService.recordFailure(
                        ruleId,
                        ruleVersionId,
                        datasetReference,
                        executedBy,
                        correlationId,
                        executionFailure
                );
            } catch (RuntimeException auditFailure) {
                executionFailure.addSuppressed(
                        auditFailure
                );
            }

            throw executionFailure;
        }

        LocalDateTime completedAt =
                LocalDateTime.now();

        Map<String, Object> resultSummary =
                buildResultSummary(
                        ruleId,
                        ruleVersionId,
                        evaluation
                );

        RuleSimulation simulation =
                new RuleSimulation();

        simulation.setSimulationName(
                simulationName.trim()
        );

        simulation.setEntityType(
                ENTITY_TYPE_RULE_VERSION
        );

        simulation.setEntityId(
                ruleVersionId
        );

        simulation.setDatasetReference(
                evaluation.getDatasetReference()
        );

        simulation.setSampleSize(
                evaluation.getSampleSize()
        );

        simulation.setStartedAt(
                startedAt
        );

        simulation.setCompletedAt(
                completedAt
        );

        simulation.setSimulationStatus(
                SIMULATION_STATUS_COMPLETED
        );

        simulation.setSimulationSource(
                SIMULATION_SOURCE_CONTROLLED_RULE_TEST
        );

        simulation.setMatchCount(
                evaluation.getMatchCount()
        );

        /*
         * UC-025 evaluates Rule Engine behavior only.
         * Downstream decisions must not be manufactured here.
         */
        simulation.setApproveCount(
                0L
        );

        simulation.setRejectCount(
                0L
        );

        simulation.setReviewCount(
                0L
        );

        simulation.setResultSummary(
                resultSummary
        );

        simulation.setExecutedBy(
                executedBy
        );

        simulation.setCreatedAt(
                startedAt
        );

        RuleSimulation savedSimulation =
                ruleSimulationRepository.save(
                        simulation
                );

        createSuccessAuditEvent(
                ruleId,
                ruleVersionId,
                savedSimulation,
                evaluation,
                executedBy,
                correlationId
        );

        return ruleSimulationMapper.toResponse(
                savedSimulation
        );
    }

    private void validateRuleBeforeDatasetResolution(
            UUID ruleId,
            UUID ruleVersionId,
            UUID executedBy,
            UUID correlationId) {

        ruleRepository
                .findByRuleId(
                        ruleId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule not found: "
                                        + ruleId
                        )
                );

        RuleVersion ruleVersion =
                ruleVersionRepository
                        .findByRuleVersionId(
                                ruleVersionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule version not found: "
                                                + ruleVersionId
                                )
                        );

        if (!ruleId.equals(
                ruleVersion.getRuleId()
        )) {
            throw new ValidationException(
                    "Rule version does not belong to rule"
            );
        }

        List<RuleCondition> conditions =
                ruleConditionRepository
                        .findByRuleVersionIdOrderByConditionOrderAsc(
                                ruleVersionId
                        );

        if (conditions.isEmpty()) {

            ValidationException rejection =
                    new ValidationException(
                            "Rule version has no conditions"
                    );

            try {
                ruleTestingAuditService.recordRejected(
                        ruleId,
                        ruleVersionId,
                        executedBy,
                        correlationId,
                        "RULE_VERSION_HAS_NO_CONDITIONS"
                );
            } catch (RuntimeException auditFailure) {
                rejection.addSuppressed(
                        auditFailure
                );
            }

            throw rejection;
        }
    }

    private void validateRequest(
            UUID ruleId,
            UUID ruleVersionId,
            String simulationName,
            UUID executedBy) {

        if (ruleId == null) {
            throw new RequestValidationException(
                    "Rule identifier is required"
            );
        }

        if (ruleVersionId == null) {
            throw new RequestValidationException(
                    "Rule version identifier is required"
            );
        }

        if (simulationName == null
                || simulationName.isBlank()) {

            throw new RequestValidationException(
                    "Rule test name is required"
            );
        }

        if (executedBy == null) {
            throw new RequestValidationException(
                    "Rule testing actor is required"
            );
        }
    }

    private Map<String, Object> buildResultSummary(
            UUID ruleId,
            UUID ruleVersionId,
            RuleTestDatasetEvaluation evaluation) {

        Map<String, Object> summary =
                new LinkedHashMap<>();

        summary.put(
                "ruleId",
                ruleId.toString()
        );

        summary.put(
                "ruleVersionId",
                ruleVersionId.toString()
        );

        summary.put(
                "sampleSize",
                evaluation.getSampleSize()
        );

        summary.put(
                "matchCount",
                evaluation.getMatchCount()
        );

        summary.put(
                "nonMatchCount",
                evaluation.getNonMatchCount()
        );

        summary.put(
                "matchRate",
                calculateRate(
                        evaluation.getMatchCount(),
                        evaluation.getSampleSize()
                )
        );

        return summary;
    }

    private BigDecimal calculateRate(
            long numerator,
            long denominator) {

        return BigDecimal.valueOf(
                        numerator
                )
                .divide(
                        BigDecimal.valueOf(
                                denominator
                        ),
                        6,
                        RoundingMode.HALF_UP
                );
    }

    private void createSuccessAuditEvent(
            UUID ruleId,
            UUID ruleVersionId,
            RuleSimulation simulation,
            RuleTestDatasetEvaluation evaluation,
            UUID executedBy,
            UUID correlationId) {

        AuditEventRequest auditRequest =
                new AuditEventRequest();

        auditRequest.setUserId(
                executedBy
        );

        auditRequest.setEventType(
                "RULE_TEST_EXECUTED"
        );

        auditRequest.setEntityType(
                "RULE"
        );

        auditRequest.setEntityId(
                ruleId
        );

        auditRequest.setAction(
                "TEST"
        );

        auditRequest.setSourceComponent(
                "RULE_ENGINE"
        );

        auditRequest.setCorrelationId(
                correlationId
        );

        auditRequest.setEventResult(
                "SUCCESS"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "ruleId",
                ruleId.toString()
        );

        details.put(
                "ruleVersionId",
                ruleVersionId.toString()
        );

        details.put(
                "simulationId",
                simulation.getSimulationId()
                        .toString()
        );

        details.put(
                "datasetReference",
                evaluation.getDatasetReference()
        );

        details.put(
                "sampleSize",
                evaluation.getSampleSize()
        );

        details.put(
                "matchCount",
                evaluation.getMatchCount()
        );

        details.put(
                "nonMatchCount",
                evaluation.getNonMatchCount()
        );

        auditRequest.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                auditRequest
        );
    }
}
