package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
import com.efs.modules.transaction.dto.TransactionPaymentMethodResponse;
import com.efs.modules.transaction.service.TransactionPaymentMethodServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionPaymentMethodController {

    private final TransactionPaymentMethodServiceInterface
            transactionPaymentMethodService;

    public TransactionPaymentMethodController(
            TransactionPaymentMethodServiceInterface
                    transactionPaymentMethodService) {

        this.transactionPaymentMethodService =
                transactionPaymentMethodService;
    }

    @PostMapping("/{transactionId}/payment-methods")
    public ResponseEntity<TransactionPaymentMethodResponse>
    createPaymentMethod(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionPaymentMethodRequest request) {

        TransactionPaymentMethodResponse response =
                transactionPaymentMethodService.createPaymentMethod(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/payment-methods/{paymentMethodId}")
    public ResponseEntity<TransactionPaymentMethodResponse>
    getPaymentMethodById(
            @PathVariable UUID paymentMethodId) {

        return ResponseEntity.ok(
                transactionPaymentMethodService
                        .getPaymentMethodById(paymentMethodId)
        );
    }

    @GetMapping("/{transactionId}/payment-methods")
    public ResponseEntity<List<TransactionPaymentMethodResponse>>
    getPaymentMethodsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionPaymentMethodService
                        .getPaymentMethodsByTransactionId(
                                transactionId
                        )
        );
    }
}