package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleCondition;
import com.efs.modules.rules.entity.RuleVersion;
import com.efs.modules.rules.repository.RuleConditionRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RuleVersionEvaluator {

    private final RuleVersionRepository ruleVersionRepository;
    private final RuleConditionRepository ruleConditionRepository;
    private final RuleConditionSetEvaluator conditionSetEvaluator;

    public RuleVersionEvaluator(
            RuleVersionRepository ruleVersionRepository,
            RuleConditionRepository ruleConditionRepository,
            RuleConditionSetEvaluator conditionSetEvaluator) {

        this.ruleVersionRepository = ruleVersionRepository;
        this.ruleConditionRepository = ruleConditionRepository;
        this.conditionSetEvaluator = conditionSetEvaluator;
    }

    @Transactional(readOnly = true)
    public boolean evaluate(
            UUID ruleVersionId,
            Map<String, Object> facts) {

        if (ruleVersionId == null) {
            throw new IllegalArgumentException(
                    "Rule version identifier is required"
            );
        }

        if (facts == null) {
            throw new IllegalArgumentException(
                    "Evaluation facts are required"
            );
        }

        RuleVersion ruleVersion =
                ruleVersionRepository
                        .findByRuleVersionId(ruleVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule version not found: "
                                                + ruleVersionId
                                )
                        );

        List<RuleCondition> conditions =
                ruleConditionRepository
                        .findByRuleVersionIdOrderByConditionOrderAsc(
                                ruleVersion.getRuleVersionId()
                        );

        if (conditions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Rule version has no conditions "
                            + "and cannot be evaluated: "
                            + ruleVersionId
            );
        }

        return conditionSetEvaluator.evaluate(
                conditions,
                facts
        );
    }
}