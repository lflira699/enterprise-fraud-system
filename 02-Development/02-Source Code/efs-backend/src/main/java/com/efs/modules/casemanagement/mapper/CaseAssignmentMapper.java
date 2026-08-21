package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.entity.CaseAssignment;
import org.springframework.stereotype.Component;

@Component
public class CaseAssignmentMapper {

    public CaseAssignment toEntity(
            CaseAssignmentRequest request) {

        CaseAssignment assignment =
                new CaseAssignment();

        assignment.setAssignedFrom(
                request.getAssignedFrom()
        );

        assignment.setAssignedTo(
                request.getAssignedTo()
        );

        assignment.setAssignedTeam(
                request.getAssignedTeam()
        );

        assignment.setAssignmentReason(
                request.getAssignmentReason()
        );

        return assignment;
    }

    public CaseAssignmentResponse toResponse(
            CaseAssignment assignment) {

        CaseAssignmentResponse response =
                new CaseAssignmentResponse();

        response.setAssignmentId(
                assignment.getAssignmentId()
        );

        response.setCaseId(
                assignment.getCaseId()
        );

        response.setAssignedFrom(
                assignment.getAssignedFrom()
        );

        response.setAssignedTo(
                assignment.getAssignedTo()
        );

        response.setAssignedTeam(
                assignment.getAssignedTeam()
        );

        response.setAssignmentReason(
                assignment.getAssignmentReason()
        );

        response.setAssignedAt(
                assignment.getAssignedAt()
        );

        response.setAcceptedAt(
                assignment.getAcceptedAt()
        );

        response.setReleasedAt(
                assignment.getReleasedAt()
        );

        return response;
    }
}