package com.efs.modules.alert.service;

import com.efs.modules.rules.service.RuleAlertActionParameters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertRuleActionConsolidator {

    public Optional<RuleAlertActionParameters> consolidate(
            List<RuleAlertActionParameters> ruleActionParameters) {

        if (ruleActionParameters == null) {
            throw new IllegalArgumentException(
                    "Rule actions are required"
            );
        }

        if (ruleActionParameters.isEmpty()) {
            return Optional.empty();
        }

        RuleAlertActionParameters consolidatedParameters =
                ruleActionParameters.get(0);

        for (int index = 1;
             index < ruleActionParameters.size();
             index++) {

            RuleAlertActionParameters currentParameters =
                    ruleActionParameters.get(index);

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