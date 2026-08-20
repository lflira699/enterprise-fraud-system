package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleActionRepository
        extends JpaRepository<RuleAction, UUID> {

    Optional<RuleAction> findByActionId(
            UUID actionId
    );

    List<RuleAction> findByRuleVersionIdOrderByExecutionOrderAsc(
            UUID ruleVersionId
    );

    List<RuleAction> findByActionTypeOrderByCreatedAtDesc(
            String actionType
    );
}