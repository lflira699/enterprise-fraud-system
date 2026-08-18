package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "transaction_metadata", schema = "transaction")
public class TransactionMetadata {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "metadata_id", nullable = false)
    private UUID metadataId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "metadata_type", nullable = false, length = 60)
    private String metadataType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadataJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransactionMetadata() {
    }

    public UUID getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(UUID metadataId) {
        this.metadataId = metadataId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public Map<String, Object> getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(Map<String, Object> metadataJson) {
        this.metadataJson = metadataJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}