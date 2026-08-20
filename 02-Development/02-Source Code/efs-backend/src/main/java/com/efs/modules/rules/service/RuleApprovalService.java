package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleApprovalRequest;
import com.efs.modules.rules.dto.RuleApprovalResponse;
import com.efs.modules.rules.entity.RuleApproval;
import com.efs.modules.rules.mapper.RuleApprovalMapper;
import com.efs.modules.rules.repository.RuleApprovalRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleApprovalService
        implements RuleApprovalServiceInterface {

    private final RuleApprovalRepository ruleApprovalRepository;
    private final RuleApprovalMapper ruleApprovalMapper;

    public RuleApprovalService(
            RuleApprovalRepository ruleApprovalRepository,
            RuleApprovalMapper ruleApprovalMapper) {

        this.ruleApprovalRepository = ruleApprovalRepository;
        this.ruleApprovalMapper = ruleApprovalMapper;
    }

    @Override
    @Transactional
    public RuleApprovalResponse createRuleApproval(
            RuleApprovalRequest request) {

        RuleApproval approval =
                ruleApprovalMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        approval.setSubmittedAt(now);
        approval.setCreatedAt(now);

        RuleApproval savedApproval =
                ruleApprovalRepository.save(approval);

        return ruleApprovalMapper.toResponse(savedApproval);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleApprovalResponse getRuleApprovalById(
            UUID approvalId) {

        RuleApproval approval =
                ruleApprovalRepository
                        .findByApprovalId(approvalId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule approval not found: "
                                                + approvalId
                                )
                        );

        return ruleApprovalMapper.toResponse(approval);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleApprovalResponse> getRuleApprovalsByEntity(
            String entityType,
            UUID entityId) {

        return ruleApprovalRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        entityType,
                        entityId
                )
                .stream()
                .map(ruleApprovalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleApprovalResponse> getRuleApprovalsByStatus(
            String approvalStatus) {

        return ruleApprovalRepository
                .findByApprovalStatusOrderByCreatedAtDesc(
                        approvalStatus
                )
                .stream()
                .map(ruleApprovalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleApprovalResponse> getRuleApprovalsBySubmittedBy(
            UUID submittedBy) {

        return ruleApprovalRepository
                .findBySubmittedByOrderByCreatedAtDesc(
                        submittedBy
                )
                .stream()
                .map(ruleApprovalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleApprovalResponse> getRuleApprovalsByReviewedBy(
            UUID reviewedBy) {

        return ruleApprovalRepository
                .findByReviewedByOrderByCreatedAtDesc(
                        reviewedBy
                )
                .stream()
                .map(ruleApprovalMapper::toResponse)
                .toList();
    }
}