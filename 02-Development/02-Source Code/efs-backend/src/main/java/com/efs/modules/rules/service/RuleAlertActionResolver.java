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
    private final RuleAlertActionParameterResolver parameterResolver;

    public RuleAlertActionResolver(
            RuleActionRepository ruleActionRepository,
            RuleExecutionRepository ruleExecutionRepository,
            RuleAlertActionParameterResolver parameterResolver) {

        this.ruleActionRepository =
                ruleActionRepository;

        this.ruleExecutionRepository =
                ruleExecutionRepository;

        this.parameterResolver =
                parameterResolver;
    }

    public List<RuleAlertActionParameters> resolveCreateAlertActions(
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
                .map(
                        parameterResolver::resolve
                )
                .toList();
    }

    public List<RuleAlertActionParameters> resolveCreateAlertActionsByTransactionId(
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