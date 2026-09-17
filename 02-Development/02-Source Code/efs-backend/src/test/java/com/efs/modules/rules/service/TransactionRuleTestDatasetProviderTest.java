package com.efs.modules.rules.service;

import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionRuleTestDatasetProviderTest {

    @Test
    void shouldRecognizeOnlyTransactionDatasetReferences() {

        TransactionServiceInterface transactionService =
                mock(
                        TransactionServiceInterface.class
                );

        TransactionRuleTestDatasetProvider provider =
                new TransactionRuleTestDatasetProvider(
                        transactionService
                );

        assertTrue(
                provider.supports(
                        "transaction://"
                                + UUID.randomUUID()
                )
        );

        assertTrue(
                provider.supports(
                        "  transaction://"
                                + UUID.randomUUID()
                                + "  "
                )
        );

        assertFalse(
                provider.supports(
                        "dataset://uc025/sample"
                )
        );

        assertFalse(
                provider.supports(
                        null
                )
        );
    }

    @Test
    void shouldLoadTransactionAsNormalizedRuleFacts() {

        TransactionServiceInterface transactionService =
                mock(
                        TransactionServiceInterface.class
                );

        TransactionRuleTestDatasetProvider provider =
                new TransactionRuleTestDatasetProvider(
                        transactionService
                );

        UUID transactionId =
                UUID.randomUUID();

        UUID customerId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        TransactionResponse transaction =
                new TransactionResponse();

        transaction.setTransactionId(
                transactionId
        );

        transaction.setTransactionReference(
                "TX-UC025-001"
        );

        transaction.setCustomerId(
                customerId
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setTransactionSubtype(
                "CARD"
        );

        transaction.setAmount(
                new BigDecimal(
                        "7500.00"
                )
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        14,
                        30
                )
        );

        transaction.setTransactionStatus(
                "RECEIVED"
        );

        transaction.setFinalDecision(
                "PENDING"
        );

        transaction.setFraudScore(
                new BigDecimal(
                        "0.00"
                )
        );

        transaction.setCorrelationId(
                correlationId
        );

        when(
                transactionService
                        .getTransactionById(
                                transactionId
                        )
        ).thenReturn(
                transaction
        );

        String reference =
                "transaction://"
                        + transactionId;

        RuleTestDataset dataset =
                provider.load(
                        reference
                );

        assertEquals(
                reference,
                dataset.getDatasetReference()
        );

        assertEquals(
                1,
                dataset.getSampleSize()
        );

        Map<String, Object> transactionFacts =
                (Map<String, Object>)
                        dataset.getRecords()
                                .getFirst()
                                .get(
                                        "transaction"
                                );

        assertEquals(
                transactionId,
                transactionFacts.get(
                        "id"
                )
        );

        assertEquals(
                "TX-UC025-001",
                transactionFacts.get(
                        "reference"
                )
        );

        assertEquals(
                customerId,
                transactionFacts.get(
                        "customerId"
                )
        );

        assertEquals(
                "PAYMENT",
                transactionFacts.get(
                        "type"
                )
        );

        assertEquals(
                "CARD",
                transactionFacts.get(
                        "subtype"
                )
        );

        assertEquals(
                new BigDecimal(
                        "7500.00"
                ),
                transactionFacts.get(
                        "amount"
                )
        );

        assertEquals(
                "GTQ",
                transactionFacts.get(
                        "currency"
                )
        );

        assertEquals(
                "RECEIVED",
                transactionFacts.get(
                        "status"
                )
        );

        assertEquals(
                "PENDING",
                transactionFacts.get(
                        "finalDecision"
                )
        );

        assertEquals(
                correlationId,
                transactionFacts.get(
                        "correlationId"
                )
        );

        verify(
                transactionService
        ).getTransactionById(
                transactionId
        );
    }

    @Test
    void shouldRejectMalformedTransactionDatasetReference() {

        TransactionServiceInterface transactionService =
                mock(
                        TransactionServiceInterface.class
                );

        TransactionRuleTestDatasetProvider provider =
                new TransactionRuleTestDatasetProvider(
                        transactionService
                );

        assertThrows(
                ValidationException.class,
                () ->
                        provider.load(
                                "transaction://not-a-uuid"
                        )
        );

        verify(
                transactionService,
                never()
        ).getTransactionById(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRejectUnknownTransaction() {

        TransactionServiceInterface transactionService =
                mock(
                        TransactionServiceInterface.class
                );

        TransactionRuleTestDatasetProvider provider =
                new TransactionRuleTestDatasetProvider(
                        transactionService
                );

        UUID transactionId =
                UUID.randomUUID();

        when(
                transactionService
                        .getTransactionById(
                                transactionId
                        )
        ).thenThrow(
                new ResourceNotFoundException(
                        "Transaction not found: "
                                + transactionId
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        provider.load(
                                "transaction://"
                                        + transactionId
                        )
        );
    }
}