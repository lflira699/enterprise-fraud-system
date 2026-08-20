package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.mapper.RuleMapper;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleService
        implements RuleServiceInterface {

    private final RuleRepository ruleRepository;
    private final RuleMapper ruleMapper;

    public RuleService(
            RuleRepository ruleRepository,
            RuleMapper ruleMapper) {

        this.ruleRepository = ruleRepository;
        this.ruleMapper = ruleMapper;
    }

    @Override
    @Transactional
    public RuleResponse createRule(
            RuleRequest request) {

        Rule rule =
                ruleMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);

        Rule savedRule =
                ruleRepository.save(rule);

        return ruleMapper.toResponse(savedRule);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleResponse getRuleById(
            UUID ruleId) {

        Rule rule =
                ruleRepository
                        .findByRuleId(ruleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule not found: " + ruleId
                                )
                        );

        return ruleMapper.toResponse(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleResponse getRuleByCode(
            String ruleCode) {

        Rule rule =
                ruleRepository
                        .findByRuleCode(ruleCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule not found: " + ruleCode
                                )
                        );

        return ruleMapper.toResponse(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getRulesByStatus(
            String status) {

        return ruleRepository
                .findByStatusOrderByPriorityAsc(status)
                .stream()
                .map(ruleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getRulesByCategory(
            String category) {

        return ruleRepository
                .findByCategoryOrderByPriorityAsc(category)
                .stream()
                .map(ruleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getRulesBySeverity(
            String severity) {

        return ruleRepository
                .findBySeverityOrderByPriorityAsc(severity)
                .stream()
                .map(ruleMapper::toResponse)
                .toList();
    }
}