package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;

public class DetectionScenarioRequest {

    @NotBlank
    @Size(max = 60)
    private String scenarioCode;

    @NotBlank
    @Size(max = 150)
    private String scenarioName;

    @NotBlank
    @Size(max = 500)
    private String objective;

    private String description;

    @NotBlank
    @Size(max = 50)
    private String category;

    @Size(max = 30)
    private String criticality;

    @NotBlank
    @Size(max = 30)
    private String status;

    @Size(max = 120)
    private String owner;

    private Integer version;

    private Integer correlationWindowMinutes;

    private Integer maximumExecutionTimeSeconds;

    private Integer minimumEvents;

    private BigDecimal minimumConfidence;

    private Integer minimumEvidence;

    private Map<String, Object> requiredRules;

    private Map<String, Object> requiredVariables;

    private Map<String, Object> evidenceRequirements;

    private Map<String, Object> exclusions;

    private Map<String, Object> exceptions;

    private Map<String, Object> suggestedActions;

    private Map<String, Object> relatedScenarios;

    private Map<String, Object> configurationContext;

    public String getScenarioCode() {
        return scenarioCode;
    }

    public void setScenarioCode(String scenarioCode) {
        this.scenarioCode = scenarioCode;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getCorrelationWindowMinutes() {
        return correlationWindowMinutes;
    }

    public void setCorrelationWindowMinutes(
            Integer correlationWindowMinutes) {
        this.correlationWindowMinutes =
                correlationWindowMinutes;
    }

    public Integer getMaximumExecutionTimeSeconds() {
        return maximumExecutionTimeSeconds;
    }

    public void setMaximumExecutionTimeSeconds(
            Integer maximumExecutionTimeSeconds) {
        this.maximumExecutionTimeSeconds =
                maximumExecutionTimeSeconds;
    }

    public Integer getMinimumEvents() {
        return minimumEvents;
    }

    public void setMinimumEvents(Integer minimumEvents) {
        this.minimumEvents = minimumEvents;
    }

    public BigDecimal getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(
            BigDecimal minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public Integer getMinimumEvidence() {
        return minimumEvidence;
    }

    public void setMinimumEvidence(Integer minimumEvidence) {
        this.minimumEvidence = minimumEvidence;
    }

    public Map<String, Object> getRequiredRules() {
        return requiredRules;
    }

    public void setRequiredRules(
            Map<String, Object> requiredRules) {
        this.requiredRules = requiredRules;
    }

    public Map<String, Object> getRequiredVariables() {
        return requiredVariables;
    }

    public void setRequiredVariables(
            Map<String, Object> requiredVariables) {
        this.requiredVariables = requiredVariables;
    }

    public Map<String, Object> getEvidenceRequirements() {
        return evidenceRequirements;
    }

    public void setEvidenceRequirements(
            Map<String, Object> evidenceRequirements) {
        this.evidenceRequirements =
                evidenceRequirements;
    }

    public Map<String, Object> getExclusions() {
        return exclusions;
    }

    public void setExclusions(
            Map<String, Object> exclusions) {
        this.exclusions = exclusions;
    }

    public Map<String, Object> getExceptions() {
        return exceptions;
    }

    public void setExceptions(
            Map<String, Object> exceptions) {
        this.exceptions = exceptions;
    }

    public Map<String, Object> getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(
            Map<String, Object> suggestedActions) {
        this.suggestedActions = suggestedActions;
    }

    public Map<String, Object> getRelatedScenarios() {
        return relatedScenarios;
    }

    public void setRelatedScenarios(
            Map<String, Object> relatedScenarios) {
        this.relatedScenarios = relatedScenarios;
    }

    public Map<String, Object> getConfigurationContext() {
        return configurationContext;
    }

    public void setConfigurationContext(
            Map<String, Object> configurationContext) {
        this.configurationContext =
                configurationContext;
    }
}