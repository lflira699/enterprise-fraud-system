package com.efs.modules.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "scenario", schema = "detection")
public class DetectionScenario {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(name = "scenario_code", nullable = false, length = 60)
    private String scenarioCode;

    @Column(name = "scenario_name", nullable = false, length = 150)
    private String scenarioName;

    @Column(name = "objective", nullable = false, length = 500)
    private String objective;

    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "criticality", length = 30)
    private String criticality;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "owner", length = 120)
    private String owner;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "correlation_window_minutes")
    private Integer correlationWindowMinutes;

    @Column(name = "maximum_execution_time_seconds")
    private Integer maximumExecutionTimeSeconds;

    @Column(name = "minimum_events")
    private Integer minimumEvents;

    @Column(name = "minimum_confidence", precision = 8, scale = 4)
    private BigDecimal minimumConfidence;

    @Column(name = "minimum_evidence")
    private Integer minimumEvidence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_rules", columnDefinition = "jsonb")
    private Map<String, Object> requiredRules;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_variables", columnDefinition = "jsonb")
    private Map<String, Object> requiredVariables;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence_requirements", columnDefinition = "jsonb")
    private Map<String, Object> evidenceRequirements;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exclusions", columnDefinition = "jsonb")
    private Map<String, Object> exclusions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exceptions", columnDefinition = "jsonb")
    private Map<String, Object> exceptions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "suggested_actions", columnDefinition = "jsonb")
    private Map<String, Object> suggestedActions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "related_scenarios", columnDefinition = "jsonb")
    private Map<String, Object> relatedScenarios;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuration_context", columnDefinition = "jsonb")
    private Map<String, Object> configurationContext;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public DetectionScenario() {
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(UUID scenarioId) {
        this.scenarioId = scenarioId;
    }

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
        this.correlationWindowMinutes = correlationWindowMinutes;
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
        this.configurationContext = configurationContext;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}