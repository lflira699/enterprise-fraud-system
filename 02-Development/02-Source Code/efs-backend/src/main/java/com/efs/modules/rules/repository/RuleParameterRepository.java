package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleParameterRepository
        extends JpaRepository<RuleParameter, UUID> {

    Optional<RuleParameter> findByParameterId(
            UUID parameterId
    );

    List<RuleParameter> findByRuleVersionId(
            UUID ruleVersionId
    );

    List<RuleParameter> findByParameterName(
            String parameterName
    );
}