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

        ruleVersion.setRuleName(
                request.getRuleName()
        );

        ruleVersion.setDescription(
                request.getDescription()
        );

        ruleVersion.setCategory(
                request.getCategory()
        );

        ruleVersion.setSeverity(
                request.getSeverity()
        );

        ruleVersion.setPriority(
                request.getPriority()
        );

        ruleVersion.setOwnerTeam(
                request.getOwnerTeam()
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

        response.setRuleName(
                ruleVersion.getRuleName()
        );

        response.setDescription(
                ruleVersion.getDescription()
        );

        response.setCategory(
                ruleVersion.getCategory()
        );

        response.setSeverity(
                ruleVersion.getSeverity()
        );

        response.setPriority(
                ruleVersion.getPriority()
        );

        response.setOwnerTeam(
                ruleVersion.getOwnerTeam()
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