package com.efs.modules.casemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "case_sla",
        schema = "case_management"
)
public class CaseSla {

    @Id
    @GeneratedValue
    @Column(
            name = "sla_id",
            nullable = false
    )
    private UUID slaId;

    @Column(
            name = "case_id",
            nullable = false
    )
    private UUID caseId;

    @Column(
            name = "sla_type",
            nullable = false,
            length = 40
    )
    private String slaType;

    @Column(
            name = "target_minutes",
            nullable = false
    )
    private Integer targetMinutes;

    @Column(
            name = "elapsed_minutes",
            nullable = false
    )
    private Integer elapsedMinutes = 0;

    @Column(
            name = "deadline",
            nullable = false
    )
    private LocalDateTime deadline;

    @Column(
            name = "breached",
            nullable = false
    )
    private Boolean breached = false;

    @Column(
            name = "breach_reason"
    )
    private String breachReason;

    @Column(
            name = "calculated_at",
            nullable = false
    )
    private LocalDateTime calculatedAt;

    public UUID getSlaId() {
        return slaId;
    }

    public void setSlaId(
            UUID slaId) {

        this.slaId =
                slaId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(
            UUID caseId) {

        this.caseId =
                caseId;
    }

    public String getSlaType() {
        return slaType;
    }

    public void setSlaType(
            String slaType) {

        this.slaType =
                slaType;
    }

    public Integer getTargetMinutes() {
        return targetMinutes;
    }

    public void setTargetMinutes(
            Integer targetMinutes) {

        this.targetMinutes =
                targetMinutes;
    }

    public Integer getElapsedMinutes() {
        return elapsedMinutes;
    }

    public void setElapsedMinutes(
            Integer elapsedMinutes) {

        this.elapsedMinutes =
                elapsedMinutes;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(
            LocalDateTime deadline) {

        this.deadline =
                deadline;
    }

    public Boolean getBreached() {
        return breached;
    }

    public void setBreached(
            Boolean breached) {

        this.breached =
                breached;
    }

    public String getBreachReason() {
        return breachReason;
    }

    public void setBreachReason(
            String breachReason) {

        this.breachReason =
                breachReason;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(
            LocalDateTime calculatedAt) {

        this.calculatedAt =
                calculatedAt;
    }
}