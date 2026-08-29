package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.DocumentTypeRequest;
import com.efs.modules.catalog.dto.DocumentTypeResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DocumentTypeServiceIntegrationTest {

    @Autowired
    private DocumentTypeServiceInterface documentTypeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveDocumentTypeById() {

        UUID organizationId =
                createOrganization();

        DocumentTypeRequest request =
                new DocumentTypeRequest();

        request.setOrganizationId(organizationId);
        request.setDocumentTypeCode("PASSPORT");
        request.setDocumentTypeName("Passport");
        request.setDescription(
                "Passport identification document"
        );
        request.setDisplayOrder((short) 1);
        request.setStatus("ACTIVE");

        DocumentTypeResponse created =
                documentTypeService.createDocumentType(
                        request
                );

        assertNotNull(created);
        assertNotNull(created.getDocumentTypeId());

        assertEquals(
                organizationId,
                created.getOrganizationId()
        );

        assertEquals(
                "PASSPORT",
                created.getDocumentTypeCode()
        );

        assertEquals(
                "Passport",
                created.getDocumentTypeName()
        );

        assertEquals(
                "Passport identification document",
                created.getDescription()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getDisplayOrder()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        DocumentTypeResponse retrieved =
                documentTypeService.getDocumentTypeById(
                        created.getDocumentTypeId()
                );

        assertEquals(
                created.getDocumentTypeId(),
                retrieved.getDocumentTypeId()
        );
    }

    @Test
    void shouldRetrieveDocumentTypeByOrganizationAndCode() {

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

        DocumentTypeResponse result =
                documentTypeService
                        .getDocumentTypeByOrganizationAndCode(
                                organizationId,
                                "NATIONAL_ID"
                        );

        assertEquals(
                documentTypeId,
                result.getDocumentTypeId()
        );

        assertEquals(
                organizationId,
                result.getOrganizationId()
        );

        assertEquals(
                "NATIONAL_ID",
                result.getDocumentTypeCode()
        );
    }

    @Test
    void shouldAllowSameDocumentTypeCodeForDifferentOrganizations() {

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

        DocumentTypeResponse resultOne =
                documentTypeService
                        .getDocumentTypeByOrganizationAndCode(
                                organizationIdOne,
                                "PASSPORT"
                        );

        DocumentTypeResponse resultTwo =
                documentTypeService
                        .getDocumentTypeByOrganizationAndCode(
                                organizationIdTwo,
                                "PASSPORT"
                        );

        assertEquals(
                documentTypeIdOne,
                resultOne.getDocumentTypeId()
        );

        assertEquals(
                documentTypeIdTwo,
                resultTwo.getDocumentTypeId()
        );

        assertEquals(
                organizationIdOne,
                resultOne.getOrganizationId()
        );

        assertEquals(
                organizationIdTwo,
                resultTwo.getOrganizationId()
        );
    }

    @Test
    void shouldReturnDocumentTypesByOrganizationOrderedByDisplayOrder() {

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

        List<DocumentTypeResponse> results =
                documentTypeService
                        .getDocumentTypesByOrganization(
                                organizationId
                        );

        assertEquals(
                3,
                results.size()
        );

        assertEquals(
                "PASSPORT",
                results.get(0).getDocumentTypeCode()
        );

        assertEquals(
                "NATIONAL_ID",
                results.get(1).getDocumentTypeCode()
        );

        assertEquals(
                "DRIVER_LICENSE",
                results.get(2).getDocumentTypeCode()
        );
    }

    @Test
    void shouldReturnDocumentTypesByOrganizationAndStatus() {

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

        List<DocumentTypeResponse> results =
                documentTypeService
                        .getDocumentTypesByOrganizationAndStatus(
                                organizationId,
                                "ACTIVE"
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "PASSPORT",
                results.get(0).getDocumentTypeCode()
        );

        assertEquals(
                "DRIVER_LICENSE",
                results.get(1).getDocumentTypeCode()
        );
    }

    @Test
    void shouldRejectUnknownDocumentTypeId() {

        UUID unknownDocumentTypeId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        documentTypeService
                                .getDocumentTypeById(
                                        unknownDocumentTypeId
                                )
        );
    }

    @Test
    void shouldRejectUnknownOrganizationAndDocumentTypeCode() {

        UUID organizationId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        documentTypeService
                                .getDocumentTypeByOrganizationAndCode(
                                        organizationId,
                                        "UNKNOWN"
                                )
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
                "Document Type Test Organization",
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