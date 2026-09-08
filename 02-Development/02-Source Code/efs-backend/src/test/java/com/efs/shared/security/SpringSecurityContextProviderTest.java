package com.efs.shared.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpringSecurityContextProviderTest {

    private final SpringSecurityContextProvider provider =
            new SpringSecurityContextProvider();

    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticatedEfsSecurityContext() {

        SecurityContext context =
                buildContext();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        context,
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication
                );

        SecurityContext current =
                provider.getCurrentContext();

        assertSame(
                context,
                current
        );
    }

    @Test
    void shouldRejectMissingAuthentication() {

        assertThrows(
                IllegalStateException.class,
                provider::getCurrentContext
        );
    }

    @Test
    void shouldRejectUnauthenticatedAuthentication() {

        SecurityContext context =
                buildContext();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        context,
                        null
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication
                );

        assertThrows(
                IllegalStateException.class,
                provider::getCurrentContext
        );
    }

    @Test
    void shouldRejectPrincipalWithoutEfsSecurityContext() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "external-principal",
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication
                );

        assertThrows(
                IllegalStateException.class,
                provider::getCurrentContext
        );
    }

    private SecurityContext buildContext() {

        return new SecurityContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(
                        "RULE_ADMINISTRATOR"
                ),
                Set.of(
                        "rule.view"
                ),
                Set.of(
                        "rules.read"
                )
        );
    }
}