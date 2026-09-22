package com.efs.modules.alert.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.entity.Alert;
import com.efs.modules.alert.repository.AlertRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlertScopeAuthorizationServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "91111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "92222222-2222-2222-2222-222222222222"
            );

    private static final UUID OTHER_ORGANIZATION_ID =
            UUID.fromString(
                    "93333333-3333-3333-3333-333333333333"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "94444444-4444-4444-4444-444444444444"
            );

    private static final UUID OTHER_TENANT_ID =
            UUID.fromString(
                    "95555555-5555-5555-5555-555555555555"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "96666666-6666-6666-6666-666666666666"
            );

    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private AlertRepository
            alertRepository;

    private AlertScopeAuthorizationService
            service;

    @BeforeEach
    void setUp() {

        userAccountLookupService =
                mock(
                        UserAccountLookupServiceInterface.class
                );

        alertRepository =
                mock(
                        AlertRepository.class
                );

        service =
                new AlertScopeAuthorizationService(
                        userAccountLookupService,
                        alertRepository
                );
    }

    @Test
    void shouldAuthorizeWhenPermissionAndTenantMatch() {

        UserAccountReference actor =
                actor(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                actor
        );

        UserAccountReference result =
                service.authorize(
                        context(
                                TENANT_ID,
                                Set.of(
                                        "alert.view"
                                )
                        ),
                        "alert.view"
                );

        assertSame(
                actor,
                result
        );
    }

    @Test
    void shouldRejectMissingPermission() {

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.authorize(
                                context(
                                        TENANT_ID,
                                        Set.of()
                                ),
                                "alert.view"
                        )
        );
    }

    @Test
    void shouldRejectAuthenticatedTenantMismatch() {

        UserAccountReference actor =
                actor(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                actor
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.authorize(
                                context(
                                        OTHER_TENANT_ID,
                                        Set.of(
                                                "alert.view"
                                        )
                                ),
                                "alert.view"
                        )
        );
    }

    @Test
    void shouldExposeSameTenantAlert() {

        when(
                alertRepository
                        .findByAlertId(
                                ALERT_ID
                        )
        ).thenReturn(
                Optional.of(
                        alert(
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );

        assertTrue(
                service.isAlertVisible(
                        ALERT_ID,
                        actor(
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );
    }

    @Test
    void shouldExposeOrganizationAlertToOrganizationActor() {

        when(
                alertRepository
                        .findByAlertId(
                                ALERT_ID
                        )
        ).thenReturn(
                Optional.of(
                        alert(
                                ORGANIZATION_ID,
                                OTHER_TENANT_ID
                        )
                )
        );

        assertTrue(
                service.isAlertVisible(
                        ALERT_ID,
                        actor(
                                ORGANIZATION_ID,
                                null
                        )
                )
        );
    }

    @Test
    void shouldHideAlertFromDifferentTenant() {

        when(
                alertRepository
                        .findByAlertId(
                                ALERT_ID
                        )
        ).thenReturn(
                Optional.of(
                        alert(
                                ORGANIZATION_ID,
                                OTHER_TENANT_ID
                        )
                )
        );

        assertFalse(
                service.isAlertVisible(
                        ALERT_ID,
                        actor(
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );
    }

    @Test
    void shouldHideAlertFromDifferentOrganization() {

        when(
                alertRepository
                        .findByAlertId(
                                ALERT_ID
                        )
        ).thenReturn(
                Optional.of(
                        alert(
                                OTHER_ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );

        assertFalse(
                service.isAlertVisible(
                        ALERT_ID,
                        actor(
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );
    }

    @Test
    void shouldHideMissingAlert() {

        when(
                alertRepository
                        .findByAlertId(
                                ALERT_ID
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertFalse(
                service.isAlertVisible(
                        ALERT_ID,
                        actor(
                                ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );
    }

    @Test
    void shouldReturnNotFoundForHiddenAlert() {

        when(
                alertRepository
                        .findByAlertId(
                                ALERT_ID
                        )
        ).thenReturn(
                Optional.of(
                        alert(
                                OTHER_ORGANIZATION_ID,
                                TENANT_ID
                        )
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.requireVisibleAlert(
                                ALERT_ID,
                                actor(
                                        ORGANIZATION_ID,
                                        TENANT_ID
                                ),
                                "Alert not found: "
                                        + ALERT_ID
                        )
        );
    }

    private SecurityContext context(
            UUID tenantId,
            Set<String> permissions) {

        return new SecurityContext(
                USER_ID,
                tenantId,
                null,
                Set.of(),
                permissions,
                Set.of()
        );
    }

    private UserAccountReference actor(
            UUID organizationId,
            UUID tenantId) {

        return new UserAccountReference(
                USER_ID,
                organizationId,
                tenantId,
                "alert-security@example.com"
        );
    }

    private Alert alert(
            UUID organizationId,
            UUID tenantId) {

        Alert alert =
                new Alert();

        alert.setOrganizationId(
                organizationId
        );

        alert.setTenantId(
                tenantId
        );

        return alert;
    }
}