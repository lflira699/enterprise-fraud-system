package com.efs.modules.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TimezoneResponse {

    private UUID timezoneId;
    private String timezoneCode;
    private String timezoneName;
    private String status;
    private LocalDateTime createdAt;

    public UUID getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(UUID timezoneId) {
        this.timezoneId = timezoneId;
    }

    public String getTimezoneCode() {
        return timezoneCode;
    }

    public void setTimezoneCode(String timezoneCode) {
        this.timezoneCode = timezoneCode;
    }

    public String getTimezoneName() {
        return timezoneName;
    }

    public void setTimezoneName(String timezoneName) {
        this.timezoneName = timezoneName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}