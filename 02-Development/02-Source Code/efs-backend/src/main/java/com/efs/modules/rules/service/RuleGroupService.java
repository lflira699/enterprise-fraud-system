package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleGroupRequest;
import com.efs.modules.rules.dto.RuleGroupResponse;
import com.efs.modules.rules.entity.RuleGroup;
import com.efs.modules.rules.mapper.RuleGroupMapper;
import com.efs.modules.rules.repository.RuleGroupRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleGroupService
        implements RuleGroupServiceInterface {

    private final RuleGroupRepository ruleGroupRepository;
    private final RuleGroupMapper ruleGroupMapper;

    public RuleGroupService(
            RuleGroupRepository ruleGroupRepository,
            RuleGroupMapper ruleGroupMapper) {

        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleGroupMapper = ruleGroupMapper;
    }

    @Override
    @Transactional
    public RuleGroupResponse createRuleGroup(
            RuleGroupRequest request) {

        RuleGroup ruleGroup =
                ruleGroupMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        ruleGroup.setCreatedAt(now);
        ruleGroup.setUpdatedAt(now);

        RuleGroup savedRuleGroup =
                ruleGroupRepository.save(ruleGroup);

        return ruleGroupMapper.toResponse(savedRuleGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleGroupResponse getRuleGroupById(
            UUID ruleGroupId) {

        RuleGroup ruleGroup =
                ruleGroupRepository
                        .findByRuleGroupId(ruleGroupId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule group not found: " + ruleGroupId
                                )
                        );

        return ruleGroupMapper.toResponse(ruleGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleGroupResponse getRuleGroupByCode(
            String groupCode) {

        RuleGroup ruleGroup =
                ruleGroupRepository
                        .findByGroupCode(groupCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule group not found: " + groupCode
                                )
                        );

        return ruleGroupMapper.toResponse(ruleGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleGroupResponse> getRuleGroupsByStatus(
            String status) {

        return ruleGroupRepository
                .findByStatusOrderByExecutionOrderAsc(status)
                .stream()
                .map(ruleGroupMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleGroupResponse> getRuleGroupsByCategory(
            String category) {

        return ruleGroupRepository
                .findByCategoryOrderByExecutionOrderAsc(category)
                .stream()
                .map(ruleGroupMapper::toResponse)
                .toList();
    }
}