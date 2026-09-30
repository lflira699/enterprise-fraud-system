package com.efs.modules.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_session", schema = "administration")
public class UserSession {

    @Id
    @Column(
            name = "session_id",
            nullable = false
    )
    private UUID sessionId;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "login_time",
            nullable = false
    )
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(
            name = "session_status",
            nullable = false,
            length = 20
    )
    private String sessionStatus;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public UserSession() {
    }

    public UserSession(
            UUID sessionId,
            UUID userId,
            LocalDateTime loginTime,
            String sessionStatus,
            LocalDateTime createdAt) {

        this.sessionId = sessionId;
        this.userId = userId;
        this.loginTime = loginTime;
        this.sessionStatus = sessionStatus;
        this.createdAt = createdAt;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void invalidate(
            LocalDateTime logoutTime) {

        this.logoutTime = logoutTime;
        this.sessionStatus = "INVALIDATED";
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
