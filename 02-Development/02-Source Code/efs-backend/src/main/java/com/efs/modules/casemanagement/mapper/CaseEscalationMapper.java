package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseEscalationRequest;
import com.efs.modules.casemanagement.dto.CaseEscalationResponse;
import com.efs.modules.casemanagement.entity.CaseEscalation;
import org.springframework.stereotype.Component;

@Component
public class CaseEscalationMapper {

    public CaseEscalation toEntity(
            CaseEscalationRequest request) {

        CaseEscalation escalation =
                new CaseEscalation();

        escalation.setEscalationLevel(
                request.getEscalationLevel()
        );

        escalation.setFromTeam(
                request.getFromTeam()
        );

        escalation.setToTeam(
                request.getToTeam()
        );

        escalation.setEscalationReason(
                request.getEscalationReason()
        );

        escalation.setEscalatedBy(
                request.getEscalatedBy()
        );

        escalation.setResolvedAt(
                request.getResolvedAt()
        );

        return escalation;
    }

    public CaseEscalationResponse toResponse(
            CaseEscalation escalation) {

        CaseEscalationResponse response =
                new CaseEscalationResponse();

        response.setEscalationId(
                escalation.getEscalationId()
        );

        response.setCaseId(
                escalation.getCaseId()
        );

        response.setEscalationLevel(
                escalation.getEscalationLevel()
        );

        response.setFromTeam(
                escalation.getFromTeam()
        );

        response.setToTeam(
                escalation.getToTeam()
        );

        response.setEscalationReason(
                escalation.getEscalationReason()
        );

        response.setEscalatedBy(
                escalation.getEscalatedBy()
        );

        response.setEscalatedAt(
                escalation.getEscalatedAt()
        );

        response.setResolvedAt(
                escalation.getResolvedAt()
        );

        return response;
    }
}