package com.efs.modules.catalog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DocumentTypeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateDocumentType()
            throws Exception {

        UUID organizationId =
                createOrganization();

        String requestBody =
                """
                {
                  "organizationId": "%s",
                  "documentTypeCode": "PASSPORT",
                  "documentTypeName": "Passport",
                  "description": "Passport identification document",
                  "displayOrder": 1,
                  "status": "ACTIVE"
                }
                """.formatted(organizationId);

        mockMvc.perform(
                        post("/api/v1/document-types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.documentTypeId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(organizationId.toString())
                )
                .andExpect(
                        jsonPath("$.documentTypeCode")
                                .value("PASSPORT")
                )
                .andExpect(
                        jsonPath("$.documentTypeName")
                                .value("Passport")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Passport identification document")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectInvalidDocumentType()
            throws Exception {

        String requestBody =
                """
                {
                  "documentTypeCode": "",
                  "documentTypeName": "",
                  "description": "Invalid document type",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/document-types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldGetDocumentTypeById()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID documentTypeId =
                UUID.randomUUID();

        insertDocumentType(
                documentTypeId,
                organizationId,
                "NATIONAL_ID",
                "National ID",
                "National identification document",
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/{documentTypeId}",
                                documentTypeId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.documentTypeId")
                                .value(documentTypeId.toString())
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(organizationId.toString())
                )
                .andExpect(
                        jsonPath("$.documentTypeCode")
                                .value("NATIONAL_ID")
                )
                .andExpect(
                        jsonPath("$.documentTypeName")
                                .value("National ID")
                );
    }

    @Test
    void shouldGetDocumentTypeByOrganizationAndCode()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID documentTypeId =
                UUID.randomUUID();

        insertDocumentType(
                documentTypeId,
                organizationId,
                "PASSPORT",
                "Passport",
                null,
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/organization/{organizationId}/code/{documentTypeCode}",
                                organizationId,
                                "PASSPORT"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.documentTypeId")
                                .value(documentTypeId.toString())
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(organizationId.toString())
                )
                .andExpect(
                        jsonPath("$.documentTypeCode")
                                .value("PASSPORT")
                );
    }

    @Test
    void shouldKeepSameCodeIsolatedByOrganization()
            throws Exception {

        UUID organizationIdOne =
                createOrganization();

        UUID organizationIdTwo =
                createOrganization();

        UUID documentTypeIdOne =
                UUID.randomUUID();

        UUID documentTypeIdTwo =
                UUID.randomUUID();

        insertDocumentType(
                documentTypeIdOne,
                organizationIdOne,
                "PASSPORT",
                "Passport Organization One",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertDocumentType(
                documentTypeIdTwo,
                organizationIdTwo,
                "PASSPORT",
                "Passport Organization Two",
                null,
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/organization/{organizationId}/code/{documentTypeCode}",
                                organizationIdOne,
                                "PASSPORT"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.documentTypeId")
                                .value(documentTypeIdOne.toString())
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(organizationIdOne.toString())
                )
                .andExpect(
                        jsonPath("$.documentTypeName")
                                .value("Passport Organization One")
                );

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/organization/{organizationId}/code/{documentTypeCode}",
                                organizationIdTwo,
                                "PASSPORT"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.documentTypeId")
                                .value(documentTypeIdTwo.toString())
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(organizationIdTwo.toString())
                )
                .andExpect(
                        jsonPath("$.documentTypeName")
                                .value("Passport Organization Two")
                );
    }

    @Test
    void shouldGetDocumentTypesByOrganizationOrderedByDisplayOrder()
            throws Exception {

        UUID organizationId =
                createOrganization();

        insertDocumentType(
                UUID.randomUUID(),
                organizationId,
                "DRIVER_LICENSE",
                "Driver License",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertDocumentType(
                UUID.randomUUID(),
                organizationId,
                "PASSPORT",
                "Passport",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertDocumentType(
                UUID.randomUUID(),
                organizationId,
                "NATIONAL_ID",
                "National ID",
                null,
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/organization/{organizationId}",
                                organizationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$[0].documentTypeCode")
                                .value("PASSPORT")
                )
                .andExpect(
                        jsonPath("$[1].documentTypeCode")
                                .value("NATIONAL_ID")
                )
                .andExpect(
                        jsonPath("$[2].documentTypeCode")
                                .value("DRIVER_LICENSE")
                );
    }

    @Test
    void shouldGetDocumentTypesByOrganizationAndStatus()
            throws Exception {

        UUID organizationId =
                createOrganization();

        insertDocumentType(
                UUID.randomUUID(),
                organizationId,
                "PASSPORT",
                "Passport",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertDocumentType(
                UUID.randomUUID(),
                organizationId,
                "NATIONAL_ID",
                "National ID",
                null,
                (short) 2,
                "INACTIVE"
        );

        insertDocumentType(
                UUID.randomUUID(),
                organizationId,
                "DRIVER_LICENSE",
                "Driver License",
                null,
                (short) 3,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/organization/{organizationId}",
                                organizationId
                        )
                                .param("status", "ACTIVE")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].documentTypeCode")
                                .value("PASSPORT")
                )
                .andExpect(
                        jsonPath("$[1].documentTypeCode")
                                .value("DRIVER_LICENSE")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownDocumentTypeId()
            throws Exception {

        UUID documentTypeId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/{documentTypeId}",
                                documentTypeId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownOrganizationAndCode()
            throws Exception {

        UUID organizationId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/document-types/organization/{organizationId}/code/{documentTypeCode}",
                                organizationId,
                                "UNKNOWN"
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private UUID createOrganization() {

        UUID organizationId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """,
                organizationId,
                "ORG-" + organizationId,
                "Document Type Controller Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        return organizationId;
    }

    private void insertDocumentType(
            UUID documentTypeId,
            UUID organizationId,
            String documentTypeCode,
            String documentTypeName,
            String description,
            Short displayOrder,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.document_type (
                    document_type_id,
                    organization_id,
                    document_type_code,
                    document_type_name,
                    description,
                    display_order,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                documentTypeId,
                organizationId,
                documentTypeCode,
                documentTypeName,
                description,
                displayOrder,
                status
        );
    }
}