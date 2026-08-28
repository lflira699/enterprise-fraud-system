package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.dto.AuditLoginResponse;
import com.efs.modules.audit.entity.AuditLogin;
import org.springframework.stereotype.Component;

@Component
public class AuditLoginMapper {

    public AuditLogin toEntity(
            AuditLoginRequest request) {

        AuditLogin auditLogin =
                new AuditLogin();

        auditLogin.setUserId(
                request.getUserId()
        );

        auditLogin.setIpAddress(
                request.getIpAddress()
        );

        auditLogin.setDeviceFingerprint(
                request.getDeviceFingerprint()
        );

        auditLogin.setAuthenticationMethod(
                request.getAuthenticationMethod()
        );

        auditLogin.setMfaResult(
                request.getMfaResult()
        );

        auditLogin.setLoginResult(
                request.getLoginResult()
        );

        auditLogin.setFailureReason(
                request.getFailureReason()
        );

        auditLogin.setCountryCode(
                request.getCountryCode()
        );

        return auditLogin;
    }

    public AuditLoginResponse toResponse(
            AuditLogin auditLogin) {

        AuditLoginResponse response =
                new AuditLoginResponse();

        response.setLoginId(
                auditLogin.getLoginId()
        );

        response.setUserId(
                auditLogin.getUserId()
        );

        response.setLoginTimestamp(
                auditLogin.getLoginTimestamp()
        );

        response.setIpAddress(
                auditLogin.getIpAddress()
        );

        response.setDeviceFingerprint(
                auditLogin.getDeviceFingerprint()
        );

        response.setAuthenticationMethod(
                auditLogin.getAuthenticationMethod()
        );

        response.setMfaResult(
                auditLogin.getMfaResult()
        );

        response.setLoginResult(
                auditLogin.getLoginResult()
        );

        response.setFailureReason(
                auditLogin.getFailureReason()
        );

        response.setCountryCode(
                auditLogin.getCountryCode()
        );

        response.setCreatedAt(
                auditLogin.getCreatedAt()
        );

        return response;
    }
}