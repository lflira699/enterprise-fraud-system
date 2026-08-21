package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.entity.Case;
import org.springframework.stereotype.Component;

@Component
public class CaseMapper {

    public Case toEntity(
            CaseRequest request) {

        Case caseEntity =
                new Case();

        caseEntity.setCaseNumber(
                request.getCaseNumber()
        );

        caseEntity.setOrganizationId(
                request.getOrganizationId()
        );

        caseEntity.setTransactionId(
                request.getTransactionId()
        );

        caseEntity.setCustomerId(
                request.getCustomerId()
        );

        caseEntity.setCaseType(
                request.getCaseType()
        );

        caseEntity.setCategory(
                request.getCategory()
        );

        caseEntity.setSeverity(
                request.getSeverity()
        );

        caseEntity.setPriority(
                request.getPriority()
        );

        caseEntity.setAssignedTeam(
                request.getAssignedTeam()
        );

        caseEntity.setAssignedUser(
                request.getAssignedUser()
        );

        caseEntity.setDueDate(
                request.getDueDate()
        );

        caseEntity.setTenantId(
                request.getTenantId()
        );

        return caseEntity;
    }

    public CaseResponse toResponse(
            Case caseEntity) {

        CaseResponse response =
                new CaseResponse();

        response.setCaseId(
                caseEntity.getCaseId()
        );

        response.setCaseNumber(
                caseEntity.getCaseNumber()
        );

        response.setOrganizationId(
                caseEntity.getOrganizationId()
        );

        response.setTransactionId(
                caseEntity.getTransactionId()
        );

        response.setCustomerId(
                caseEntity.getCustomerId()
        );

        response.setCaseType(
                caseEntity.getCaseType()
        );

        response.setCategory(
                caseEntity.getCategory()
        );

        response.setSeverity(
                caseEntity.getSeverity()
        );

        response.setPriority(
                caseEntity.getPriority()
        );

        response.setCurrentStatus(
                caseEntity.getCurrentStatus()
        );

        response.setAssignedTeam(
                caseEntity.getAssignedTeam()
        );

        response.setAssignedUser(
                caseEntity.getAssignedUser()
        );

        response.setCreatedAt(
                caseEntity.getCreatedAt()
        );

        response.setUpdatedAt(
                caseEntity.getUpdatedAt()
        );

        response.setDueDate(
                caseEntity.getDueDate()
        );

        response.setClosedAt(
                caseEntity.getClosedAt()
        );

        response.setTenantId(
                caseEntity.getTenantId()
        );

        return response;
    }
}