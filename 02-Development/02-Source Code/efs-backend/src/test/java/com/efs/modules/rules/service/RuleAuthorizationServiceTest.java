package com.efs.modules.rules.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleAuthorizationServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID OTHER_USER_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    private static final UUID OTHER_TENANT_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private RuleAuthorizationService
            service;

    @BeforeEach
    void setUp() {

        service =
                new RuleAuthorizationService(
                        userAccountLookupService
                );
    }

    @Test
    void shouldAuthorizeTenantActorWithRequiredPermission() {

        SecurityContext context =
                context(
                        Set.of("rule.view"),
                        TENANT_ID
                );

        UserAccountReference actor =
                actor(
                        TENANT_ID
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(USER_ID)
        ).thenReturn(
                actor
        );

        assertEquals(
                actor,
                service.authorize(
                        context,
                        "rule.view"
                )
        );
    }

    @Test
    void shouldAuthorizeOrganizationActorWithNullTenant() {

        SecurityContext context =
                context(
                        Set.of("rule.create"),
                        null
                );

        UserAccountReference actor =
                actor(
                        null
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(USER_ID)
        ).thenReturn(
                actor
        );

        assertEquals(
                actor,
                service.authorize(
                        context,
                        "rule.create"
                )
        );
    }

    @Test
    void shouldRejectMissingPermissionBeforeUserLookup() {

        SecurityContext context =
                context(
                        Set.of(),
                        TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.authorize(
                                context,
                                "rule.update"
                        )
        );

        verify(
                userAccountLookupService,
                never()
        ).getAuthorizedUser(USER_ID);
    }

    @Test
    void shouldRejectAuthenticatedTenantMismatch() {

        SecurityContext context =
                context(
                        Set.of("rule.activate"),
                        OTHER_TENANT_ID
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(USER_ID)
        ).thenReturn(
                actor(
                        TENANT_ID
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.authorize(
                                context,
                                "rule.activate"
                        )
        );
    }

    @Test
    void shouldRejectNullSecurityContext() {

        assertThrows(
                NullPointerException.class,
                () ->
                        service.authorize(
                                null,
                                "rule.view"
                        )
        );
    }

    @Test
    void shouldAcceptMatchingAuthenticatedActor() {

        UserAccountReference actor =
                actor(
                        TENANT_ID
                );

        assertDoesNotThrow(
                () ->
                        service.requireActor(
                                actor,
                                USER_ID,
                                "changedBy"
                        )
        );
    }

    @Test
    void shouldRejectDifferentRequestedActor() {

        UserAccountReference actor =
                actor(
                        TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.requireActor(
                                actor,
                                OTHER_USER_ID,
                                "changedBy"
                        )
        );
    }

    @Test
    void shouldRejectNullRequestedActor() {

        UserAccountReference actor =
                actor(
                        TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.requireActor(
                                actor,
                                null,
                                "executedBy"
                        )
        );
    }

    private UserAccountReference actor(
            UUID tenantId) {

        return new UserAccountReference(
                USER_ID,
                ORGANIZATION_ID,
                tenantId,
                "rule-security@example.com"
        );
    }

    private SecurityContext context(
            Set<String> permissions,
            UUID tenantId) {

        return new SecurityContext(
                USER_ID,
                tenantId,
                null,
                Set.of(),
                permissions,
                Set.of()
        );
    }
}