package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleGroupRepository
        extends JpaRepository<RuleGroup, UUID> {

    Optional<RuleGroup> findByRuleGroupId(
            UUID ruleGroupId
    );

    Optional<RuleGroup> findByGroupCode(
            String groupCode
    );

    List<RuleGroup> findByStatusOrderByExecutionOrderAsc(
            String status
    );

    List<RuleGroup> findByCategoryOrderByExecutionOrderAsc(
            String category
    );
}