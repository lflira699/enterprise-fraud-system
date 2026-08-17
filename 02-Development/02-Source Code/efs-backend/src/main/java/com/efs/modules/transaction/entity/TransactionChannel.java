package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_channel", schema = "transaction")
public class TransactionChannel {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "channel_transaction_id", nullable = false)
    private UUID channelTransactionId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "channel_type", nullable = false, length = 40)
    private String channelType;

    @Column(name = "application_name", length = 120)
    private String applicationName;

    @Column(name = "application_version", length = 50)
    private String applicationVersion;

    @Column(name = "sdk_version", length = 50)
    private String sdkVersion;

    @Column(name = "api_version", length = 50)
    private String apiVersion;

    @Column(name = "authentication_method", length = 60)
    private String authenticationMethod;

    @Column(name = "session_duration")
    private Integer sessionDuration;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransactionChannel() {
    }

    public UUID getChannelTransactionId() {
        return channelTransactionId;
    }

    public void setChannelTransactionId(UUID channelTransactionId) {
        this.channelTransactionId = channelTransactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public Integer getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Integer sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}