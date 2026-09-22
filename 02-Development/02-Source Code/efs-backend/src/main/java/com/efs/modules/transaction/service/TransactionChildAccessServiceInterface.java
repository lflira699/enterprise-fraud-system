package com.efs.modules.transaction.service;

import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TransactionChildAccessServiceInterface {

    <REQUEST, RESPONSE> RESPONSE create(
            SecurityContext securityContext,
            UUID transactionId,
            REQUEST request,
            BiFunction<UUID, REQUEST, RESPONSE> creator
    );

    <RESPONSE> RESPONSE getById(
            SecurityContext securityContext,
            UUID childId,
            Function<UUID, RESPONSE> getter,
            Function<RESPONSE, UUID> transactionIdExtractor,
            String childNotFoundMessage
    );

    <RESPONSE> List<RESPONSE> getByTransactionId(
            SecurityContext securityContext,
            UUID transactionId,
            Function<UUID, List<RESPONSE>> getter
    );

    <RESPONSE> List<RESPONSE> filterVisible(
            SecurityContext securityContext,
            Supplier<List<RESPONSE>> getter,
            Function<RESPONSE, UUID> transactionIdExtractor
    );
}