package com.efs.modules.integration.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class IntegrationRetryResponse {

    private UUID retryId;
    private UUID messageId;
    private Integer retryNumber;
    private String errorDescription;
    private LocalDateTime nextRetry;
    private String retryStatus;
    private LocalDateTime createdAt;
    private String errorCode;

    public IntegrationRetryResponse() {
    }

    public UUID getRetryId() {
        return retryId;
    }

    public void setRetryId(UUID retryId) {
        this.retryId = retryId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public Integer getRetryNumber() {
        return retryNumber;
    }

    public void setRetryNumber(Integer retryNumber) {
        this.retryNumber = retryNumber;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public LocalDateTime getNextRetry() {
        return nextRetry;
    }

    public void setNextRetry(LocalDateTime nextRetry) {
        this.nextRetry = nextRetry;
    }

    public String getRetryStatus() {
        return retryStatus;
    }

    public void setRetryStatus(String retryStatus) {
        this.retryStatus = retryStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}