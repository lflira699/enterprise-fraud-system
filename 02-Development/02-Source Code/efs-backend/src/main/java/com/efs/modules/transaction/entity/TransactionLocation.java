package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_location", schema = "transaction")
public class TransactionLocation {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "state", length = 120)
    private String state;

    @Column(name = "city", length = 120)
    private String city;

    @Column(name = "postal_code", length = 30)
    private String postalCode;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "asn")
    private Long asn;

    @Column(name = "internet_provider", length = 150)
    private String internetProvider;

    @Column(name = "vpn_detected", nullable = false)
    private Boolean vpnDetected;

    @Column(name = "proxy_detected", nullable = false)
    private Boolean proxyDetected;

    @Column(name = "tor_detected", nullable = false)
    private Boolean torDetected;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransactionLocation() {
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Long getAsn() {
        return asn;
    }

    public void setAsn(Long asn) {
        this.asn = asn;
    }

    public String getInternetProvider() {
        return internetProvider;
    }

    public void setInternetProvider(String internetProvider) {
        this.internetProvider = internetProvider;
    }

    public Boolean getVpnDetected() {
        return vpnDetected;
    }

    public void setVpnDetected(Boolean vpnDetected) {
        this.vpnDetected = vpnDetected;
    }

    public Boolean getProxyDetected() {
        return proxyDetected;
    }

    public void setProxyDetected(Boolean proxyDetected) {
        this.proxyDetected = proxyDetected;
    }

    public Boolean getTorDetected() {
        return torDetected;
    }

    public void setTorDetected(Boolean torDetected) {
        this.torDetected = torDetected;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}