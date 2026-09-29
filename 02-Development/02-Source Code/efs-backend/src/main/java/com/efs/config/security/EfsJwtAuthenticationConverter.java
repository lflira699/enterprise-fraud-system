package com.efs.config.security;

import com.efs.modules.administration.service.UserSessionBindingService;
import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.service.AuditLoginServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class EfsJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String FEDERATED_OIDC =
            "FEDERATED_OIDC";

    private static final String LOGIN_SUCCESS =
            "SUCCESS";

    private static final String LOGIN_FAILURE =
            "FAILURE";

    private final EfsJwtSecurityContextMapper
            securityContextMapper;

    private final UserSessionBindingService
            userSessionBindingService;

    private final AuditLoginServiceInterface
            auditLoginService;

    public EfsJwtAuthenticationConverter(
            EfsJwtSecurityContextMapper securityContextMapper,
            UserSessionBindingService userSessionBindingService,
            AuditLoginServiceInterface auditLoginService) {

        this.securityContextMapper =
                securityContextMapper;

        this.userSessionBindingService =
                userSessionBindingService;

        this.auditLoginService =
                auditLoginService;
    }

    @Override
    public AbstractAuthenticationToken convert(
            Jwt jwt) {

        Objects.requireNonNull(
                jwt,
                "jwt is required"
        );

        SecurityContext context =
                securityContextMapper.map(
                        jwt
                );

        try {

            userSessionBindingService.establishSession(
                    context.getSessionId(),
                    context.getUserId()
            );

            userSessionBindingService.requireActiveSession(
                    context.getSessionId(),
                    context.getUserId()
            );

        } catch (RuntimeException exception) {

            auditAuthentication(
                    context.getUserId(),
                    LOGIN_FAILURE,
                    exception.getMessage()
            );

            throw exception;
        }

        auditAuthentication(
                context.getUserId(),
                LOGIN_SUCCESS,
                null
        );

        return new UsernamePasswordAuthenticationToken(
                context,
                null,
                List.of()
        );
    }

    private void auditAuthentication(
            UUID userId,
            String loginResult,
            String failureReason) {

        AuditLoginRequest request =
                new AuditLoginRequest();

        request.setUserId(
                userId
        );

        request.setAuthenticationMethod(
                FEDERATED_OIDC
        );

        request.setLoginResult(
                loginResult
        );

        request.setFailureReason(
                failureReason
        );

        auditLoginService.createAuditLogin(
                request
        );
    }
}
