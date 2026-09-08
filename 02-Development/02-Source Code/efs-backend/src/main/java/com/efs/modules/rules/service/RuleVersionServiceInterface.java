package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleVersionPublishRequest;
import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;

import java.util.List;
import java.util.UUID;

public interface RuleVersionServiceInterface {

    RuleVersionResponse createRuleVersion(
            UUID ruleId,
            RuleVersionRequest request
    );

    RuleVersionResponse publishRuleVersion(
            UUID ruleId,
            UUID ruleVersionId,
            RuleVersionPublishRequest request
    );

    RuleVersionResponse getRuleVersionById(
            UUID ruleVersionId
    );

    RuleVersionResponse getRuleVersionByRuleIdAndVersionNumber(
            UUID ruleId,
            Integer versionNumber
    );

    List<RuleVersionResponse> getRuleVersionsByRuleId(
            UUID ruleId
    );

    List<RuleVersionResponse> getRuleVersionsByPublicationStatus(
            String publicationStatus
    );
}