package com.efs.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityContextProvider
        implements SecurityContextProvider {

    @Override
    public SecurityContext getCurrentContext() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated security context is not available"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof SecurityContext context)) {

            throw new IllegalStateException(
                    "Authenticated principal does not contain EFS security context"
            );
        }

        return context;
    }
}