package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.dto.AuditLoginResponse;

import java.util.List;
import java.util.UUID;

public interface AuditLoginServiceInterface {

    AuditLoginResponse createAuditLogin(
            AuditLoginRequest request
    );

    AuditLoginResponse getAuditLoginById(
            UUID loginId
    );

    List<AuditLoginResponse> getAuditLoginsByUserId(
            UUID userId
    );

    List<AuditLoginResponse> getAuditLoginsByLoginResult(
            String loginResult
    );

    List<AuditLoginResponse> getAuditLoginsByIpAddress(
            String ipAddress
    );

    List<AuditLoginResponse> getAuditLoginsByAuthenticationMethod(
            String authenticationMethod
    );
}