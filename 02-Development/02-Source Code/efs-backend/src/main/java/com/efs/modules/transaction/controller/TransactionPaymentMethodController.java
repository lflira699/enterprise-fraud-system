package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
import com.efs.modules.transaction.dto.TransactionPaymentMethodResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionPaymentMethodServiceInterface;
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
public class TransactionPaymentMethodController {

    private final TransactionPaymentMethodServiceInterface
            transactionPaymentMethodService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider securityContextProvider;

    public TransactionPaymentMethodController(
            TransactionPaymentMethodServiceInterface
                    transactionPaymentMethodService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionPaymentMethodService =
                transactionPaymentMethodService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/payment-methods")
    public ResponseEntity<TransactionPaymentMethodResponse>
    createPaymentMethod(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionPaymentMethodRequest request) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        TransactionPaymentMethodResponse response =
                transactionChildAccessService.create(
                        securityContext,
                        transactionId,
                        request,
                        transactionPaymentMethodService
                                ::createPaymentMethod
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/payment-methods/{paymentMethodId}")
    public ResponseEntity<TransactionPaymentMethodResponse>
    getPaymentMethodById(
            @PathVariable UUID paymentMethodId) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.getById(
                        securityContext,
                        paymentMethodId,
                        transactionPaymentMethodService
                                ::getPaymentMethodById,
                        TransactionPaymentMethodResponse
                                ::getTransactionId,
                        "Transaction payment method not found: "
                                + paymentMethodId
                )
        );
    }

    @GetMapping("/{transactionId}/payment-methods")
    public ResponseEntity<List<TransactionPaymentMethodResponse>>
    getPaymentMethodsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.getByTransactionId(
                        securityContext,
                        transactionId,
                        transactionPaymentMethodService
                                ::getPaymentMethodsByTransactionId
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