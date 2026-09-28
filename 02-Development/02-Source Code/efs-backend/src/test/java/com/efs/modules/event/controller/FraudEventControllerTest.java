package com.efs.modules.event.controller;

import com.efs.modules.event.dto.FraudEventRequest;
import com.efs.modules.event.dto.FraudEventResponse;
import com.efs.modules.event.service.FraudEventServiceInterface;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FraudEventControllerTest {

    private static final UUID FRAUD_EVENT_ID =
            UUID.fromString(
                    "51000000-0000-0000-0000-000000000001"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "51000000-0000-0000-0000-000000000002"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "51000000-0000-0000-0000-000000000003"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "51000000-0000-0000-0000-000000000004"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "51000000-0000-0000-0000-000000000005"
            );

    @Mock
    private FraudEventServiceInterface fraudEventService;

    @Mock
    private SecurityContextProvider securityContextProvider;

    @Mock
    private SecurityContext securityContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        FraudEventController controller =
                new FraudEventController(
                        fraudEventService,
                        securityContextProvider
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                controller
                        )
                        .build();

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );
    }

    @Test
    void shouldRegisterFraudEventUsingCurrentSecurityContext()
            throws Exception {

        FraudEventResponse response =
                createResponse();

        when(
                fraudEventService.registerEvent(
                        any(
                                FraudEventRequest.class
                        ),
                        eq(
                                securityContext
                        )
                )
        ).thenReturn(
                response
        );

        String request =
                """
                {
                    "transactionId": "%s",
                    "eventType": "ACCOUNT_LOGIN",
                    "sourceType": "DIGITAL_CHANNEL",
                    "sourceReference": "SRC-001",
                    "idempotencyKey": "IDEMP-001",
                    "correlationId": "%s",
                    "payload": {
                        "channel": "WEB"
                    },
                    "occurredAt": "2026-09-27T14:30:00"
                }
                """.formatted(
                        TRANSACTION_ID,
                        CORRELATION_ID
                );

        mockMvc.perform(
                        post(
                                "/api/v1/events"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(
                                        request
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.fraudEventId"
                        ).value(
                                FRAUD_EVENT_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.eventType"
                        ).value(
                                "ACCOUNT_LOGIN"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.sourceType"
                        ).value(
                                "DIGITAL_CHANNEL"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.correlationId"
                        ).value(
                                CORRELATION_ID.toString()
                        )
                );

        ArgumentCaptor<FraudEventRequest> requestCaptor =
                ArgumentCaptor.forClass(
                        FraudEventRequest.class
                );

        verify(
                fraudEventService
        ).registerEvent(
                requestCaptor.capture(),
                eq(
                        securityContext
                )
        );

        FraudEventRequest captured =
                requestCaptor.getValue();

        assertEquals(
                TRANSACTION_ID,
                captured.getTransactionId()
        );

        assertEquals(
                "ACCOUNT_LOGIN",
                captured.getEventType()
        );

        assertEquals(
                "DIGITAL_CHANNEL",
                captured.getSourceType()
        );

        assertEquals(
                "SRC-001",
                captured.getSourceReference()
        );

        assertEquals(
                "IDEMP-001",
                captured.getIdempotencyKey()
        );

        assertEquals(
                CORRELATION_ID,
                captured.getCorrelationId()
        );

        assertEquals(
                LocalDateTime.of(
                        2026,
                        9,
                        27,
                        14,
                        30
                ),
                captured.getOccurredAt()
        );

        verify(
                securityContextProvider
        ).getCurrentContext();
    }

    @Test
    void shouldGetFraudEventByIdUsingCurrentSecurityContext()
            throws Exception {

        FraudEventResponse response =
                createResponse();

        when(
                fraudEventService.getEvent(
                        FRAUD_EVENT_ID,
                        securityContext
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/v1/events/{eventId}",
                                FRAUD_EVENT_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.fraudEventId"
                        ).value(
                                FRAUD_EVENT_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.organizationId"
                        ).value(
                                ORGANIZATION_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.tenantId"
                        ).value(
                                TENANT_ID.toString()
                        )
                );

        verify(
                fraudEventService
        ).getEvent(
                FRAUD_EVENT_ID,
                securityContext
        );

        verify(
                securityContextProvider
        ).getCurrentContext();
    }

    @Test
    void shouldSearchFraudEventsUsingApprovedDefaults()
            throws Exception {

        PageResponse<FraudEventResponse> pageResponse =
                new PageResponse<>(
                        List.of(
                                createResponse()
                        ),
                        0,
                        25,
                        1L,
                        1,
                        false,
                        false
                );

        when(
                fraudEventService.searchEvents(
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        eq(
                                0
                        ),
                        eq(
                                25
                        ),
                        eq(
                                "occurredAt"
                        ),
                        eq(
                                "DESC"
                        ),
                        eq(
                                securityContext
                        )
                )
        ).thenReturn(
                pageResponse
        );

        mockMvc.perform(
                        get(
                                "/api/v1/events"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].fraudEventId"
                        ).value(
                                FRAUD_EVENT_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.page"
                        ).value(
                                0
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.size"
                        ).value(
                                25
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.totalElements"
                        ).value(
                                1
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.totalPages"
                        ).value(
                                1
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.hasNext"
                        ).value(
                                false
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.hasPrevious"
                        ).value(
                                false
                        )
                );

        verify(
                fraudEventService
        ).searchEvents(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(
                        0
                ),
                eq(
                        25
                ),
                eq(
                        "occurredAt"
                ),
                eq(
                        "DESC"
                ),
                eq(
                        securityContext
                )
        );

        verify(
                securityContextProvider
        ).getCurrentContext();
    }

    @Test
    void shouldBindApprovedSearchParameters()
            throws Exception {

        LocalDateTime occurredFrom =
                LocalDateTime.of(
                        2026,
                        9,
                        20,
                        10,
                        0
                );

        LocalDateTime occurredTo =
                LocalDateTime.of(
                        2026,
                        9,
                        21,
                        10,
                        0
                );

        LocalDateTime receivedFrom =
                LocalDateTime.of(
                        2026,
                        9,
                        22,
                        11,
                        0
                );

        LocalDateTime receivedTo =
                LocalDateTime.of(
                        2026,
                        9,
                        23,
                        11,
                        0
                );

        PageResponse<FraudEventResponse> pageResponse =
                new PageResponse<>(
                        List.of(),
                        2,
                        50,
                        0L,
                        0,
                        false,
                        true
                );

        when(
                fraudEventService.searchEvents(
                        TRANSACTION_ID,
                        "ACCOUNT_LOGIN",
                        "DIGITAL_CHANNEL",
                        "SRC-001",
                        CORRELATION_ID,
                        occurredFrom,
                        occurredTo,
                        receivedFrom,
                        receivedTo,
                        2,
                        50,
                        "receivedAt",
                        "ASC",
                        securityContext
                )
        ).thenReturn(
                pageResponse
        );

        mockMvc.perform(
                        get(
                                "/api/v1/events"
                        )
                                .param(
                                        "transactionId",
                                        TRANSACTION_ID.toString()
                                )
                                .param(
                                        "eventType",
                                        "ACCOUNT_LOGIN"
                                )
                                .param(
                                        "sourceType",
                                        "DIGITAL_CHANNEL"
                                )
                                .param(
                                        "sourceReference",
                                        "SRC-001"
                                )
                                .param(
                                        "correlationId",
                                        CORRELATION_ID.toString()
                                )
                                .param(
                                        "occurredFrom",
                                        "2026-09-20T10:00:00"
                                )
                                .param(
                                        "occurredTo",
                                        "2026-09-21T10:00:00"
                                )
                                .param(
                                        "receivedFrom",
                                        "2026-09-22T11:00:00"
                                )
                                .param(
                                        "receivedTo",
                                        "2026-09-23T11:00:00"
                                )
                                .param(
                                        "page",
                                        "2"
                                )
                                .param(
                                        "size",
                                        "50"
                                )
                                .param(
                                        "sort",
                                        "receivedAt"
                                )
                                .param(
                                        "direction",
                                        "ASC"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content"
                        ).isEmpty()
                )
                .andExpect(
                        jsonPath(
                                "$.page"
                        ).value(
                                2
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.size"
                        ).value(
                                50
                        )
                );

        verify(
                fraudEventService
        ).searchEvents(
                TRANSACTION_ID,
                "ACCOUNT_LOGIN",
                "DIGITAL_CHANNEL",
                "SRC-001",
                CORRELATION_ID,
                occurredFrom,
                occurredTo,
                receivedFrom,
                receivedTo,
                2,
                50,
                "receivedAt",
                "ASC",
                securityContext
        );

        verify(
                securityContextProvider
        ).getCurrentContext();
    }

    private FraudEventResponse createResponse() {

        FraudEventResponse response =
                new FraudEventResponse();

        response.setFraudEventId(
                FRAUD_EVENT_ID
        );

        response.setOrganizationId(
                ORGANIZATION_ID
        );

        response.setTenantId(
                TENANT_ID
        );

        response.setTransactionId(
                TRANSACTION_ID
        );

        response.setEventType(
                "ACCOUNT_LOGIN"
        );

        response.setSourceType(
                "DIGITAL_CHANNEL"
        );

        response.setSourceReference(
                "SRC-001"
        );

        response.setIdempotencyKey(
                "IDEMP-001"
        );

        response.setCorrelationId(
                CORRELATION_ID
        );

        return response;
    }
}