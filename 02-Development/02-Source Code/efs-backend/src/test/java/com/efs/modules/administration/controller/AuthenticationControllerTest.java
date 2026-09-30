package com.efs.modules.administration.controller;

import com.efs.modules.administration.service.UserSessionBindingService;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserSessionBindingService userSessionBindingService;

    @Mock
    private SecurityContextProvider securityContextProvider;

    private MockMvc mockMvc;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {

        AuthenticationController controller =
                new AuthenticationController(
                        userSessionBindingService,
                        securityContextProvider
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .build();

        securityContext =
                new SecurityContext(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Set.of("USER"),
                        Set.of(),
                        Set.of()
                );
    }

    @Test
    void logoutShouldDelegateCurrentSecurityContextAndReturnNoContent()
            throws Exception {

        when(securityContextProvider.getCurrentContext())
                .thenReturn(securityContext);

        mockMvc.perform(
                        post(
                                "/api/v1/authentication/logout"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(securityContextProvider)
                .getCurrentContext();

        verify(userSessionBindingService)
                .logout(
                        securityContext
                );
    }
}