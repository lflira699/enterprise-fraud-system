package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.TransactionTypeRequest;
import com.efs.modules.catalog.dto.TransactionTypeResponse;
import com.efs.modules.catalog.service.TransactionTypeServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transaction-types")
public class TransactionTypeController {

    private final TransactionTypeServiceInterface transactionTypeService;

    public TransactionTypeController(
            TransactionTypeServiceInterface transactionTypeService) {

        this.transactionTypeService =
                transactionTypeService;
    }

    @PostMapping
    public ResponseEntity<TransactionTypeResponse> createTransactionType(
            @Valid @RequestBody TransactionTypeRequest request) {

        TransactionTypeResponse response =
                transactionTypeService.createTransactionType(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{transactionTypeId}")
    public ResponseEntity<TransactionTypeResponse> getTransactionTypeById(
            @PathVariable UUID transactionTypeId) {

        return ResponseEntity.ok(
                transactionTypeService.getTransactionTypeById(
                        transactionTypeId
                )
        );
    }

    @GetMapping("/code/{transactionTypeCode}")
    public ResponseEntity<TransactionTypeResponse> getTransactionTypeByCode(
            @PathVariable String transactionTypeCode) {

        return ResponseEntity.ok(
                transactionTypeService.getTransactionTypeByCode(
                        transactionTypeCode
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<TransactionTypeResponse>> getTransactionTypes(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    transactionTypeService.getTransactionTypesByStatus(
                            status
                    )
            );
        }

        return ResponseEntity.ok(
                transactionTypeService.getAllTransactionTypes()
        );
    }
}