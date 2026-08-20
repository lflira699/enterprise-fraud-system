package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleConditionRepository
        extends JpaRepository<RuleCondition, UUID> {

    Optional<RuleCondition> findByConditionId(
            UUID conditionId
    );

    List<RuleCondition> findByRuleVersionIdOrderByConditionOrderAsc(
            UUID ruleVersionId
    );

    List<RuleCondition> findByAttributeName(
            String attributeName
    );
}