package com.efs.shared.security;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class SecurityContext {

    private final UUID userId;
    private final UUID tenantId;
    private final UUID sessionId;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Set<String> scopes;

    public SecurityContext(
            UUID userId,
            UUID tenantId,
            UUID sessionId,
            Set<String> roles,
            Set<String> permissions,
            Set<String> scopes) {

        this.userId =
                Objects.requireNonNull(
                        userId,
                        "userId is required"
                );

        this.tenantId =
                tenantId;

        this.sessionId =
                sessionId;

        this.roles =
                Set.copyOf(
                        Objects.requireNonNull(
                                roles,
                                "roles are required"
                        )
                );

        this.permissions =
                Set.copyOf(
                        Objects.requireNonNull(
                                permissions,
                                "permissions are required"
                        )
                );

        this.scopes =
                Set.copyOf(
                        Objects.requireNonNull(
                                scopes,
                                "scopes are required"
                        )
                );
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public boolean hasRole(
            String role) {

        return roles.contains(
                role
        );
    }

    public boolean hasPermission(
            String permission) {

        return permissions.contains(
                permission
        );
    }

    public boolean hasScope(
            String scope) {

        return scopes.contains(
                scope
        );
    }
}
