package com.efs.modules.alert.validator;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AlertStatusValidator {

    private static final Set<String> VALID_STATUSES =
            Set.of(
                    "NEW",
                    "ASSIGNED",
                    "IN_PROGRESS",
                    "PENDING_INFORMATION",
                    "ESCALATED",
                    "RESOLVED",
                    "CLOSED",
                    "CANCELLED"
            );

    public boolean isValidStatus(
            String status) {

        return status != null
                && VALID_STATUSES.contains(
                        normalize(status)
                );
    }

    public String normalize(
            String status) {

        return status
                .trim()
                .toUpperCase()
                .replace(" ", "_");
    }
}