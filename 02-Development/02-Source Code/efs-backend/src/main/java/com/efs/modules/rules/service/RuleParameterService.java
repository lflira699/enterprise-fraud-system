package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleParameterRequest;
import com.efs.modules.rules.dto.RuleParameterResponse;
import com.efs.modules.rules.entity.RuleParameter;
import com.efs.modules.rules.mapper.RuleParameterMapper;
import com.efs.modules.rules.repository.RuleParameterRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RuleParameterService
        implements RuleParameterServiceInterface {

    private final RuleParameterRepository ruleParameterRepository;
    private final RuleVersionRepository ruleVersionRepository;
    private final RuleParameterMapper ruleParameterMapper;

    public RuleParameterService(
            RuleParameterRepository ruleParameterRepository,
            RuleVersionRepository ruleVersionRepository,
            RuleParameterMapper ruleParameterMapper) {

        this.ruleParameterRepository = ruleParameterRepository;
        this.ruleVersionRepository = ruleVersionRepository;
        this.ruleParameterMapper = ruleParameterMapper;
    }

    @Override
    @Transactional
    public RuleParameterResponse createRuleParameter(
            UUID ruleVersionId,
            RuleParameterRequest request) {

        ruleVersionRepository
                .findByRuleVersionId(ruleVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule version not found: " + ruleVersionId
                        )
                );

        RuleParameter parameter =
                ruleParameterMapper.toEntity(request);

        parameter.setRuleVersionId(ruleVersionId);

        RuleParameter savedParameter =
                ruleParameterRepository.save(parameter);

        return ruleParameterMapper.toResponse(savedParameter);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleParameterResponse getRuleParameterById(
            UUID parameterId) {

        RuleParameter parameter =
                ruleParameterRepository
                        .findByParameterId(parameterId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule parameter not found: "
                                                + parameterId
                                )
                        );

        return ruleParameterMapper.toResponse(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleParameterResponse> getRuleParametersByRuleVersionId(
            UUID ruleVersionId) {

        ruleVersionRepository
                .findByRuleVersionId(ruleVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule version not found: " + ruleVersionId
                        )
                );

        return ruleParameterRepository
                .findByRuleVersionId(ruleVersionId)
                .stream()
                .map(ruleParameterMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleParameterResponse> getRuleParametersByName(
            String parameterName) {

        return ruleParameterRepository
                .findByParameterName(parameterName)
                .stream()
                .map(ruleParameterMapper::toResponse)
                .toList();
    }
}