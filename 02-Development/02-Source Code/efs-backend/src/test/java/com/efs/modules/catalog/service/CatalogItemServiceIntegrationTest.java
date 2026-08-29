package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CatalogItemRequest;
import com.efs.modules.catalog.dto.CatalogItemResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CatalogItemServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "f2f2f2f2-f2f2-f2f2-f2f2-f2f2f2f2f2f2"
            );

    private static final UUID CATALOG_ID =
            UUID.fromString(
                    "f3f3f3f3-f3f3-f3f3-f3f3-f3f3f3f3f3f3"
            );

    private static final UUID PARENT_ITEM_ID =
            UUID.fromString(
                    "f4f4f4f4-f4f4-f4f4-f4f4-f4f4f4f4f4f4"
            );

    @Autowired
    private CatalogItemServiceInterface catalogItemService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();
        insertCatalog();
    }

    @Test
    void shouldCreateAndRetrieveCatalogItemById() {

        CatalogItemRequest request =
                new CatalogItemRequest();

        request.setCatalogId(
                CATALOG_ID
        );

        request.setItemCode(
                "OPEN"
        );

        request.setItemName(
                "Open"
        );

        request.setDisplayOrder(
                (short) 1
        );

        request.setIsDefault(
                true
        );

        request.setStatus(
                "ACTIVE"
        );

        CatalogItemResponse created =
                catalogItemService.createCatalogItem(
                        request
                );

        assertNotNull(
                created
        );

        assertNotNull(
                created.getCatalogItemId()
        );

        assertEquals(
                CATALOG_ID,
                created.getCatalogId()
        );

        assertEquals(
                "OPEN",
                created.getItemCode()
        );

        assertEquals(
                "Open",
                created.getItemName()
        );

        assertEquals(
                (short) 1,
                created.getDisplayOrder()
        );

        assertNull(
                created.getParentItemId()
        );

        assertEquals(
                true,
                created.getIsDefault()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        CatalogItemResponse retrieved =
                catalogItemService.getCatalogItemById(
                        created.getCatalogItemId()
                );

        assertEquals(
                created.getCatalogItemId(),
                retrieved.getCatalogItemId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        CatalogItemRequest request =
                new CatalogItemRequest();

        request.setCatalogId(
                CATALOG_ID
        );

        request.setItemCode(
                "PENDING"
        );

        request.setItemName(
                "Pending"
        );

        request.setIsDefault(
                false
        );

        request.setStatus(
                "ACTIVE"
        );

        CatalogItemResponse created =
                catalogItemService.createCatalogItem(
                        request
                );

        assertNotNull(
                created.getCatalogItemId()
        );

        assertNull(
                created.getDisplayOrder()
        );

        assertNull(
                created.getParentItemId()
        );

        assertEquals(
                false,
                created.getIsDefault()
        );

        assertNotNull(
                created.getCreatedAt()
        );
    }

    @Test
    void shouldRetrieveCatalogItemByCatalogAndCode() {

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

        CatalogItemResponse result =
                catalogItemService
                        .getCatalogItemByCatalogAndCode(
                                CATALOG_ID,
                                "REVIEW"
                        );

        assertEquals(
                catalogItemId,
                result.getCatalogItemId()
        );

        assertEquals(
                CATALOG_ID,
                result.getCatalogId()
        );

        assertEquals(
                "REVIEW",
                result.getItemCode()
        );
    }

    @Test
    void shouldReturnCatalogItemsByCatalogId() {

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

        List<CatalogItemResponse> results =
                catalogItemService
                        .getCatalogItemsByCatalogId(
                                CATALOG_ID
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "FIRST",
                results.get(0).getItemCode()
        );

        assertEquals(
                "SECOND",
                results.get(1).getItemCode()
        );
    }

    @Test
    void shouldReturnCatalogItemsByParentItemId() {

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

        List<CatalogItemResponse> results =
                catalogItemService
                        .getCatalogItemsByParentItemId(
                                PARENT_ITEM_ID
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        PARENT_ITEM_ID.equals(
                                                result.getParentItemId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnCatalogItemsByCatalogIdAndStatus() {

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

        List<CatalogItemResponse> results =
                catalogItemService
                        .getCatalogItemsByCatalogIdAndStatus(
                                CATALOG_ID,
                                "ACTIVE"
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "ACTIVE".equals(
                                                result.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldRejectUnknownCatalogItemId() {

        UUID unknownCatalogItemId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        catalogItemService.getCatalogItemById(
                                unknownCatalogItemId
                        )
        );
    }

    @Test
    void shouldRejectUnknownCatalogItemCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        catalogItemService
                                .getCatalogItemByCatalogAndCode(
                                        CATALOG_ID,
                                        "UNKNOWN_ITEM"
                                )
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
                "EFS-CATALOG-ITEM-ORG",
                "EFS Catalog Item Organization",
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
                "EFS-CATALOG-ITEM-TENANT",
                "EFS Catalog Item Tenant",
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
                "CASE_STATUS",
                "Case Status",
                "Catalog Item integration test catalog",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );
    }
}