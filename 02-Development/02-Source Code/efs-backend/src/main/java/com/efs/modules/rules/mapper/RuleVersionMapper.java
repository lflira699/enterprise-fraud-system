package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.entity.RuleVersion;
import org.springframework.stereotype.Component;

@Component
public class RuleVersionMapper {

    public RuleVersion toEntity(
            RuleVersionRequest request) {

        RuleVersion ruleVersion =
                new RuleVersion();

        ruleVersion.setVersionNumber(
                request.getVersionNumber()
        );

        ruleVersion.setEffectiveFrom(
                request.getEffectiveFrom()
        );

        ruleVersion.setEffectiveTo(
                request.getEffectiveTo()
        );

        ruleVersion.setPublicationStatus(
                request.getPublicationStatus()
        );

        ruleVersion.setChangeSummary(
                request.getChangeSummary()
        );

        ruleVersion.setCreatedBy(
                request.getCreatedBy()
        );

        ruleVersion.setApprovedBy(
                request.getApprovedBy()
        );

        return ruleVersion;
    }

    public RuleVersionResponse toResponse(
            RuleVersion ruleVersion) {

        RuleVersionResponse response =
                new RuleVersionResponse();

        response.setRuleVersionId(
                ruleVersion.getRuleVersionId()
        );

        response.setRuleId(
                ruleVersion.getRuleId()
        );

        response.setVersionNumber(
                ruleVersion.getVersionNumber()
        );

        response.setEffectiveFrom(
                ruleVersion.getEffectiveFrom()
        );

        response.setEffectiveTo(
                ruleVersion.getEffectiveTo()
        );

        response.setPublicationStatus(
                ruleVersion.getPublicationStatus()
        );

        response.setChangeSummary(
                ruleVersion.getChangeSummary()
        );

        response.setCreatedBy(
                ruleVersion.getCreatedBy()
        );

        response.setApprovedBy(
                ruleVersion.getApprovedBy()
        );

        response.setCreatedAt(
                ruleVersion.getCreatedAt()
        );

        return response;
    }
}