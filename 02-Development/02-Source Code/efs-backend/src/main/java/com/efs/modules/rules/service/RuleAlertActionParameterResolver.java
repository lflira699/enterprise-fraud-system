package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleAction;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RuleAlertActionParameterResolver {

    private static final String CREATE_ALERT =
            "CREATE_ALERT";

    private static final int ALERT_TYPE_MAX_LENGTH =
            40;

    private static final int PRIORITY_MAX_LENGTH =
            20;

    public RuleAlertActionParameters resolve(
            RuleAction ruleAction) {

        if (ruleAction == null) {
            throw new IllegalArgumentException(
                    "Rule action is required"
            );
        }

        if (!CREATE_ALERT.equals(
                ruleAction.getActionType())) {

            throw new IllegalArgumentException(
                    "Rule action must be CREATE_ALERT"
            );
        }

        Map<String, Object> parameters =
                ruleAction.getParameterJson();

        if (parameters == null) {
            throw new IllegalArgumentException(
                    "CREATE_ALERT parameters are required"
            );
        }

        String alertType =
                requireStringParameter(
                        parameters,
                        "alertType",
                        ALERT_TYPE_MAX_LENGTH
                );

        String priority =
                requireStringParameter(
                        parameters,
                        "priority",
                        PRIORITY_MAX_LENGTH
                );

        return new RuleAlertActionParameters(
                alertType,
                priority
        );
    }

    private String requireStringParameter(
            Map<String, Object> parameters,
            String parameterName,
            int maxLength) {

        Object value =
                parameters.get(
                        parameterName
                );

        if (!(value instanceof String text)
                || text.isBlank()) {

            throw new IllegalArgumentException(
                    "CREATE_ALERT parameter "
                            + parameterName
                            + " is required"
            );
        }

        if (text.length() > maxLength) {

            throw new IllegalArgumentException(
                    "CREATE_ALERT parameter "
                            + parameterName
                            + " exceeds maximum length "
                            + maxLength
            );
        }

        return text;
    }
}