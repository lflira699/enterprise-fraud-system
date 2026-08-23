package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CaseSlaRequest {

    @NotBlank
    @Size(max = 40)
    private String slaType;

    @NotNull
    private Integer targetMinutes;

    private Integer elapsedMinutes;

    @NotNull
    private LocalDateTime deadline;

    private Boolean breached;

    private String breachReason;

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
}