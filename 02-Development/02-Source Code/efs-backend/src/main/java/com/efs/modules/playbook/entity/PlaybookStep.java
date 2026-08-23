package com.efs.modules.playbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "playbook_step", schema = "playbook")
public class PlaybookStep {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "playbook_step_id", nullable = false)
    private UUID playbookStepId;

    @Column(name = "playbook_version_id", nullable = false)
    private UUID playbookVersionId;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "step_name", nullable = false, length = 180)
    private String stepName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "expected_duration_minutes")
    private Integer expectedDurationMinutes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PlaybookStep() {
    }

    public UUID getPlaybookStepId() {
        return playbookStepId;
    }

    public void setPlaybookStepId(UUID playbookStepId) {
        this.playbookStepId = playbookStepId;
    }

    public UUID getPlaybookVersionId() {
        return playbookVersionId;
    }

    public void setPlaybookVersionId(UUID playbookVersionId) {
        this.playbookVersionId = playbookVersionId;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Integer getExpectedDurationMinutes() {
        return expectedDurationMinutes;
    }

    public void setExpectedDurationMinutes(Integer expectedDurationMinutes) {
        this.expectedDurationMinutes = expectedDurationMinutes;
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