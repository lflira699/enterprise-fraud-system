package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioRuleRequest;
import com.efs.modules.detection.dto.ScenarioRuleResponse;
import com.efs.modules.detection.entity.ScenarioRule;
import com.efs.modules.detection.mapper.ScenarioRuleMapper;
import com.efs.modules.detection.repository.ScenarioRuleRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScenarioRuleService
        implements ScenarioRuleServiceInterface {

    private final ScenarioRuleRepository scenarioRuleRepository;
    private final ScenarioRuleMapper scenarioRuleMapper;

    public ScenarioRuleService(
            ScenarioRuleRepository scenarioRuleRepository,
            ScenarioRuleMapper scenarioRuleMapper) {

        this.scenarioRuleRepository = scenarioRuleRepository;
        this.scenarioRuleMapper = scenarioRuleMapper;
    }

    @Override
    @Transactional
    public ScenarioRuleResponse createScenarioRule(
            ScenarioRuleRequest request) {

        ScenarioRule scenarioRule =
                scenarioRuleMapper.toEntity(request);

        scenarioRule.setCreatedAt(
                LocalDateTime.now()
        );

        ScenarioRule savedScenarioRule =
                scenarioRuleRepository.save(scenarioRule);

        return scenarioRuleMapper.toResponse(
                savedScenarioRule
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioRuleResponse getScenarioRuleById(
            UUID scenarioRuleId) {

        ScenarioRule scenarioRule =
                scenarioRuleRepository
                        .findByScenarioRuleId(scenarioRuleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario rule not found: "
                                                + scenarioRuleId
                                )
                        );

        return scenarioRuleMapper.toResponse(
                scenarioRule
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioRuleResponse>
    getScenarioRulesByScenarioVersion(
            UUID scenarioVersionId) {

        return scenarioRuleRepository
                .findByScenarioVersionIdOrderByEvaluationOrderAsc(
                        scenarioVersionId
                )
                .stream()
                .map(scenarioRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioRuleResponse>
    getScenarioRulesByRule(UUID ruleId) {

        return scenarioRuleRepository
                .findByRuleIdOrderByScenarioVersionIdAsc(ruleId)
                .stream()
                .map(scenarioRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioRuleResponse>
    getRequiredScenarioRules(
            UUID scenarioVersionId) {

        return scenarioRuleRepository
                .findByScenarioVersionIdAndRequiredOrderByEvaluationOrderAsc(
                        scenarioVersionId,
                        true
                )
                .stream()
                .map(scenarioRuleMapper::toResponse)
                .toList();
    }
}