package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleVersionRepository
        extends JpaRepository<RuleVersion, UUID> {

    Optional<RuleVersion> findByRuleVersionId(
            UUID ruleVersionId
    );

    Optional<RuleVersion> findByRuleIdAndVersionNumber(
            UUID ruleId,
            Integer versionNumber
    );

    List<RuleVersion> findByRuleIdOrderByVersionNumberDesc(
            UUID ruleId
    );

    List<RuleVersion> findByPublicationStatusOrderByCreatedAtDesc(
            String publicationStatus
    );
}