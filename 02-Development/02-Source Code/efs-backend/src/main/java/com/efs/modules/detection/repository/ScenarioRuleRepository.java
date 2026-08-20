package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioRuleRepository
        extends JpaRepository<ScenarioRule, UUID> {

    Optional<ScenarioRule> findByScenarioRuleId(
            UUID scenarioRuleId
    );

    List<ScenarioRule> findByScenarioVersionIdOrderByEvaluationOrderAsc(
            UUID scenarioVersionId
    );

    List<ScenarioRule> findByRuleIdOrderByScenarioVersionIdAsc(
            UUID ruleId
    );

    List<ScenarioRule> findByScenarioVersionIdAndRequiredOrderByEvaluationOrderAsc(
            UUID scenarioVersionId,
            Boolean required
    );
}