package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleConditionRequest;
import com.efs.modules.rules.dto.RuleConditionResponse;
import com.efs.modules.rules.entity.RuleCondition;
import com.efs.modules.rules.mapper.RuleConditionMapper;
import com.efs.modules.rules.repository.RuleConditionRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RuleConditionService
        implements RuleConditionServiceInterface {

    private final RuleConditionRepository ruleConditionRepository;
    private final RuleVersionRepository ruleVersionRepository;
    private final RuleConditionMapper ruleConditionMapper;

    public RuleConditionService(
            RuleConditionRepository ruleConditionRepository,
            RuleVersionRepository ruleVersionRepository,
            RuleConditionMapper ruleConditionMapper) {

        this.ruleConditionRepository = ruleConditionRepository;
        this.ruleVersionRepository = ruleVersionRepository;
        this.ruleConditionMapper = ruleConditionMapper;
    }

    @Override
    @Transactional
    public RuleConditionResponse createRuleCondition(
            UUID ruleVersionId,
            RuleConditionRequest request) {

        ruleVersionRepository
                .findByRuleVersionId(ruleVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule version not found: " + ruleVersionId
                        )
                );

        RuleCondition condition =
                ruleConditionMapper.toEntity(request);

        condition.setRuleVersionId(ruleVersionId);

        RuleCondition savedCondition =
                ruleConditionRepository.save(condition);

        return ruleConditionMapper.toResponse(savedCondition);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleConditionResponse getRuleConditionById(
            UUID conditionId) {

        RuleCondition condition =
                ruleConditionRepository
                        .findByConditionId(conditionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule condition not found: "
                                                + conditionId
                                )
                        );

        return ruleConditionMapper.toResponse(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleConditionResponse> getRuleConditionsByRuleVersionId(
            UUID ruleVersionId) {

        ruleVersionRepository
                .findByRuleVersionId(ruleVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule version not found: " + ruleVersionId
                        )
                );

        return ruleConditionRepository
                .findByRuleVersionIdOrderByConditionOrderAsc(ruleVersionId)
                .stream()
                .map(ruleConditionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleConditionResponse> getRuleConditionsByAttributeName(
            String attributeName) {

        return ruleConditionRepository
                .findByAttributeName(attributeName)
                .stream()
                .map(ruleConditionMapper::toResponse)
                .toList();
    }
}