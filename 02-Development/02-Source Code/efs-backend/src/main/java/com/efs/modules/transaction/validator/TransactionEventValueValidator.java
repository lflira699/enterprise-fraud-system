package com.efs.modules.transaction.validator;

import com.efs.modules.transaction.dto.TransactionEventRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventValueValidator {

    public void validate(
            TransactionEventRequest request) {

        Integer executionTimeMs =
                request.getExecutionTimeMs();

        if (
                executionTimeMs != null
                        && executionTimeMs < 0
        ) {
            throw new IllegalArgumentException(
                    "Execution time cannot be negative: "
                            + executionTimeMs
            );
        }
    }
}
