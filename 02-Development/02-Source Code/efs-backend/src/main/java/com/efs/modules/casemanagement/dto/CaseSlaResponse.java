package com.efs.modules.casemanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseSlaResponse {

    private UUID slaId;
    private UUID caseId;
    private String slaType;
    private Integer targetMinutes;
    private Integer elapsedMinutes;
    private LocalDateTime deadline;
    private Boolean breached;
    private String breachReason;
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