package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.repository.RuleActionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleAlertActionResolver {

    private static final String CREATE_ALERT =
            "CREATE_ALERT";

    private final RuleActionRepository ruleActionRepository;

    public RuleAlertActionResolver(
            RuleActionRepository ruleActionRepository) {

        this.ruleActionRepository =
                ruleActionRepository;
    }

    public List<RuleAction> resolveCreateAlertActions(
            RuleExecution ruleExecution) {

        if (ruleExecution == null) {
            throw new IllegalArgumentException(
                    "Rule execution is required"
            );
        }

        if (!Boolean.TRUE.equals(
                ruleExecution.getMatched())) {

            return List.of();
        }

        if (ruleExecution.getRuleVersionId() == null) {

            return List.of();
        }

        return ruleActionRepository
                .findByRuleVersionIdOrderByExecutionOrderAsc(
                        ruleExecution.getRuleVersionId()
                )
                .stream()
                .filter(
                        action ->
                                CREATE_ALERT.equals(
                                        action.getActionType()
                                )
                )
                .toList();
    }
}