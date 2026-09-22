package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.shared.security.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Transactional(readOnly = true)
public class TransactionChildAccessService
        implements TransactionChildAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "transaction.view";

    private static final String UPDATE_PERMISSION =
            "transaction.update";

    private final TransactionScopeAuthorizationServiceInterface
            scopeAuthorizationService;

    public TransactionChildAccessService(
            TransactionScopeAuthorizationServiceInterface
                    scopeAuthorizationService) {

        this.scopeAuthorizationService =
                scopeAuthorizationService;
    }

    @Override
    @Transactional
    public <REQUEST, RESPONSE> RESPONSE create(
            SecurityContext securityContext,
            UUID transactionId,
            REQUEST request,
            BiFunction<UUID, REQUEST, RESPONSE> creator) {

        Objects.requireNonNull(
                transactionId,
                "transactionId is required"
        );

        Objects.requireNonNull(
                creator,
                "creator is required"
        );

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                UPDATE_PERMISSION
                        );

        scopeAuthorizationService
                .requireVisibleTransaction(
                        transactionId,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        return creator.apply(
                transactionId,
                request
        );
    }

    @Override
    public <RESPONSE> RESPONSE getById(
            SecurityContext securityContext,
            UUID childId,
            Function<UUID, RESPONSE> getter,
            Function<RESPONSE, UUID> transactionIdExtractor,
            String childNotFoundMessage) {

        Objects.requireNonNull(
                childId,
                "childId is required"
        );

        Objects.requireNonNull(
                getter,
                "getter is required"
        );

        Objects.requireNonNull(
                transactionIdExtractor,
                "transactionIdExtractor is required"
        );

        Objects.requireNonNull(
                childNotFoundMessage,
                "childNotFoundMessage is required"
        );

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        RESPONSE response =
                getter.apply(
                        childId
                );

        UUID transactionId =
                transactionIdExtractor.apply(
                        response
                );

        scopeAuthorizationService
                .requireVisibleTransaction(
                        transactionId,
                        actor,
                        childNotFoundMessage
                );

        return response;
    }

    @Override
    public <RESPONSE> List<RESPONSE> getByTransactionId(
            SecurityContext securityContext,
            UUID transactionId,
            Function<UUID, List<RESPONSE>> getter) {

        Objects.requireNonNull(
                transactionId,
                "transactionId is required"
        );

        Objects.requireNonNull(
                getter,
                "getter is required"
        );

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        scopeAuthorizationService
                .requireVisibleTransaction(
                        transactionId,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        return getter.apply(
                transactionId
        );
    }

    @Override
    public <RESPONSE> List<RESPONSE> filterVisible(
            SecurityContext securityContext,
            Supplier<List<RESPONSE>> getter,
            Function<RESPONSE, UUID> transactionIdExtractor) {

        Objects.requireNonNull(
                getter,
                "getter is required"
        );

        Objects.requireNonNull(
                transactionIdExtractor,
                "transactionIdExtractor is required"
        );

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        List<RESPONSE> responses =
                getter.get();

        if (
            responses == null
                    ||
            responses.isEmpty()
        ) {
            return List.of();
        }

        Map<UUID, Boolean> visibilityCache =
                new HashMap<>();

        return responses.stream()
                .filter(
                        response -> {

                            UUID transactionId =
                                    transactionIdExtractor
                                            .apply(
                                                    response
                                            );

                            if (transactionId == null) {
                                return false;
                            }

                            return visibilityCache
                                    .computeIfAbsent(
                                            transactionId,
                                            id ->
                                                    scopeAuthorizationService
                                                            .isTransactionVisible(
                                                                    id,
                                                                    actor
                                                            )
                                    );
                        }
                )
                .toList();
    }
}