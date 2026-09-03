package com.efs.modules.integration.event;

import com.efs.modules.integration.config.RabbitMQConfig;
import org.springframework.stereotype.Component;

@Component
public class DomainEventRoutingKeyResolver {

    public String resolve(String eventType) {

        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException(
                    "Domain event type is required"
            );
        }

        return switch (eventType) {

            case "RuleMatched" ->
                    "rule.matched.v1";

            case "ScenarioActivated" ->
                    RabbitMQConfig.SCENARIO_ACTIVATED_ROUTING_KEY;

            case "RiskCalculated" ->
                    "risk.calculated.v1";

            case "DecisionGenerated" ->
                    "decision.generated.v1";

            case "AlertCreated" ->
                    "alert.created.v1";

            case "CaseCreated" ->
                    "case.created.v1";

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported domain event type: "
                                    + eventType
                    );
        };
    }
}