package com.efs.modules.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionLocationResponse {

    private UUID locationId;
    private UUID transactionId;
    private String ipAddress;
    private String countryCode;
    private String state;
    private String city;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long asn;
    private String internetProvider;
    private Boolean vpnDetected;
    private Boolean proxyDetected;
    private Boolean torDetected;
    private LocalDateTime createdAt;

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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
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