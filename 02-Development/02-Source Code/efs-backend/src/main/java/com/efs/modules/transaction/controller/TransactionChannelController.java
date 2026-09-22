package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionChannelRequest;
import com.efs.modules.transaction.dto.TransactionChannelResponse;
import com.efs.modules.transaction.service.TransactionChannelServiceInterface;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionChannelController {

    private final TransactionChannelServiceInterface
            transactionChannelService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionChannelController(
            TransactionChannelServiceInterface transactionChannelService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionChannelService =
                transactionChannelService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/channels")
    public ResponseEntity<TransactionChannelResponse> createChannel(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionChannelRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionChannelResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionChannelService
                                        ::createChannel
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/channels/{channelTransactionId}")
    public ResponseEntity<TransactionChannelResponse> getChannelById(
            @PathVariable UUID channelTransactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                channelTransactionId,
                                transactionChannelService
                                        ::getChannelById,
                                TransactionChannelResponse
                                        ::getTransactionId,
                                "Transaction channel not found: "
                                        + channelTransactionId
                        )
        );
    }

    @GetMapping("/{transactionId}/channels")
    public ResponseEntity<List<TransactionChannelResponse>>
    getChannelsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionChannelService
                                        ::getChannelsByTransactionId
                        )
        );
    }

    @GetMapping("/channels/type/{channelType}")
    public ResponseEntity<List<TransactionChannelResponse>>
    getChannelsByType(
            @PathVariable String channelType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionChannelService
                                                .getChannelsByType(
                                                        channelType
                                                ),
                                TransactionChannelResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/channels/application/{applicationName}")
    public ResponseEntity<List<TransactionChannelResponse>>
    getChannelsByApplicationName(
            @PathVariable String applicationName) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionChannelService
                                                .getChannelsByApplicationName(
                                                        applicationName
                                                ),
                                TransactionChannelResponse
                                        ::getTransactionId
                        )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}
