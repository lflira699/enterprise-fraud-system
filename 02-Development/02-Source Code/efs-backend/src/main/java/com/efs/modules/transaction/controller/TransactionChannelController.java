package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionChannelRequest;
import com.efs.modules.transaction.dto.TransactionChannelResponse;
import com.efs.modules.transaction.service.TransactionChannelServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionChannelController {

    private final TransactionChannelServiceInterface
            transactionChannelService;

    public TransactionChannelController(
            TransactionChannelServiceInterface transactionChannelService) {

        this.transactionChannelService =
                transactionChannelService;
    }

    @PostMapping("/{transactionId}/channels")
    public ResponseEntity<TransactionChannelResponse> createChannel(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionChannelRequest request) {

        TransactionChannelResponse response =
                transactionChannelService.createChannel(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/channels/{channelTransactionId}")
    public ResponseEntity<TransactionChannelResponse> getChannelById(
            @PathVariable UUID channelTransactionId) {

        return ResponseEntity.ok(
                transactionChannelService
                        .getChannelById(channelTransactionId)
        );
    }

    @GetMapping("/{transactionId}/channels")
    public ResponseEntity<List<TransactionChannelResponse>>
    getChannelsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionChannelService
                        .getChannelsByTransactionId(transactionId)
        );
    }

    @GetMapping("/channels/type/{channelType}")
    public ResponseEntity<List<TransactionChannelResponse>>
    getChannelsByType(
            @PathVariable String channelType) {

        return ResponseEntity.ok(
                transactionChannelService
                        .getChannelsByType(channelType)
        );
    }

    @GetMapping("/channels/application/{applicationName}")
    public ResponseEntity<List<TransactionChannelResponse>>
    getChannelsByApplicationName(
            @PathVariable String applicationName) {

        return ResponseEntity.ok(
                transactionChannelService
                        .getChannelsByApplicationName(applicationName)
        );
    }
}