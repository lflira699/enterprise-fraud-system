package com.efs.modules.integration.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainEventRoutingKeyResolverTest {

    private final DomainEventRoutingKeyResolver resolver =
            new DomainEventRoutingKeyResolver();

    @Test
    void shouldResolveRuleMatchedRoutingKey() {

        assertEquals(
                "rule.matched.v1",
                resolver.resolve("RuleMatched")
        );
    }

    @Test
    void shouldResolveScenarioActivatedRoutingKey() {

        assertEquals(
                "scenario.activated.v1",
                resolver.resolve("ScenarioActivated")
        );
    }

    @Test
    void shouldResolveRiskCalculatedRoutingKey() {

        assertEquals(
                "risk.calculated.v1",
                resolver.resolve("RiskCalculated")
        );
    }

    @Test
    void shouldResolveDecisionGeneratedRoutingKey() {

        assertEquals(
                "decision.generated.v1",
                resolver.resolve("DecisionGenerated")
        );
    }

    @Test
    void shouldResolveAlertCreatedRoutingKey() {

        assertEquals(
                "alert.created.v1",
                resolver.resolve("AlertCreated")
        );
    }

    @Test
    void shouldResolveCaseCreatedRoutingKey() {

        assertEquals(
                "case.created.v1",
                resolver.resolve("CaseCreated")
        );
    }

    @Test
    void shouldRejectUnknownEventType() {

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(
                        "UNKNOWN_EVENT"
                )
        );
    }

    @Test
    void shouldRejectMissingEventType() {

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(" ")
        );
    }
}