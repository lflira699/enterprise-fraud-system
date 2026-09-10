package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CaseUpdateRequest {

    @Size(max = 20)
    private String priority;

    private LocalDateTime dueDate;

    public String getPriority() {
        return priority;
    }

    public void setPriority(
            String priority) {

        this.priority =
                priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(
            LocalDateTime dueDate) {

        this.dueDate =
                dueDate;
    }
}
