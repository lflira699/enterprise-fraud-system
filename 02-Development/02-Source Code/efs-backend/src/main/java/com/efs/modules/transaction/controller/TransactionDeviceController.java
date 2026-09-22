package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionDeviceRequest;
import com.efs.modules.transaction.dto.TransactionDeviceResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionDeviceServiceInterface;
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
public class TransactionDeviceController {

    private final TransactionDeviceServiceInterface
            transactionDeviceService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider securityContextProvider;

    public TransactionDeviceController(
            TransactionDeviceServiceInterface transactionDeviceService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionDeviceService =
                transactionDeviceService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/devices")
    public ResponseEntity<TransactionDeviceResponse> createDevice(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionDeviceRequest request) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        TransactionDeviceResponse response =
                transactionChildAccessService.create(
                        securityContext,
                        transactionId,
                        request,
                        transactionDeviceService::createDevice
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/devices/{deviceTransactionId}")
    public ResponseEntity<TransactionDeviceResponse> getDeviceById(
            @PathVariable UUID deviceTransactionId) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.getById(
                        securityContext,
                        deviceTransactionId,
                        transactionDeviceService::getDeviceById,
                        TransactionDeviceResponse::getTransactionId,
                        "Transaction device not found: "
                                + deviceTransactionId
                )
        );
    }

    @GetMapping("/{transactionId}/devices")
    public ResponseEntity<List<TransactionDeviceResponse>>
    getDevicesByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.getByTransactionId(
                        securityContext,
                        transactionId,
                        transactionDeviceService
                                ::getDevicesByTransactionId
                )
        );
    }

    @GetMapping("/devices/fingerprint/{deviceFingerprint}")
    public ResponseEntity<List<TransactionDeviceResponse>>
    getDevicesByFingerprint(
            @PathVariable String deviceFingerprint) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.filterVisible(
                        securityContext,
                        () ->
                                transactionDeviceService
                                        .getDevicesByFingerprint(
                                                deviceFingerprint
                                        ),
                        TransactionDeviceResponse::getTransactionId
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