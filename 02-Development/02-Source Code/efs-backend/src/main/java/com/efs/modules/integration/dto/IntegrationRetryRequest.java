package com.efs.modules.integration.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class IntegrationRetryRequest {

    private UUID messageId;
    private Integer retryNumber;
    private String errorDescription;
    private LocalDateTime nextRetry;
    private String retryStatus;
    private String errorCode;

    public IntegrationRetryRequest() {
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}