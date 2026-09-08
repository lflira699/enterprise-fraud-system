package com.efs.modules.rules.service;

import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionRuleTestDatasetProvider
        implements RuleTestDatasetProvider {

    private static final String REFERENCE_PREFIX =
            "transaction://";

    private final TransactionRepository transactionRepository;

    public TransactionRuleTestDatasetProvider(
            TransactionRepository transactionRepository) {

        this.transactionRepository =
                transactionRepository;
    }

    @Override
    public boolean supports(
            String datasetReference) {

        return datasetReference != null
                && datasetReference
                .trim()
                .startsWith(
                        REFERENCE_PREFIX
                );
    }

    @Override
    @Transactional(readOnly = true)
    public RuleTestDataset load(
            String datasetReference) {

        String normalizedReference =
                normalizeReference(
                        datasetReference
                );

        UUID transactionId =
                parseTransactionId(
                        normalizedReference
                );

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                transactionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule test transaction "
                                                + "not found: "
                                                + transactionId
                                )
                        );

        Map<String, Object> transactionFacts =
                buildTransactionFacts(
                        transaction
                );

        Map<String, Object> facts =
                new LinkedHashMap<>();

        facts.put(
                "transaction",
                transactionFacts
        );

        return new RuleTestDataset(
                normalizedReference,
                List.of(
                        facts
                )
        );
    }

    private String normalizeReference(
            String datasetReference) {

        if (datasetReference == null
                || datasetReference.isBlank()) {

            throw new ValidationException(
                    "Transaction dataset reference "
                            + "is required"
            );
        }

        String normalizedReference =
                datasetReference.trim();

        if (!normalizedReference.startsWith(
                REFERENCE_PREFIX
        )) {

            throw new ValidationException(
                    "Unsupported transaction dataset "
                            + "reference: "
                            + normalizedReference
            );
        }

        return normalizedReference;
    }

    private UUID parseTransactionId(
            String datasetReference) {

        String identifier =
                datasetReference.substring(
                        REFERENCE_PREFIX.length()
                );

        if (identifier.isBlank()) {
            throw new ValidationException(
                    "Transaction identifier is required "
                            + "in dataset reference"
            );
        }

        try {

            return UUID.fromString(
                    identifier
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Invalid transaction identifier "
                            + "in dataset reference"
            );
        }
    }

    private Map<String, Object> buildTransactionFacts(
            Transaction transaction) {

        Map<String, Object> facts =
                new LinkedHashMap<>();

        putIfNotNull(
                facts,
                "id",
                transaction.getTransactionId()
        );

        putIfNotNull(
                facts,
                "reference",
                transaction.getTransactionReference()
        );

        putIfNotNull(
                facts,
                "customerId",
                transaction.getCustomerId()
        );

        putIfNotNull(
                facts,
                "type",
                transaction.getTransactionType()
        );

        putIfNotNull(
                facts,
                "subtype",
                transaction.getTransactionSubtype()
        );

        putIfNotNull(
                facts,
                "amount",
                transaction.getAmount()
        );

        putIfNotNull(
                facts,
                "currency",
                transaction.getCurrencyCode()
        );

        putIfNotNull(
                facts,
                "datetime",
                transaction.getTransactionDatetime()
        );

        putIfNotNull(
                facts,
                "status",
                transaction.getTransactionStatus()
        );

        putIfNotNull(
                facts,
                "finalDecision",
                transaction.getFinalDecision()
        );

        putIfNotNull(
                facts,
                "fraudScore",
                transaction.getFraudScore()
        );

        putIfNotNull(
                facts,
                "correlationId",
                transaction.getCorrelationId()
        );

        putIfNotNull(
                facts,
                "requestId",
                transaction.getRequestId()
        );

        putIfNotNull(
                facts,
                "sessionId",
                transaction.getSessionId()
        );

        putIfNotNull(
                facts,
                "tenantId",
                transaction.getTenantId()
        );

        return facts;
    }

    private void putIfNotNull(
            Map<String, Object> facts,
            String name,
            Object value) {

        if (value != null) {
            facts.put(
                    name,
                    value
            );
        }
    }
}