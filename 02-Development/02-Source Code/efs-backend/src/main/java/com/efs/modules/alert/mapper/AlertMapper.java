package com.efs.modules.alert.mapper;

import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.entity.Alert;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public Alert toEntity(
            AlertRequest request) {

        Alert alert =
                new Alert();

        alert.setAlertReference(
                request.getAlertReference()
        );

        alert.setCustomerId(
                request.getCustomerId()
        );

        alert.setTransactionId(
                request.getTransactionId()
        );

        alert.setDecisionId(
                request.getDecisionId()
        );

        alert.setRiskAssessmentId(
                request.getRiskAssessmentId()
        );

        alert.setScenarioId(
                request.getScenarioId()
        );

        alert.setRuleId(
                request.getRuleId()
        );

        alert.setAlertType(
                request.getAlertType()
        );

        alert.setCategory(
                request.getCategory()
        );

        alert.setSeverity(
                request.getSeverity()
        );

        alert.setPriority(
                request.getPriority()
        );

        alert.setPriorityScore(
                request.getPriorityScore()
        );

        alert.setTitle(
                request.getTitle()
        );

        alert.setDescription(
                request.getDescription()
        );

        alert.setRiskScore(
                request.getRiskScore()
        );

        alert.setCorrelationId(
                request.getCorrelationId()
        );

        alert.setAssignedTo(
                request.getAssignedTo()
        );

        alert.setAssignedTeam(
                request.getAssignedTeam()
        );

        alert.setDueAt(
                request.getDueAt()
        );

        return alert;
    }

    public AlertResponse toResponse(
            Alert alert) {

        AlertResponse response =
                new AlertResponse();

        response.setAlertId(
                alert.getAlertId()
        );

        response.setAlertReference(
                alert.getAlertReference()
        );

        response.setCustomerId(
                alert.getCustomerId()
        );

        response.setTransactionId(
                alert.getTransactionId()
        );

        response.setDecisionId(
                alert.getDecisionId()
        );

        response.setRiskAssessmentId(
                alert.getRiskAssessmentId()
        );

        response.setScenarioId(
                alert.getScenarioId()
        );

        response.setRuleId(
                alert.getRuleId()
        );

        response.setAlertType(
                alert.getAlertType()
        );

        response.setCategory(
                alert.getCategory()
        );

        response.setSeverity(
                alert.getSeverity()
        );

        response.setPriority(
                alert.getPriority()
        );

        response.setPriorityScore(
                alert.getPriorityScore()
        );

        response.setStatus(
                alert.getStatus()
        );

        response.setTitle(
                alert.getTitle()
        );

        response.setDescription(
                alert.getDescription()
        );

        response.setRiskScore(
                alert.getRiskScore()
        );

        response.setCorrelationId(
                alert.getCorrelationId()
        );

        response.setAssignedTo(
                alert.getAssignedTo()
        );

        response.setAssignedTeam(
                alert.getAssignedTeam()
        );

        response.setDueAt(
                alert.getDueAt()
        );

        response.setGeneratedAt(
                alert.getGeneratedAt()
        );

        response.setClosedAt(
                alert.getClosedAt()
        );

        response.setClosureReason(
                alert.getClosureReason()
        );

        response.setCreatedAt(
                alert.getCreatedAt()
        );

        response.setUpdatedAt(
                alert.getUpdatedAt()
        );

        response.setRecordVersion(
                alert.getRecordVersion()
        );

        return response;
    }
}