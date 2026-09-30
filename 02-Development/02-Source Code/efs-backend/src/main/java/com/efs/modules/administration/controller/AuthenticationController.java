package com.efs.modules.administration.controller;

import com.efs.modules.administration.service.UserSessionBindingService;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final UserSessionBindingService userSessionBindingService;
    private final SecurityContextProvider securityContextProvider;

    public AuthenticationController(
            UserSessionBindingService userSessionBindingService,
            SecurityContextProvider securityContextProvider) {

        this.userSessionBindingService =
                userSessionBindingService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        userSessionBindingService.logout(
                securityContext
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}