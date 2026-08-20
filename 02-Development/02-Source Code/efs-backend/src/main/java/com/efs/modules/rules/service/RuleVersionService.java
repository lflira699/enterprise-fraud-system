package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.entity.RuleVersion;
import com.efs.modules.rules.mapper.RuleVersionMapper;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleVersionService
        implements RuleVersionServiceInterface {

    private final RuleVersionRepository ruleVersionRepository;
    private final RuleRepository ruleRepository;
    private final RuleVersionMapper ruleVersionMapper;

    public RuleVersionService(
            RuleVersionRepository ruleVersionRepository,
            RuleRepository ruleRepository,
            RuleVersionMapper ruleVersionMapper) {

        this.ruleVersionRepository = ruleVersionRepository;
        this.ruleRepository = ruleRepository;
        this.ruleVersionMapper = ruleVersionMapper;
    }

    @Override
    @Transactional
    public RuleVersionResponse createRuleVersion(
            UUID ruleId,
            RuleVersionRequest request) {

        ruleRepository
                .findByRuleId(ruleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule not found: " + ruleId
                        )
                );

        RuleVersion ruleVersion =
                ruleVersionMapper.toEntity(request);

        ruleVersion.setRuleId(ruleId);
        ruleVersion.setCreatedAt(LocalDateTime.now());

        RuleVersion savedRuleVersion =
                ruleVersionRepository.save(ruleVersion);

        return ruleVersionMapper.toResponse(savedRuleVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleVersionResponse getRuleVersionById(
            UUID ruleVersionId) {

        RuleVersion ruleVersion =
                ruleVersionRepository
                        .findByRuleVersionId(ruleVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule version not found: "
                                                + ruleVersionId
                                )
                        );

        return ruleVersionMapper.toResponse(ruleVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleVersionResponse getRuleVersionByRuleIdAndVersionNumber(
            UUID ruleId,
            Integer versionNumber) {

        RuleVersion ruleVersion =
                ruleVersionRepository
                        .findByRuleIdAndVersionNumber(
                                ruleId,
                                versionNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule version not found for rule "
                                                + ruleId
                                                + " and version "
                                                + versionNumber
                                )
                        );

        return ruleVersionMapper.toResponse(ruleVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleVersionResponse> getRuleVersionsByRuleId(
            UUID ruleId) {

        ruleRepository
                .findByRuleId(ruleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule not found: " + ruleId
                        )
                );

        return ruleVersionRepository
                .findByRuleIdOrderByVersionNumberDesc(ruleId)
                .stream()
                .map(ruleVersionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleVersionResponse> getRuleVersionsByPublicationStatus(
            String publicationStatus) {

        return ruleVersionRepository
                .findByPublicationStatusOrderByCreatedAtDesc(publicationStatus)
                .stream()
                .map(ruleVersionMapper::toResponse)
                .toList();
    }
}