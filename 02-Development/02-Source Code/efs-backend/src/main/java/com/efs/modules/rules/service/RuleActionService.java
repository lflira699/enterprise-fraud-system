package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleActionRequest;
import com.efs.modules.rules.dto.RuleActionResponse;
import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.mapper.RuleActionMapper;
import com.efs.modules.rules.repository.RuleActionRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleActionService
        implements RuleActionServiceInterface {

    private final RuleActionRepository ruleActionRepository;
    private final RuleVersionRepository ruleVersionRepository;
    private final RuleActionMapper ruleActionMapper;

    public RuleActionService(
            RuleActionRepository ruleActionRepository,
            RuleVersionRepository ruleVersionRepository,
            RuleActionMapper ruleActionMapper) {

        this.ruleActionRepository = ruleActionRepository;
        this.ruleVersionRepository = ruleVersionRepository;
        this.ruleActionMapper = ruleActionMapper;
    }

    @Override
    @Transactional
    public RuleActionResponse createRuleAction(
            UUID ruleVersionId,
            RuleActionRequest request) {

        ruleVersionRepository
                .findByRuleVersionId(ruleVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule version not found: " + ruleVersionId
                        )
                );

        RuleAction action =
                ruleActionMapper.toEntity(request);

        action.setRuleVersionId(ruleVersionId);
        action.setCreatedAt(LocalDateTime.now());

        RuleAction savedAction =
                ruleActionRepository.save(action);

        return ruleActionMapper.toResponse(savedAction);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleActionResponse getRuleActionById(
            UUID actionId) {

        RuleAction action =
                ruleActionRepository
                        .findByActionId(actionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule action not found: " + actionId
                                )
                        );

        return ruleActionMapper.toResponse(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleActionResponse> getRuleActionsByRuleVersionId(
            UUID ruleVersionId) {

        ruleVersionRepository
                .findByRuleVersionId(ruleVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule version not found: " + ruleVersionId
                        )
                );

        return ruleActionRepository
                .findByRuleVersionIdOrderByExecutionOrderAsc(ruleVersionId)
                .stream()
                .map(ruleActionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleActionResponse> getRuleActionsByType(
            String actionType) {

        return ruleActionRepository
                .findByActionTypeOrderByCreatedAtDesc(actionType)
                .stream()
                .map(ruleActionMapper::toResponse)
                .toList();
    }
}