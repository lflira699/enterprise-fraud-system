package com.efs.modules.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "country", schema = "catalog")
public class Country {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "country_id", nullable = false)
    private UUID countryId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "country_code",
            nullable = false,
            length = 2,
            columnDefinition = "char(2)"
    )
    private String countryCode;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "alpha3_code",
            nullable = false,
            length = 3,
            columnDefinition = "char(3)"
    )
    private String alpha3Code;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "numeric_code",
            length = 3,
            columnDefinition = "char(3)"
    )
    private String numericCode;

    @Column(
            name = "country_name",
            nullable = false,
            length = 150
    )
    private String countryName;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public Country() {
    }

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
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