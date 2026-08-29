package com.efs.modules.catalog.controller;

import org.junit.jupiter.api.BeforeEach;
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
class CatalogItemControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "11111111-aaaa-aaaa-aaaa-111111111111"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "22222222-bbbb-bbbb-bbbb-222222222222"
            );

    private static final UUID CATALOG_ID =
            UUID.fromString(
                    "33333333-cccc-cccc-cccc-333333333333"
            );

    private static final UUID PARENT_ITEM_ID =
            UUID.fromString(
                    "44444444-dddd-dddd-dddd-444444444444"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();
        insertCatalog();
    }

    @Test
    void shouldCreateCatalogItem()
            throws Exception {

        String requestBody =
                """
                {
                  "catalogId": "%s",
                  "itemCode": "OPEN",
                  "itemName": "Open",
                  "displayOrder": 1,
                  "isDefault": true,
                  "status": "ACTIVE"
                }
                """.formatted(
                        CATALOG_ID
                );

        mockMvc.perform(
                        post("/api/v1/catalog-items")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.catalogItemId").exists()
                )
                .andExpect(
                        jsonPath("$.catalogId")
                                .value(
                                        CATALOG_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.itemCode")
                                .value("OPEN")
                )
                .andExpect(
                        jsonPath("$.itemName")
                                .value("Open")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.isDefault")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        String requestBody =
                """
                {
                  "itemCode": "",
                  "itemName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/catalog-items")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldGetCatalogItemById()
            throws Exception {

        UUID catalogItemId =
                UUID.randomUUID();

        insertCatalogItem(
                catalogItemId,
                "REVIEW",
                "Review",
                (short) 1,
                null,
                false,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog-items/{catalogItemId}",
                                catalogItemId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.catalogItemId")
                                .value(
                                        catalogItemId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.catalogId")
                                .value(
                                        CATALOG_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.itemCode")
                                .value("REVIEW")
                );
    }

    @Test
    void shouldGetCatalogItemByCatalogAndCode()
            throws Exception {

        UUID catalogItemId =
                UUID.randomUUID();

        insertCatalogItem(
                catalogItemId,
                "CLOSED",
                "Closed",
                (short) 1,
                null,
                false,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog-items/catalog/{catalogId}/code/{itemCode}",
                                CATALOG_ID,
                                "CLOSED"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.catalogItemId")
                                .value(
                                        catalogItemId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.itemCode")
                                .value("CLOSED")
                );
    }

    @Test
    void shouldGetCatalogItemsByCatalogId()
            throws Exception {

        insertCatalogItem(
                UUID.randomUUID(),
                "FIRST",
                "First",
                (short) 1,
                null,
                false,
                "ACTIVE"
        );

        insertCatalogItem(
                UUID.randomUUID(),
                "SECOND",
                "Second",
                (short) 2,
                null,
                false,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog-items/catalog/{catalogId}",
                                CATALOG_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].itemCode")
                                .value("FIRST")
                )
                .andExpect(
                        jsonPath("$[1].itemCode")
                                .value("SECOND")
                );
    }

    @Test
    void shouldGetCatalogItemsByParentItemId()
            throws Exception {

        insertCatalogItem(
                PARENT_ITEM_ID,
                "PARENT",
                "Parent",
                (short) 1,
                null,
                false,
                "ACTIVE"
        );

        insertCatalogItem(
                UUID.randomUUID(),
                "CHILD_A",
                "Child A",
                (short) 1,
                PARENT_ITEM_ID,
                false,
                "ACTIVE"
        );

        insertCatalogItem(
                UUID.randomUUID(),
                "CHILD_B",
                "Child B",
                (short) 2,
                PARENT_ITEM_ID,
                false,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog-items/parent/{parentItemId}",
                                PARENT_ITEM_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].parentItemId")
                                .value(
                                        PARENT_ITEM_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].parentItemId")
                                .value(
                                        PARENT_ITEM_ID.toString()
                                )
                );
    }

    @Test
    void shouldGetCatalogItemsByCatalogIdAndStatus()
            throws Exception {

        insertCatalogItem(
                UUID.randomUUID(),
                "ACTIVE_A",
                "Active A",
                (short) 1,
                null,
                false,
                "ACTIVE"
        );

        insertCatalogItem(
                UUID.randomUUID(),
                "ACTIVE_B",
                "Active B",
                (short) 2,
                null,
                false,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog-items/catalog/{catalogId}/status",
                                CATALOG_ID
                        )
                                .param(
                                        "status",
                                        "ACTIVE"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("ACTIVE")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCatalogItemId()
            throws Exception {

        UUID unknownCatalogItemId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/catalog-items/{catalogItemId}",
                                unknownCatalogItemId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertCatalogItem(
            UUID catalogItemId,
            String itemCode,
            String itemName,
            Short displayOrder,
            UUID parentItemId,
            boolean isDefault,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.catalog_item (
                    catalog_item_id,
                    catalog_id,
                    item_code,
                    item_name,
                    display_order,
                    parent_item_id,
                    is_default,
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
                    ?,
                    clock_timestamp()
                )
                """,
                catalogItemId,
                CATALOG_ID,
                itemCode,
                itemName,
                displayOrder,
                parentItemId,
                isDefault,
                status
        );
    }

    private void insertOrganization() {

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
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-CATALOG-ITEM-CTRL-ORG",
                "EFS Catalog Item Controller Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertTenant() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                TENANT_ID,
                ORGANIZATION_ID,
                "EFS-CATALOG-ITEM-CTRL-TENANT",
                "EFS Catalog Item Controller Tenant",
                "ACTIVE",
                "TEST"
        );
    }

    private void insertCatalog() {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.catalog (
                    catalog_id,
                    catalog_code,
                    catalog_name,
                    description,
                    organization_id,
                    tenant_id,
                    status,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, clock_timestamp())
                """,
                CATALOG_ID,
                "CASE_STATUS_CTRL",
                "Case Status Controller",
                "Catalog Item controller integration test catalog",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );
    }
}