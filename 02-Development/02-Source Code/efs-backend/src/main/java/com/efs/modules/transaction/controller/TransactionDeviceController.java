package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionDeviceRequest;
import com.efs.modules.transaction.dto.TransactionDeviceResponse;
import com.efs.modules.transaction.service.TransactionDeviceServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionDeviceController {

    private final TransactionDeviceServiceInterface
            transactionDeviceService;

    public TransactionDeviceController(
            TransactionDeviceServiceInterface transactionDeviceService) {

        this.transactionDeviceService = transactionDeviceService;
    }

    @PostMapping("/{transactionId}/devices")
    public ResponseEntity<TransactionDeviceResponse> createDevice(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionDeviceRequest request) {

        TransactionDeviceResponse response =
                transactionDeviceService.createDevice(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/devices/{deviceTransactionId}")
    public ResponseEntity<TransactionDeviceResponse> getDeviceById(
            @PathVariable UUID deviceTransactionId) {

        return ResponseEntity.ok(
                transactionDeviceService
                        .getDeviceById(deviceTransactionId)
        );
    }

    @GetMapping("/{transactionId}/devices")
    public ResponseEntity<List<TransactionDeviceResponse>>
    getDevicesByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionDeviceService
                        .getDevicesByTransactionId(transactionId)
        );
    }

    @GetMapping("/devices/fingerprint/{deviceFingerprint}")
    public ResponseEntity<List<TransactionDeviceResponse>>
    getDevicesByFingerprint(
            @PathVariable String deviceFingerprint) {

        return ResponseEntity.ok(
                transactionDeviceService
                        .getDevicesByFingerprint(deviceFingerprint)
        );
    }
}