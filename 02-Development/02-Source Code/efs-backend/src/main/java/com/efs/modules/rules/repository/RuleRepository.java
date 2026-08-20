package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleRepository
        extends JpaRepository<Rule, UUID> {

    Optional<Rule> findByRuleId(
            UUID ruleId
    );

    Optional<Rule> findByRuleCode(
            String ruleCode
    );

    List<Rule> findByStatusOrderByPriorityAsc(
            String status
    );

    List<Rule> findByCategoryOrderByPriorityAsc(
            String category
    );

    List<Rule> findBySeverityOrderByPriorityAsc(
            String severity
    );
}