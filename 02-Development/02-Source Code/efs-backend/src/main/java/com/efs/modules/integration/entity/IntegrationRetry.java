package com.efs.modules.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "integration_retry",
        schema = "integration"
)
public class IntegrationRetry {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "retry_id", nullable = false)
    private UUID retryId;

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "retry_number", nullable = false)
    private Integer retryNumber;

    @Column(
            name = "error_description",
            columnDefinition = "TEXT"
    )
    private String errorDescription;

    @Column(name = "next_retry")
    private LocalDateTime nextRetry;

    @Column(
            name = "retry_status",
            nullable = false,
            length = 20
    )
    private String retryStatus;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "error_code",
            length = 50
    )
    private String errorCode;

    public IntegrationRetry() {
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