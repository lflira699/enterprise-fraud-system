package com.efs.modules.alert.service;

import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.service.RuleAlertActionParameterResolver;
import com.efs.modules.rules.service.RuleAlertActionParameters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertRuleActionConsolidator {

    private final RuleAlertActionParameterResolver
            parameterResolver;

    public AlertRuleActionConsolidator(
            RuleAlertActionParameterResolver parameterResolver) {

        this.parameterResolver =
                parameterResolver;
    }

    public Optional<RuleAlertActionParameters> consolidate(
            List<RuleAction> ruleActions) {

        if (ruleActions == null) {
            throw new IllegalArgumentException(
                    "Rule actions are required"
            );
        }

        if (ruleActions.isEmpty()) {
            return Optional.empty();
        }

        RuleAlertActionParameters consolidatedParameters =
                parameterResolver.resolve(
                        ruleActions.get(0)
                );

        for (int index = 1;
             index < ruleActions.size();
             index++) {

            RuleAlertActionParameters currentParameters =
                    parameterResolver.resolve(
                            ruleActions.get(index)
                    );

            if (!consolidatedParameters.equals(
                    currentParameters)) {

                throw new IllegalArgumentException(
                        "CREATE_ALERT actions contain conflicting alert parameters"
                );
            }
        }

        return Optional.of(
                consolidatedParameters
        );
    }
}