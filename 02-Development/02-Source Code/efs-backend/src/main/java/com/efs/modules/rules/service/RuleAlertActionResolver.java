package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.repository.RuleActionRepository;
import com.efs.modules.rules.repository.RuleExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleAlertActionResolver {

    private static final String CREATE_ALERT =
            "CREATE_ALERT";

    private final RuleActionRepository ruleActionRepository;
    private final RuleExecutionRepository ruleExecutionRepository;

    public RuleAlertActionResolver(
            RuleActionRepository ruleActionRepository,
            RuleExecutionRepository ruleExecutionRepository) {

        this.ruleActionRepository =
                ruleActionRepository;

        this.ruleExecutionRepository =
                ruleExecutionRepository;
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

    public List<RuleAction> resolveCreateAlertActionsByTransactionId(
            UUID transactionId) {

        if (transactionId == null) {
            throw new IllegalArgumentException(
                    "Transaction id is required"
            );
        }

        return ruleExecutionRepository
                .findByTransactionIdAndMatchedTrueOrderByExecutedAtDesc(
                        transactionId
                )
                .stream()
                .flatMap(
                        execution ->
                                resolveCreateAlertActions(
                                        execution
                                ).stream()
                )
                .toList();
    }
}