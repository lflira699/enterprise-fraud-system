package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.TransactionTypeRequest;
import com.efs.modules.catalog.dto.TransactionTypeResponse;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionTypeServiceIntegrationTest {

    @Autowired
    private TransactionTypeServiceInterface transactionTypeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveTransactionTypeById() {

        TransactionTypeRequest request =
                new TransactionTypeRequest();

        request.setTransactionTypeCode("TRANSFER");
        request.setTransactionTypeName("Transfer");
        request.setDescription("Transfer transaction type");
        request.setDisplayOrder((short) 2);
        request.setStatus("ACTIVE");

        TransactionTypeResponse created =
                transactionTypeService.createTransactionType(request);

        assertNotNull(created);
        assertNotNull(created.getTransactionTypeId());

        assertEquals(
                "TRANSFER",
                created.getTransactionTypeCode()
        );

        assertEquals(
                "Transfer",
                created.getTransactionTypeName()
        );

        assertEquals(
                "Transfer transaction type",
                created.getDescription()
        );

        assertEquals(
                Short.valueOf((short) 2),
                created.getDisplayOrder()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        TransactionTypeResponse retrieved =
                transactionTypeService.getTransactionTypeById(
                        created.getTransactionTypeId()
                );

        assertEquals(
                created.getTransactionTypeId(),
                retrieved.getTransactionTypeId()
        );
    }

    @Test
    void shouldRetrieveTransactionTypeByCode() {

        UUID transactionTypeId =
                UUID.randomUUID();

        insertTransactionType(
                transactionTypeId,
                "PAYMENT",
                "Payment",
                "Payment transaction type",
                (short) 1,
                "ACTIVE"
        );

        TransactionTypeResponse result =
                transactionTypeService.getTransactionTypeByCode(
                        "PAYMENT"
                );

        assertEquals(
                transactionTypeId,
                result.getTransactionTypeId()
        );

        assertEquals(
                "PAYMENT",
                result.getTransactionTypeCode()
        );

        assertEquals(
                "Payment",
                result.getTransactionTypeName()
        );
    }

    @Test
    void shouldReturnTransactionTypesByStatusOrderedByDisplayOrder() {

        insertTransactionType(
                UUID.randomUUID(),
                "WITHDRAWAL",
                "Withdrawal",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "PAYMENT",
                "Payment",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "TRANSFER",
                "Transfer",
                null,
                (short) 2,
                "ACTIVE"
        );

        List<TransactionTypeResponse> results =
                transactionTypeService.getTransactionTypesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                3,
                results.size()
        );

        assertEquals(
                "PAYMENT",
                results.get(0).getTransactionTypeCode()
        );

        assertEquals(
                "TRANSFER",
                results.get(1).getTransactionTypeCode()
        );

        assertEquals(
                "WITHDRAWAL",
                results.get(2).getTransactionTypeCode()
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
    void shouldReturnAllTransactionTypesOrderedByDisplayOrder() {

        insertTransactionType(
                UUID.randomUUID(),
                "WITHDRAWAL",
                "Withdrawal",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "PAYMENT",
                "Payment",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "TRANSFER",
                "Transfer",
                null,
                (short) 2,
                "ACTIVE"
        );

        List<TransactionTypeResponse> results =
                transactionTypeService.getAllTransactionTypes();

        assertEquals(
                3,
                results.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                results.get(0).getDisplayOrder()
        );

        assertEquals(
                Short.valueOf((short) 2),
                results.get(1).getDisplayOrder()
        );

        assertEquals(
                Short.valueOf((short) 3),
                results.get(2).getDisplayOrder()
        );
    }

    @Test
    void shouldRejectUnknownTransactionTypeId() {

        UUID unknownTransactionTypeId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        transactionTypeService.getTransactionTypeById(
                                unknownTransactionTypeId
                        )
        );
    }

    @Test
    void shouldRejectUnknownTransactionTypeCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        transactionTypeService.getTransactionTypeByCode(
                                "UNKNOWN"
                        )
        );
    }

    @Test
    void shouldPersistTransactionTypeFields() {

        TransactionTypeRequest request =
                new TransactionTypeRequest();

        request.setTransactionTypeCode("REFUND");
        request.setTransactionTypeName("Refund");
        request.setDescription("Refund transaction type");
        request.setDisplayOrder((short) 4);
        request.setStatus("ACTIVE");

        TransactionTypeResponse created =
                transactionTypeService.createTransactionType(request);

        String transactionTypeCode =
                jdbcTemplate.queryForObject(
                        """
                        SELECT transaction_type_code
                        FROM catalog.transaction_type
                        WHERE transaction_type_id = ?
                        """,
                        String.class,
                        created.getTransactionTypeId()
                );

        String transactionTypeName =
                jdbcTemplate.queryForObject(
                        """
                        SELECT transaction_type_name
                        FROM catalog.transaction_type
                        WHERE transaction_type_id = ?
                        """,
                        String.class,
                        created.getTransactionTypeId()
                );

        Short displayOrder =
                jdbcTemplate.queryForObject(
                        """
                        SELECT display_order
                        FROM catalog.transaction_type
                        WHERE transaction_type_id = ?
                        """,
                        Short.class,
                        created.getTransactionTypeId()
                );

        assertEquals(
                "REFUND",
                transactionTypeCode
        );

        assertEquals(
                "Refund",
                transactionTypeName
        );

        assertEquals(
                Short.valueOf((short) 4),
                displayOrder
        );
    }

    private void insertTransactionType(
            UUID transactionTypeId,
            String transactionTypeCode,
            String transactionTypeName,
            String description,
            Short displayOrder,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.transaction_type (
                    transaction_type_id,
                    transaction_type_code,
                    transaction_type_name,
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
                    clock_timestamp()
                )
                """,
                transactionTypeId,
                transactionTypeCode,
                transactionTypeName,
                description,
                displayOrder,
                status
        );
    }
}