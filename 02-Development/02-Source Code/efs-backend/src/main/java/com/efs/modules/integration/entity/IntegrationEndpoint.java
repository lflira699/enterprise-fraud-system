package com.efs.modules.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "integration_endpoint",
        schema = "integration"
)
public class IntegrationEndpoint {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "endpoint_id", nullable = false)
    private UUID endpointId;

    @Column(
            name = "endpoint_code",
            nullable = false,
            length = 60,
            unique = true
    )
    private String endpointCode;

    @Column(
            name = "endpoint_name",
            nullable = false,
            length = 150
    )
    private String endpointName;

    @Column(
            name = "endpoint_url",
            nullable = false,
            length = 500
    )
    private String endpointUrl;

    @Column(
            name = "protocol",
            nullable = false,
            length = 20
    )
    private String protocol;

    @Column(
            name = "authentication_type",
            nullable = false,
            length = 30
    )
    private String authenticationType;

    @Column(
            name = "timeout_seconds",
            nullable = false
    )
    private Integer timeoutSeconds;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public IntegrationEndpoint() {
    }

    public UUID getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(UUID endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointCode() {
        return endpointCode;
    }

    public void setEndpointCode(String endpointCode) {
        this.endpointCode = endpointCode;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
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
}