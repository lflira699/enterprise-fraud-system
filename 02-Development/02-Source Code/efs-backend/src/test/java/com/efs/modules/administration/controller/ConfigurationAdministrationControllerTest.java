package com.efs.modules.administration.controller;

import com.efs.modules.administration.dto.ConfigurationChangeRequestCreateRequest;
import com.efs.modules.administration.dto.ConfigurationChangeRequestResponse;
import com.efs.modules.administration.dto.ConfigurationEffectiveResponse;
import com.efs.modules.administration.dto.ConfigurationRejectRequest;
import com.efs.modules.administration.dto.ConfigurationVersionResponse;
import com.efs.modules.administration.service.ConfigurationGovernanceServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationAdministrationControllerTest {

    private static final UUID CHANGE_REQUEST_ID =
            UUID.fromString(
                    "44000000-0000-0000-0000-000000000001"
            );

    @Mock
    private ConfigurationGovernanceServiceInterface
            configurationGovernanceService;

    @Mock
    private SecurityContextProvider
            securityContextProvider;

    @Mock
    private SecurityContext
            securityContext;

    private ConfigurationAdministrationController
            controller;

    @BeforeEach
    void setUp() {

        controller =
                new ConfigurationAdministrationController(
                        configurationGovernanceService,
                        securityContextProvider
                );
    }

    @Test
    void shouldExposeOnlyApprovedConfigurationRoutes()
            throws Exception {

        RequestMapping baseMapping =
                ConfigurationAdministrationController.class
                        .getAnnotation(
                                RequestMapping.class
                        );

        assertNotNull(
                baseMapping
        );

        assertEquals(
                "/api/v1/admin/configuration",
                baseMapping.value()[0]
        );

        assertEquals(
                "/effective",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "getEffectiveConfigurations",
                                UUID.class
                        )
                        .getAnnotation(
                                GetMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/effective/{configurationKey}",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "getEffectiveConfiguration",
                                String.class,
                                UUID.class
                        )
                        .getAnnotation(
                                GetMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/change-requests",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "createChangeRequest",
                                ConfigurationChangeRequestCreateRequest.class
                        )
                        .getAnnotation(
                                PostMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/change-requests/{changeRequestId}",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "getChangeRequest",
                                UUID.class
                        )
                        .getAnnotation(
                                GetMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/change-requests/{changeRequestId}/approve",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "approveChangeRequest",
                                UUID.class
                        )
                        .getAnnotation(
                                PostMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/change-requests/{changeRequestId}/reject",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "rejectChangeRequest",
                                UUID.class,
                                ConfigurationRejectRequest.class
                        )
                        .getAnnotation(
                                PostMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/change-requests/{changeRequestId}/publish",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "publishChangeRequest",
                                UUID.class
                        )
                        .getAnnotation(
                                PostMapping.class
                        )
                        .value()[0]
        );

        assertEquals(
                "/{configurationKey}/versions",
                ConfigurationAdministrationController.class
                        .getDeclaredMethod(
                                "getAppliedVersions",
                                String.class,
                                UUID.class
                        )
                        .getAnnotation(
                                GetMapping.class
                        )
                        .value()[0]
        );

        for (
                Method method
                : ConfigurationAdministrationController.class
                        .getDeclaredMethods()
        ) {

            assertNull(
                    method.getAnnotation(
                            PatchMapping.class
                    )
            );

            assertNull(
                    method.getAnnotation(
                            PutMapping.class
                    )
            );

            assertNull(
                    method.getAnnotation(
                            DeleteMapping.class
                    )
            );
        }
    }

    @Test
    void shouldDelegateApprovedReadOperationsUsingCurrentSecurityContext() {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );

        ConfigurationEffectiveResponse effective =
                new ConfigurationEffectiveResponse();

        effective.setConfigurationKey(
                "EFS.REPORT.MAX_RECORDS"
        );

        ConfigurationVersionResponse version =
                new ConfigurationVersionResponse();

        version.setConfigurationKey(
                "EFS.REPORT.MAX_RECORDS"
        );

        when(
                configurationGovernanceService
                        .getEffectiveConfigurations(
                                null,
                                securityContext
                        )
        ).thenReturn(
                List.of(
                        effective
                )
        );

        when(
                configurationGovernanceService
                        .getEffectiveConfiguration(
                                "EFS.REPORT.MAX_RECORDS",
                                null,
                                securityContext
                        )
        ).thenReturn(
                effective
        );

        when(
                configurationGovernanceService
                        .getAppliedVersions(
                                "EFS.REPORT.MAX_RECORDS",
                                null,
                                securityContext
                        )
        ).thenReturn(
                List.of(
                        version
                )
        );

        assertEquals(
                1,
                controller.getEffectiveConfigurations(
                                null
                        )
                        .getBody()
                        .size()
        );

        assertEquals(
                effective,
                controller.getEffectiveConfiguration(
                                "EFS.REPORT.MAX_RECORDS",
                                null
                        )
                        .getBody()
        );

        assertEquals(
                1,
                controller.getAppliedVersions(
                                "EFS.REPORT.MAX_RECORDS",
                                null
                        )
                        .getBody()
                        .size()
        );

        verify(
                configurationGovernanceService
        ).getEffectiveConfigurations(
                null,
                securityContext
        );

        verify(
                configurationGovernanceService
        ).getEffectiveConfiguration(
                "EFS.REPORT.MAX_RECORDS",
                null,
                securityContext
        );

        verify(
                configurationGovernanceService
        ).getAppliedVersions(
                "EFS.REPORT.MAX_RECORDS",
                null,
                securityContext
        );
    }

    @Test
    void shouldDelegateApprovedLifecycleOperationsUsingCurrentSecurityContext() {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );

        ConfigurationChangeRequestResponse response =
                new ConfigurationChangeRequestResponse();

        response.setChangeRequestId(
                CHANGE_REQUEST_ID
        );

        ConfigurationChangeRequestCreateRequest createRequest =
                new ConfigurationChangeRequestCreateRequest();

        ConfigurationRejectRequest rejectRequest =
                new ConfigurationRejectRequest();

        rejectRequest.setRejectionReason(
                "Controlled rejection"
        );

        when(
                configurationGovernanceService
                        .createChangeRequest(
                                createRequest,
                                securityContext
                        )
        ).thenReturn(
                response
        );

        when(
                configurationGovernanceService
                        .getChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        ).thenReturn(
                response
        );

        when(
                configurationGovernanceService
                        .approveChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        ).thenReturn(
                response
        );

        when(
                configurationGovernanceService
                        .rejectChangeRequest(
                                CHANGE_REQUEST_ID,
                                "Controlled rejection",
                                securityContext
                        )
        ).thenReturn(
                response
        );

        when(
                configurationGovernanceService
                        .publishChangeRequest(
                                CHANGE_REQUEST_ID,
                                securityContext
                        )
        ).thenReturn(
                response
        );

        assertEquals(
                201,
                controller.createChangeRequest(
                                createRequest
                        )
                        .getStatusCode()
                        .value()
        );

        assertEquals(
                response,
                controller.getChangeRequest(
                                CHANGE_REQUEST_ID
                        )
                        .getBody()
        );

        assertEquals(
                response,
                controller.approveChangeRequest(
                                CHANGE_REQUEST_ID
                        )
                        .getBody()
        );

        assertEquals(
                response,
                controller.rejectChangeRequest(
                                CHANGE_REQUEST_ID,
                                rejectRequest
                        )
                        .getBody()
        );

        assertEquals(
                response,
                controller.publishChangeRequest(
                                CHANGE_REQUEST_ID
                        )
                        .getBody()
        );

        verify(
                configurationGovernanceService
        ).createChangeRequest(
                createRequest,
                securityContext
        );

        verify(
                configurationGovernanceService
        ).getChangeRequest(
                CHANGE_REQUEST_ID,
                securityContext
        );

        verify(
                configurationGovernanceService
        ).approveChangeRequest(
                CHANGE_REQUEST_ID,
                securityContext
        );

        verify(
                configurationGovernanceService
        ).rejectChangeRequest(
                CHANGE_REQUEST_ID,
                "Controlled rejection",
                securityContext
        );

        verify(
                configurationGovernanceService
        ).publishChangeRequest(
                CHANGE_REQUEST_ID,
                securityContext
        );
    }
}