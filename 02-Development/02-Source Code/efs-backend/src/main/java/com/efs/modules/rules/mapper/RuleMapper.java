package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.entity.Rule;
import org.springframework.stereotype.Component;

@Component
public class RuleMapper {

    public Rule toEntity(
            RuleRequest request) {

        Rule rule =
                new Rule();

        rule.setRuleCode(
                request.getRuleCode()
        );

        rule.setRuleName(
                request.getRuleName()
        );

        rule.setDescription(
                request.getDescription()
        );

        rule.setCategory(
                request.getCategory()
        );

        rule.setSeverity(
                request.getSeverity()
        );

        rule.setPriority(
                request.getPriority()
        );

        rule.setOwnerTeam(
                request.getOwnerTeam()
        );

        rule.setCurrentVersion(
                request.getCurrentVersion()
        );

        rule.setStatus(
                request.getStatus()
        );

        return rule;
    }

    public RuleResponse toResponse(
            Rule rule) {

        RuleResponse response =
                new RuleResponse();

        response.setRuleId(
                rule.getRuleId()
        );

        response.setRuleCode(
                rule.getRuleCode()
        );

        response.setRuleName(
                rule.getRuleName()
        );

        response.setDescription(
                rule.getDescription()
        );

        response.setCategory(
                rule.getCategory()
        );

        response.setSeverity(
                rule.getSeverity()
        );

        response.setPriority(
                rule.getPriority()
        );

        response.setOwnerTeam(
                rule.getOwnerTeam()
        );

        response.setCurrentVersion(
                rule.getCurrentVersion()
        );

        response.setStatus(
                rule.getStatus()
        );

        response.setCreatedAt(
                rule.getCreatedAt()
        );

        response.setUpdatedAt(
                rule.getUpdatedAt()
        );

        return response;
    }
}