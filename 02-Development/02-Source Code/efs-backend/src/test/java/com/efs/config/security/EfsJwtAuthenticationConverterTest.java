package com.efs.config.security;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.administration.service.UserSessionBindingService;
import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.service.AuditLoginServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EfsJwtAuthenticationConverterTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "18318318-3183-4183-8183-183183183183"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "18418418-4184-4184-8184-184184184184"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "18618618-6186-4186-8186-186186186186"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "18518518-5185-4185-8185-185185185185"
            );

    private EfsJwtSecurityContextMapper
            securityContextMapper;

    private UserSessionBindingService
            userSessionBindingService;

    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private AuditLoginServiceInterface
            auditLoginService;

    private EfsJwtAuthenticationConverter
            converter;

    private SecurityContext
            securityContext;

    @BeforeEach
    void setUp() {

        securityContextMapper =
                mock(
                        EfsJwtSecurityContextMapper.class
                );

        userSessionBindingService =
                mock(
                        UserSessionBindingService.class
                );

        userAccountLookupService =
                mock(
                        UserAccountLookupServiceInterface.class
                );

        auditLoginService =
                mock(
                        AuditLoginServiceInterface.class
                );

        converter =
                new EfsJwtAuthenticationConverter(
                        securityContextMapper,
                        userSessionBindingService,
                        userAccountLookupService,
                        auditLoginService
                );

        securityContext =
                mock(
                        SecurityContext.class
                );

        when(
                securityContext.getUserId()
        ).thenReturn(
                USER_ID
        );

        when(
                securityContext.getSessionId()
        ).thenReturn(
                SESSION_ID
        );
        when(
                securityContext.getTenantId()
        ).thenReturn(
                TENANT_ID
        );

        when(
                securityContext.getScopes()
        ).thenReturn(
                Set.of(
                        "openid"
                )
        );

        when(
                userAccountLookupService.getAuthorizedUser(
                        USER_ID
                )
        ).thenReturn(
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "efs.development.investigator@example.invalid"
                )
        );

        when(
                userAccountLookupService.getAuthorizedRoleCodes(
                        USER_ID
                )
        ).thenReturn(
                Set.of(
                        "EFS_DEVELOPMENT_INVESTIGATOR"
                )
        );

        when(
                userAccountLookupService.getAuthorizedPermissionCodes(
                        USER_ID
                )
        ).thenReturn(
                Set.of(
                        "event.view"
                )
        );
    }

    @Test
    void shouldEstablishAndValidateSessionBeforeSuccessfulAuthenticationAudit() {

        Jwt jwt =
                jwt();

        when(
                securityContextMapper.map(
                        jwt
                )
        ).thenReturn(
                securityContext
        );

        AbstractAuthenticationToken authentication =
                converter.convert(
                        jwt
                );

        assertTrue(
                authentication.isAuthenticated()
        );

        SecurityContext authenticatedContext =
                (SecurityContext)
                        authentication.getPrincipal();

        assertEquals(
                USER_ID,
                authenticatedContext.getUserId()
        );

        assertEquals(
                TENANT_ID,
                authenticatedContext.getTenantId()
        );

        assertEquals(
                SESSION_ID,
                authenticatedContext.getSessionId()
        );

        assertTrue(
                authenticatedContext.hasRole(
                        "EFS_DEVELOPMENT_INVESTIGATOR"
                )
        );

        assertTrue(
                authenticatedContext.hasPermission(
                        "event.view"
                )
        );

        assertTrue(
                authenticatedContext.hasScope(
                        "openid"
                )
        );

        verify(
                userSessionBindingService,
                times(1)
        ).establishSession(
                SESSION_ID,
                USER_ID
        );

        verify(
                userSessionBindingService,
                times(1)
        ).requireActiveSession(
                SESSION_ID,
                USER_ID
        );

        verify(
                auditLoginService,
                times(1)
        ).createAuditLogin(
                any(
                        AuditLoginRequest.class
                )
        );
    }

    @Test
    void shouldPersistSuccessfulFederatedOidcAuthenticationAudit() {

        Jwt jwt =
                jwt();

        when(
                securityContextMapper.map(
                        jwt
                )
        ).thenReturn(
                securityContext
        );

        converter.convert(
                jwt
        );

        org.mockito.ArgumentCaptor<AuditLoginRequest> captor =
                org.mockito.ArgumentCaptor.forClass(
                        AuditLoginRequest.class
                );

        verify(
                auditLoginService
        ).createAuditLogin(
                captor.capture()
        );

        AuditLoginRequest request =
                captor.getValue();

        assertEquals(
                USER_ID,
                request.getUserId()
        );

        assertEquals(
                "FEDERATED_OIDC",
                request.getAuthenticationMethod()
        );

        assertEquals(
                "SUCCESS",
                request.getLoginResult()
        );

        assertEquals(
                null,
                request.getFailureReason()
        );
    }

    @Test
    void shouldPersistFailureAuditWhenSessionEstablishmentIsRejected() {

        Jwt jwt =
                jwt();

        when(
                securityContextMapper.map(
                        jwt
                )
        ).thenReturn(
                securityContext
        );

        IllegalStateException rejection =
                new IllegalStateException(
                        "EFS session cannot be reactivated: "
                                + SESSION_ID
                );

        doThrow(
                rejection
        ).when(
                userSessionBindingService
        ).establishSession(
                SESSION_ID,
                USER_ID
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                converter.convert(
                                        jwt
                                )
                );

        assertSame(
                rejection,
                thrown
        );

        org.mockito.ArgumentCaptor<AuditLoginRequest> captor =
                org.mockito.ArgumentCaptor.forClass(
                        AuditLoginRequest.class
                );

        verify(
                auditLoginService
        ).createAuditLogin(
                captor.capture()
        );

        AuditLoginRequest request =
                captor.getValue();

        assertEquals(
                USER_ID,
                request.getUserId()
        );

        assertEquals(
                "FEDERATED_OIDC",
                request.getAuthenticationMethod()
        );

        assertEquals(
                "FAILURE",
                request.getLoginResult()
        );

        assertEquals(
                rejection.getMessage(),
                request.getFailureReason()
        );

        verify(
                userSessionBindingService,
                never()
        ).requireActiveSession(
                SESSION_ID,
                USER_ID
        );
    }

    @Test
    void shouldPersistFailureAuditWhenActiveSessionValidationIsRejected() {

        Jwt jwt =
                jwt();

        when(
                securityContextMapper.map(
                        jwt
                )
        ).thenReturn(
                securityContext
        );

        IllegalStateException rejection =
                new IllegalStateException(
                        "Active EFS user session is not available: "
                                + SESSION_ID
                );

        doThrow(
                rejection
        ).when(
                userSessionBindingService
        ).requireActiveSession(
                SESSION_ID,
                USER_ID
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                converter.convert(
                                        jwt
                                )
                );

        assertSame(
                rejection,
                thrown
        );

        org.mockito.ArgumentCaptor<AuditLoginRequest> captor =
                org.mockito.ArgumentCaptor.forClass(
                        AuditLoginRequest.class
                );

        verify(
                auditLoginService
        ).createAuditLogin(
                captor.capture()
        );

        AuditLoginRequest request =
                captor.getValue();

        assertEquals(
                USER_ID,
                request.getUserId()
        );

        assertEquals(
                "FEDERATED_OIDC",
                request.getAuthenticationMethod()
        );

        assertEquals(
                "FAILURE",
                request.getLoginResult()
        );

        assertEquals(
                rejection.getMessage(),
                request.getFailureReason()
        );
    }

    @Test
    void shouldNotCreateLoginAuditWhenSecurityContextCannotBeResolved() {

        Jwt jwt =
                jwt();

        IllegalStateException mappingFailure =
                new IllegalStateException(
                        "EFS identity cannot be resolved"
                );

        when(
                securityContextMapper.map(
                        jwt
                )
        ).thenThrow(
                mappingFailure
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                converter.convert(
                                        jwt
                                )
                );

        assertSame(
                mappingFailure,
                thrown
        );

        verify(
                userSessionBindingService,
                never()
        ).establishSession(
                any(),
                any()
        );

        verify(
                auditLoginService,
                never()
        ).createAuditLogin(
                any(
                        AuditLoginRequest.class
                )
        );
    }

    @Test
    void shouldPropagateAuditPersistenceFailure() {

        Jwt jwt =
                jwt();

        when(
                securityContextMapper.map(
                        jwt
                )
        ).thenReturn(
                securityContext
        );

        IllegalStateException auditFailure =
                new IllegalStateException(
                        "Audit persistence failed"
                );

        when(
                auditLoginService.createAuditLogin(
                        any(
                                AuditLoginRequest.class
                        )
                )
        ).thenThrow(
                auditFailure
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                converter.convert(
                                        jwt
                                )
                );

        assertSame(
                auditFailure,
                thrown
        );
    }

    @Test
    void shouldRejectNullJwtWithoutCreatingAudit() {

        assertThrows(
                NullPointerException.class,
                () ->
                        converter.convert(
                                null
                        )
        );

        verify(
                securityContextMapper,
                never()
        ).map(
                any()
        );

        verify(
                auditLoginService,
                never()
        ).createAuditLogin(
                any(
                        AuditLoginRequest.class
                )
        );
    }

    private Jwt jwt() {

        Instant issuedAt =
                Instant.parse(
                        "2026-09-29T16:00:00Z"
                );

        return new Jwt(
                "token-value",
                issuedAt,
                issuedAt.plusSeconds(
                        3600
                ),
                Map.of(
                        "alg",
                        "RS256"
                ),
                Map.of(
                        "sub",
                        "federated-user"
                )
        );
    }
}