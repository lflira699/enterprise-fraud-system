package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.rules.dto.RuleActivationRequest;
import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.dto.RuleUpdateRequest;
import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.mapper.RuleMapper;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RuleService
        implements RuleServiceInterface {

    private final RuleRepository ruleRepository;
    private final RuleMapper ruleMapper;
    private final RuleVersionServiceInterface ruleVersionService;
    private final RuleHistoryServiceInterface ruleHistoryService;
    private final AuditEventServiceInterface auditEventService;
    private final AuditEntityChangeServiceInterface auditEntityChangeService;

    public RuleService(
            RuleRepository ruleRepository,
            RuleMapper ruleMapper,
            RuleVersionServiceInterface ruleVersionService,
            RuleHistoryServiceInterface ruleHistoryService,
            AuditEventServiceInterface auditEventService,
            AuditEntityChangeServiceInterface auditEntityChangeService) {

        this.ruleRepository = ruleRepository;
        this.ruleMapper = ruleMapper;
        this.ruleVersionService = ruleVersionService;
        this.ruleHistoryService = ruleHistoryService;
        this.auditEventService = auditEventService;
        this.auditEntityChangeService = auditEntityChangeService;
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
    public List<RuleResponse> getRules() {

        return ruleRepository
                .findAllByOrderByPriorityAsc()
                .stream()
                .map(ruleMapper::toResponse)
                .toList();
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

    @Override
    @Transactional
    public RuleVersionResponse updateRule(
            UUID ruleId,
            RuleUpdateRequest request) {

        if (request.getChangedBy() == null) {
            throw new RequestValidationException(
                    "Rule update actor is required"
            );
        }

        Rule rule =
                ruleRepository
                        .findByRuleId(ruleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule not found: " + ruleId
                                )
                        );

        boolean hasUpdate =
                request.getRuleName() != null
                        || request.getDescription() != null
                        || request.getCategory() != null
                        || request.getSeverity() != null
                        || request.getPriority() != null
                        || request.getOwnerTeam() != null;

        if (!hasUpdate) {
            throw new RequestValidationException(
                    "At least one rule field is required for update"
            );
        }

        List<RuleVersionResponse> existingVersions =
                ruleVersionService
                        .getRuleVersionsByRuleId(
                                ruleId
                        );

        RuleVersionResponse baseVersion =
                existingVersions.isEmpty()
                        ? null
                        : existingVersions.get(0);

        int highestVersionNumber =
                baseVersion == null
                        ? rule.getCurrentVersion()
                        : baseVersion.getVersionNumber();

        int nextVersionNumber =
                Math.max(
                        rule.getCurrentVersion(),
                        highestVersionNumber
                ) + 1;

        String baseRuleName =
                baseVersion == null
                        ? rule.getRuleName()
                        : baseVersion.getRuleName();

        String baseDescription =
                baseVersion == null
                        ? rule.getDescription()
                        : baseVersion.getDescription();

        String baseCategory =
                baseVersion == null
                        ? rule.getCategory()
                        : baseVersion.getCategory();

        String baseSeverity =
                baseVersion == null
                        ? rule.getSeverity()
                        : baseVersion.getSeverity();

        Short basePriority =
                baseVersion == null
                        ? rule.getPriority()
                        : baseVersion.getPriority();

        String baseOwnerTeam =
                baseVersion == null
                        ? rule.getOwnerTeam()
                        : baseVersion.getOwnerTeam();

        String updatedRuleName =
                request.getRuleName() == null
                        ? baseRuleName
                        : request.getRuleName();

        String updatedDescription =
                request.getDescription() == null
                        ? baseDescription
                        : request.getDescription();

        String updatedCategory =
                request.getCategory() == null
                        ? baseCategory
                        : request.getCategory();

        String updatedSeverity =
                request.getSeverity() == null
                        ? baseSeverity
                        : request.getSeverity();

        Short updatedPriority =
                request.getPriority() == null
                        ? basePriority
                        : request.getPriority();

        String updatedOwnerTeam =
                request.getOwnerTeam() == null
                        ? baseOwnerTeam
                        : request.getOwnerTeam();

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        if (request.getRuleName() != null) {

            previousValue.put(
                    "ruleName",
                    baseRuleName
            );

            currentValue.put(
                    "ruleName",
                    updatedRuleName
            );
        }

        if (request.getDescription() != null) {

            previousValue.put(
                    "description",
                    baseDescription
            );

            currentValue.put(
                    "description",
                    updatedDescription
            );
        }

        if (request.getCategory() != null) {

            previousValue.put(
                    "category",
                    baseCategory
            );

            currentValue.put(
                    "category",
                    updatedCategory
            );
        }

        if (request.getSeverity() != null) {

            previousValue.put(
                    "severity",
                    baseSeverity
            );

            currentValue.put(
                    "severity",
                    updatedSeverity
            );
        }

        if (request.getPriority() != null) {

            previousValue.put(
                    "priority",
                    basePriority
            );

            currentValue.put(
                    "priority",
                    updatedPriority
            );
        }

        if (request.getOwnerTeam() != null) {

            previousValue.put(
                    "ownerTeam",
                    baseOwnerTeam
            );

            currentValue.put(
                    "ownerTeam",
                    updatedOwnerTeam
            );
        }

        RuleVersionRequest versionRequest =
                new RuleVersionRequest();

        versionRequest.setVersionNumber(
                nextVersionNumber
        );

        versionRequest.setRuleName(
                updatedRuleName
        );

        versionRequest.setDescription(
                updatedDescription
        );

        versionRequest.setCategory(
                updatedCategory
        );

        versionRequest.setSeverity(
                updatedSeverity
        );

        versionRequest.setPriority(
                updatedPriority
        );

        versionRequest.setOwnerTeam(
                updatedOwnerTeam
        );

        versionRequest.setEffectiveFrom(
                null
        );

        versionRequest.setEffectiveTo(
                null
        );

        versionRequest.setPublicationStatus(
                "DRAFT"
        );

        versionRequest.setChangeSummary(
                request.getChangeReason()
        );

        versionRequest.setCreatedBy(
                request.getChangedBy()
        );

        versionRequest.setApprovedBy(
                null
        );

        RuleVersionResponse createdVersion =
                ruleVersionService
                        .createRuleVersion(
                                ruleId,
                                versionRequest
                        );

        RuleHistoryRequest historyRequest =
                new RuleHistoryRequest();

        historyRequest.setEntityType(
                "RULE"
        );

        historyRequest.setEntityId(
                ruleId
        );

        historyRequest.setOperationType(
                "UPDATE"
        );

        historyRequest.setPreviousValue(
                previousValue
        );

        historyRequest.setCurrentValue(
                currentValue
        );

        historyRequest.setChangeReason(
                request.getChangeReason()
        );

        historyRequest.setChangedBy(
                request.getChangedBy()
        );

        historyRequest.setCorrelationId(
                request.getCorrelationId()
        );

        ruleHistoryService.createRuleHistory(
                historyRequest
        );

        AuditEventRequest auditEventRequest =
                new AuditEventRequest();

        auditEventRequest.setUserId(
                request.getChangedBy()
        );

        auditEventRequest.setEventType(
                "RULE_UPDATED"
        );

        auditEventRequest.setEntityType(
                "RULE"
        );

        auditEventRequest.setEntityId(
                ruleId
        );

        auditEventRequest.setAction(
                "UPDATE"
        );

        auditEventRequest.setSourceComponent(
                "RULE_ENGINE"
        );

        auditEventRequest.setCorrelationId(
                request.getCorrelationId()
        );

        auditEventRequest.setEventResult(
                "SUCCESS"
        );

        Map<String, Object> eventDetails =
                new LinkedHashMap<>();

        eventDetails.put(
                "ruleId",
                ruleId.toString()
        );

        eventDetails.put(
                "ruleVersionId",
                createdVersion
                        .getRuleVersionId()
                        .toString()
        );

        eventDetails.put(
                "versionNumber",
                createdVersion.getVersionNumber()
        );

        eventDetails.put(
                "publicationStatus",
                createdVersion.getPublicationStatus()
        );

        eventDetails.put(
                "changedFields",
                List.copyOf(
                        currentValue.keySet()
                )
        );

        auditEventRequest.setEventDetails(
                eventDetails
        );

        AuditEventResponse auditEvent =
                auditEventService.createAuditEvent(
                        auditEventRequest
                );

        AuditEntityChangeRequest entityChangeRequest =
                new AuditEntityChangeRequest();

        entityChangeRequest.setAuditEventId(
                auditEvent.getAuditEventId()
        );

        entityChangeRequest.setEntityType(
                "RULE"
        );

        entityChangeRequest.setEntityId(
                ruleId
        );

        entityChangeRequest.setOperation(
                "UPDATE"
        );

        entityChangeRequest.setPreviousValue(
                previousValue
        );

        entityChangeRequest.setCurrentValue(
                currentValue
        );

        auditEntityChangeService.createAuditEntityChange(
                entityChangeRequest
        );

        return createdVersion;
    }

    @Override
    @Transactional
    public RuleResponse activateRule(
            UUID ruleId,
            RuleActivationRequest request) {

        if (request.getChangedBy() == null) {
            throw new RequestValidationException(
                    "Rule activation actor is required"
            );
        }

        Rule rule =
                ruleRepository
                        .findByRuleId(ruleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule not found: " + ruleId
                                )
                        );

        if ("ACTIVE".equals(rule.getStatus())) {
            return ruleMapper.toResponse(rule);
        }

        if (!"INACTIVE".equals(rule.getStatus())) {
            throw new ValidationException(
                    "Rule must be INACTIVE before activation"
            );
        }

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        previousValue.put(
                "status",
                rule.getStatus()
        );

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        currentValue.put(
                "status",
                "ACTIVE"
        );

        rule.setStatus(
                "ACTIVE"
        );

        rule.setUpdatedAt(
                LocalDateTime.now()
        );

        Rule savedRule =
                ruleRepository.save(rule);

        RuleHistoryRequest historyRequest =
                new RuleHistoryRequest();

        historyRequest.setEntityType(
                "RULE"
        );

        historyRequest.setEntityId(
                ruleId
        );

        historyRequest.setOperationType(
                "ACTIVATION"
        );

        historyRequest.setPreviousValue(
                previousValue
        );

        historyRequest.setCurrentValue(
                currentValue
        );

        historyRequest.setChangeReason(
                request.getChangeReason()
        );

        historyRequest.setChangedBy(
                request.getChangedBy()
        );

        historyRequest.setCorrelationId(
                request.getCorrelationId()
        );

        ruleHistoryService.createRuleHistory(
                historyRequest
        );

        AuditEventRequest auditEventRequest =
                new AuditEventRequest();

        auditEventRequest.setUserId(
                request.getChangedBy()
        );

        auditEventRequest.setEventType(
                "RULE_ACTIVATED"
        );

        auditEventRequest.setEntityType(
                "RULE"
        );

        auditEventRequest.setEntityId(
                ruleId
        );

        auditEventRequest.setAction(
                "ACTIVATE"
        );

        auditEventRequest.setSourceComponent(
                "RULE_ENGINE"
        );

        auditEventRequest.setCorrelationId(
                request.getCorrelationId()
        );

        auditEventRequest.setEventResult(
                "SUCCESS"
        );

        Map<String, Object> eventDetails =
                new LinkedHashMap<>();

        eventDetails.put(
                "ruleId",
                ruleId.toString()
        );

        eventDetails.put(
                "previousStatus",
                "INACTIVE"
        );

        eventDetails.put(
                "newStatus",
                "ACTIVE"
        );

        auditEventRequest.setEventDetails(
                eventDetails
        );

        AuditEventResponse auditEvent =
                auditEventService.createAuditEvent(
                        auditEventRequest
                );

        AuditEntityChangeRequest entityChangeRequest =
                new AuditEntityChangeRequest();

        entityChangeRequest.setAuditEventId(
                auditEvent.getAuditEventId()
        );

        entityChangeRequest.setEntityType(
                "RULE"
        );

        entityChangeRequest.setEntityId(
                ruleId
        );

        entityChangeRequest.setOperation(
                "UPDATE"
        );

        entityChangeRequest.setPreviousValue(
                previousValue
        );

        entityChangeRequest.setCurrentValue(
                currentValue
        );

        auditEntityChangeService.createAuditEntityChange(
                entityChangeRequest
        );

        return ruleMapper.toResponse(
                savedRule
        );
    }
}