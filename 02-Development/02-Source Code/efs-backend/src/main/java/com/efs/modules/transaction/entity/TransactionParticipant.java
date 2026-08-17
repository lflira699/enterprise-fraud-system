package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_participant", schema = "transaction")
public class TransactionParticipant {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "participant_id", nullable = false)
    private UUID participantId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "participant_type", nullable = false, length = 40)
    private String participantType;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "external_identifier", length = 150)
    private String externalIdentifier;

    @Column(name = "institution_id")
    private UUID institutionId;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransactionParticipant() {
    }

    public UUID getParticipantId() {
        return participantId;
    }

    public void setParticipantId(UUID participantId) {
        this.participantId = participantId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public UUID getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(UUID institutionId) {
        this.institutionId = institutionId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}