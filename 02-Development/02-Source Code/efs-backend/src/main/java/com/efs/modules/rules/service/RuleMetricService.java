package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleMetricRequest;
import com.efs.modules.rules.dto.RuleMetricResponse;
import com.efs.modules.rules.entity.RuleMetric;
import com.efs.modules.rules.mapper.RuleMetricMapper;
import com.efs.modules.rules.repository.RuleMetricRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleMetricService
        implements RuleMetricServiceInterface {

    private final RuleMetricRepository ruleMetricRepository;
    private final RuleMetricMapper ruleMetricMapper;

    public RuleMetricService(
            RuleMetricRepository ruleMetricRepository,
            RuleMetricMapper ruleMetricMapper) {

        this.ruleMetricRepository = ruleMetricRepository;
        this.ruleMetricMapper = ruleMetricMapper;
    }

    @Override
    @Transactional
    public RuleMetricResponse createRuleMetric(
            RuleMetricRequest request) {

        RuleMetric metric =
                ruleMetricMapper.toEntity(request);

        metric.setCalculatedAt(
                LocalDateTime.now()
        );

        RuleMetric savedMetric =
                ruleMetricRepository.save(metric);

        return ruleMetricMapper.toResponse(savedMetric);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleMetricResponse getRuleMetricById(
            UUID metricId) {

        RuleMetric metric =
                ruleMetricRepository
                        .findByMetricId(metricId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule metric not found: "
                                                + metricId
                                )
                        );

        return ruleMetricMapper.toResponse(metric);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleMetricResponse> getRuleMetricsByRuleId(
            UUID ruleId) {

        return ruleMetricRepository
                .findByRuleIdOrderByMetricDateDesc(ruleId)
                .stream()
                .map(ruleMetricMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleMetricResponse> getRuleMetricsByRuleVersionId(
            UUID ruleVersionId) {

        return ruleMetricRepository
                .findByRuleVersionIdOrderByMetricDateDesc(ruleVersionId)
                .stream()
                .map(ruleMetricMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleMetricResponse> getRuleMetricsByDate(
            LocalDate metricDate) {

        return ruleMetricRepository
                .findByMetricDateOrderByRuleId(metricDate)
                .stream()
                .map(ruleMetricMapper::toResponse)
                .toList();
    }
}