package com.efs.modules.transaction.validator;

import com.efs.modules.transaction.dto.TransactionChannelRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionChannelValueValidator {

    public void validate(
            TransactionChannelRequest request) {

        Integer sessionDuration =
                request.getSessionDuration();

        if (
                sessionDuration != null
                        && sessionDuration < 0
        ) {
            throw new IllegalArgumentException(
                    "Session duration cannot be negative: "
                            + sessionDuration
            );
        }
    }
}
