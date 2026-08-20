package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleMetricRepository
        extends JpaRepository<RuleMetric, UUID> {

    Optional<RuleMetric> findByMetricId(
            UUID metricId
    );

    List<RuleMetric> findByRuleIdOrderByMetricDateDesc(
            UUID ruleId
    );

    List<RuleMetric> findByRuleVersionIdOrderByMetricDateDesc(
            UUID ruleVersionId
    );

    List<RuleMetric> findByMetricDateOrderByRuleId(
            LocalDate metricDate
    );
}