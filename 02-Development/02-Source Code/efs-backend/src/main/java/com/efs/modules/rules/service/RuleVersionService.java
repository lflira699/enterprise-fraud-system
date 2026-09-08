package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleVersionPublishRequest;
import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.entity.RuleVersion;
import com.efs.modules.rules.mapper.RuleVersionMapper;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.modules.rules.repository.RuleSimulationRepository;
import com.efs.modules.rules.repository.RuleVersionRepository;
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
public class RuleVersionService
        implements RuleVersionServiceInterface {

    private static final String PUBLICATION_STATUS_DRAFT =
            "DRAFT";

    private static final String PUBLICATION_STATUS_PUBLISHED =
            "PUBLISHED";

    private static final String ENTITY_TYPE_RULE_VERSION =
            "RULE_VERSION";

    private static final String SIMULATION_SOURCE_CONTROLLED_RULE_TEST =
            "CONTROLLED_RULE_TEST";

    private static final String SIMULATION_STATUS_COMPLETED =
            "COMPLETED";

    private final RuleVersionRepository ruleVersionRepository;
    private final RuleRepository ruleRepository;
    private final RuleSimulationRepository ruleSimulationRepository;
    private final RuleVersionMapper ruleVersionMapper;
    private final RuleHistoryServiceInterface ruleHistoryService;
    private final AuditEventServiceInterface auditEventService;
    private final AuditEntityChangeServiceInterface auditEntityChangeService;

    public RuleVersionService(
            RuleVersionRepository ruleVersionRepository,
            RuleRepository ruleRepository,
            RuleSimulationRepository ruleSimulationRepository,
            RuleVersionMapper ruleVersionMapper,
            RuleHistoryServiceInterface ruleHistoryService,
            AuditEventServiceInterface auditEventService,
            AuditEntityChangeServiceInterface auditEntityChangeService) {

        this.ruleVersionRepository = ruleVersionRepository;
        this.ruleRepository = ruleRepository;
        this.ruleSimulationRepository = ruleSimulationRepository;
        this.ruleVersionMapper = ruleVersionMapper;
        this.ruleHistoryService = ruleHistoryService;
        this.auditEventService = auditEventService;
        this.auditEntityChangeService =
                auditEntityChangeService;
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

        if (PUBLICATION_STATUS_PUBLISHED.equals(
                request.getPublicationStatus()
        )) {
            throw new ValidationException(
                    "Rule version cannot be created as PUBLISHED; use publication workflow"
            );
        }

        RuleVersion ruleVersion =
                ruleVersionMapper.toEntity(request);

        ruleVersion.setRuleId(ruleId);
        ruleVersion.setCreatedAt(LocalDateTime.now());

        RuleVersion savedRuleVersion =
                ruleVersionRepository.save(ruleVersion);

        return ruleVersionMapper.toResponse(savedRuleVersion);
    }

    @Override
    @Transactional
    public RuleVersionResponse publishRuleVersion(
            UUID ruleId,
            UUID ruleVersionId,
            RuleVersionPublishRequest request) {

        if (request == null
                || request.getApprovedBy() == null) {

            throw new RequestValidationException(
                    "Rule version publication actor is required"
            );
        }

        Rule rule =
                ruleRepository
                        .findByRuleId(
                                ruleId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule not found: "
                                                + ruleId
                                )
                        );

        RuleVersion ruleVersion =
                ruleVersionRepository
                        .findByRuleVersionId(
                                ruleVersionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule version not found: "
                                                + ruleVersionId
                                )
                        );

        if (!ruleId.equals(
                ruleVersion.getRuleId()
        )) {
            throw new ValidationException(
                    "Rule version does not belong to rule"
            );
        }

        if (PUBLICATION_STATUS_PUBLISHED.equals(
                ruleVersion.getPublicationStatus()
        )) {
            throw new ValidationException(
                    "Rule version is already published"
            );
        }

        if (!PUBLICATION_STATUS_DRAFT.equals(
                ruleVersion.getPublicationStatus()
        )) {
            throw new ValidationException(
                    "Rule version must be DRAFT before publication"
            );
        }

        boolean hasCompletedControlledTest =
                ruleSimulationRepository
                        .existsByEntityTypeAndEntityIdAndSimulationSourceAndSimulationStatusAndCompletedAtIsNotNull(
                                ENTITY_TYPE_RULE_VERSION,
                                ruleVersionId,
                                SIMULATION_SOURCE_CONTROLLED_RULE_TEST,
                                SIMULATION_STATUS_COMPLETED
                        );

        if (!hasCompletedControlledTest) {
            throw new ValidationException(
                    "Rule version requires a completed controlled test before publication"
            );
        }

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        previousValue.put(
                "publicationStatus",
                PUBLICATION_STATUS_DRAFT
        );

        LocalDateTime publishedAt =
                LocalDateTime.now();

        ruleVersion.setPublicationStatus(
                PUBLICATION_STATUS_PUBLISHED
        );

        ruleVersion.setApprovedBy(
                request.getApprovedBy()
        );

        ruleVersion.setEffectiveFrom(
                publishedAt
        );

        rule.setCurrentVersion(
                ruleVersion.getVersionNumber()
        );

        rule.setUpdatedAt(
                publishedAt
        );

        ruleRepository.save(
                rule
        );

        RuleVersion savedRuleVersion =
                ruleVersionRepository.save(
                        ruleVersion
                );

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        currentValue.put(
                "publicationStatus",
                PUBLICATION_STATUS_PUBLISHED
        );

        currentValue.put(
                "approvedBy",
                request.getApprovedBy().toString()
        );

        currentValue.put(
                "effectiveFrom",
                publishedAt.toString()
        );

        RuleHistoryRequest historyRequest =
                new RuleHistoryRequest();

        historyRequest.setEntityType(
                ENTITY_TYPE_RULE_VERSION
        );

        historyRequest.setEntityId(
                ruleVersionId
        );

        historyRequest.setOperationType(
                "PUBLICATION"
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
                request.getApprovedBy()
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
                request.getApprovedBy()
        );

        auditEventRequest.setEventType(
                "RULE_VERSION_PUBLISHED"
        );

        auditEventRequest.setEntityType(
                ENTITY_TYPE_RULE_VERSION
        );

        auditEventRequest.setEntityId(
                ruleVersionId
        );

        auditEventRequest.setAction(
                "PUBLISH"
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
                ruleVersionId.toString()
        );

        eventDetails.put(
                "versionNumber",
                ruleVersion.getVersionNumber()
        );

        eventDetails.put(
                "previousPublicationStatus",
                PUBLICATION_STATUS_DRAFT
        );

        eventDetails.put(
                "newPublicationStatus",
                PUBLICATION_STATUS_PUBLISHED
        );

        eventDetails.put(
                "approvedBy",
                request.getApprovedBy().toString()
        );

        eventDetails.put(
                "effectiveFrom",
                publishedAt.toString()
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
                ENTITY_TYPE_RULE_VERSION
        );

        entityChangeRequest.setEntityId(
                ruleVersionId
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

        return ruleVersionMapper.toResponse(
                savedRuleVersion
        );
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