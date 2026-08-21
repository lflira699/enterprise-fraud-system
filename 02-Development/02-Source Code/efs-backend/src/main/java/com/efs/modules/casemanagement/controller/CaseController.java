package com.efs.modules.casemanagement.controller;

import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseFromAlertRequest;
import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
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
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseServiceInterface caseService;

    public CaseController(
            CaseServiceInterface caseService) {

        this.caseService =
                caseService;
    }

    @PostMapping
    public ResponseEntity<CaseResponse> createCase(
            @Valid @RequestBody CaseRequest request) {

        CaseResponse response =
                caseService.createCase(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/from-alert")
    public ResponseEntity<CaseResponse> createCaseFromAlert(
            @Valid @RequestBody CaseFromAlertRequest request) {

        CaseResponse response =
                caseService.createCaseFromAlert(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{caseId}/assignments")
    public ResponseEntity<CaseAssignmentResponse> assignCase(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseAssignmentRequest request) {

        CaseAssignmentResponse response =
                caseService.assignCase(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/assignments")
    public ResponseEntity<List<CaseAssignmentResponse>>
    getCaseAssignments(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseAssignments(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<CaseResponse> getCaseById(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseById(
                        caseId
                )
        );
    }

    @GetMapping("/number/{caseNumber}")
    public ResponseEntity<CaseResponse> getCaseByNumber(
            @PathVariable String caseNumber) {

        return ResponseEntity.ok(
                caseService.getCaseByNumber(
                        caseNumber
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CaseResponse>>
    getCasesByCustomerId(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                caseService.getCasesByCustomerId(
                        customerId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<CaseResponse>>
    getCasesByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                caseService.getCasesByTransactionId(
                        transactionId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<CaseResponse>> searchCases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID assignedUser,
            @RequestParam(required = false) String assignedTeam) {

        if (status != null) {
            return ResponseEntity.ok(
                    caseService.getCasesByStatus(
                            status
                    )
            );
        }

        if (priority != null) {
            return ResponseEntity.ok(
                    caseService.getCasesByPriority(
                            priority
                    )
            );
        }

        if (assignedUser != null) {
            return ResponseEntity.ok(
                    caseService.getCasesByAssignedUser(
                            assignedUser
                    )
            );
        }

        if (assignedTeam != null) {
            return ResponseEntity.ok(
                    caseService.getCasesByAssignedTeam(
                            assignedTeam
                    )
            );
        }

        return ResponseEntity.ok(
                List.of()
        );
    }
}