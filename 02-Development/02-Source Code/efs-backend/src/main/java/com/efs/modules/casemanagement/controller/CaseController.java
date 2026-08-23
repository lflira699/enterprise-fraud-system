package com.efs.modules.casemanagement.controller;

import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseCommentRequest;
import com.efs.modules.casemanagement.dto.CaseCommentResponse;
import com.efs.modules.casemanagement.dto.CaseEscalationRequest;
import com.efs.modules.casemanagement.dto.CaseEscalationResponse;
import com.efs.modules.casemanagement.dto.CaseEvidenceRequest;
import com.efs.modules.casemanagement.dto.CaseEvidenceResponse;
import com.efs.modules.casemanagement.dto.CaseFromAlertRequest;
import com.efs.modules.casemanagement.dto.CaseHistoryRequest;
import com.efs.modules.casemanagement.dto.CaseHistoryResponse;
import com.efs.modules.casemanagement.dto.CaseNotificationRequest;
import com.efs.modules.casemanagement.dto.CaseNotificationResponse;
import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResolutionRequest;
import com.efs.modules.casemanagement.dto.CaseResolutionResponse;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.dto.CaseSlaRequest;
import com.efs.modules.casemanagement.dto.CaseSlaResponse;
import com.efs.modules.casemanagement.dto.CaseStatusHistoryResponse;
import com.efs.modules.casemanagement.dto.CaseStatusUpdateRequest;
import com.efs.modules.casemanagement.dto.CaseTaskRequest;
import com.efs.modules.casemanagement.dto.CaseTaskResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PostMapping("/{caseId}/tasks")
    public ResponseEntity<CaseTaskResponse> createCaseTask(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseTaskRequest request) {

        CaseTaskResponse response =
                caseService.createCaseTask(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/tasks")
    public ResponseEntity<List<CaseTaskResponse>> getCaseTasks(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseTasks(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/tasks/{taskId}")
    public ResponseEntity<CaseTaskResponse> getCaseTaskById(
            @PathVariable UUID caseId,
            @PathVariable UUID taskId) {

        return ResponseEntity.ok(
                caseService.getCaseTaskById(
                        caseId,
                        taskId
                )
        );
    }

    @PostMapping("/{caseId}/comments")
    public ResponseEntity<CaseCommentResponse> createCaseComment(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseCommentRequest request) {

        CaseCommentResponse response =
                caseService.createCaseComment(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/comments")
    public ResponseEntity<List<CaseCommentResponse>> getCaseComments(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseComments(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/comments/{commentId}")
    public ResponseEntity<CaseCommentResponse> getCaseCommentById(
            @PathVariable UUID caseId,
            @PathVariable UUID commentId) {

        return ResponseEntity.ok(
                caseService.getCaseCommentById(
                        caseId,
                        commentId
                )
        );
    }

    @PostMapping("/{caseId}/evidence")
    public ResponseEntity<CaseEvidenceResponse> createCaseEvidence(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseEvidenceRequest request) {

        CaseEvidenceResponse response =
                caseService.createCaseEvidence(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/evidence")
    public ResponseEntity<List<CaseEvidenceResponse>> getCaseEvidence(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseEvidence(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/evidence/{evidenceId}")
    public ResponseEntity<CaseEvidenceResponse> getCaseEvidenceById(
            @PathVariable UUID caseId,
            @PathVariable UUID evidenceId) {

        return ResponseEntity.ok(
                caseService.getCaseEvidenceById(
                        caseId,
                        evidenceId
                )
        );
    }

    @PatchMapping("/{caseId}/status")
    public ResponseEntity<CaseResponse> updateCaseStatus(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseStatusUpdateRequest request) {

        return ResponseEntity.ok(
                caseService.updateCaseStatus(
                        caseId,
                        request
                )
        );
    }

    @GetMapping("/{caseId}/status-history")
    public ResponseEntity<List<CaseStatusHistoryResponse>>
    getCaseStatusHistory(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseStatusHistory(
                        caseId
                )
        );
    }

    @PostMapping("/{caseId}/resolutions")
    public ResponseEntity<CaseResolutionResponse> createCaseResolution(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseResolutionRequest request) {

        CaseResolutionResponse response =
                caseService.createCaseResolution(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/resolutions")
    public ResponseEntity<List<CaseResolutionResponse>>
    getCaseResolutions(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseResolutions(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/resolutions/{resolutionId}")
    public ResponseEntity<CaseResolutionResponse>
    getCaseResolutionById(
            @PathVariable UUID caseId,
            @PathVariable UUID resolutionId) {

        return ResponseEntity.ok(
                caseService.getCaseResolutionById(
                        caseId,
                        resolutionId
                )
        );
    }

    @PostMapping("/{caseId}/escalations")
    public ResponseEntity<CaseEscalationResponse> createCaseEscalation(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseEscalationRequest request) {

        CaseEscalationResponse response =
                caseService.createCaseEscalation(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/escalations")
    public ResponseEntity<List<CaseEscalationResponse>>
    getCaseEscalations(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseEscalations(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/escalations/{escalationId}")
    public ResponseEntity<CaseEscalationResponse>
    getCaseEscalationById(
            @PathVariable UUID caseId,
            @PathVariable UUID escalationId) {

        return ResponseEntity.ok(
                caseService.getCaseEscalationById(
                        caseId,
                        escalationId
                )
        );
    }

    @PostMapping("/{caseId}/slas")
    public ResponseEntity<CaseSlaResponse> createCaseSla(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseSlaRequest request) {

        CaseSlaResponse response =
                caseService.createCaseSla(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/slas")
    public ResponseEntity<List<CaseSlaResponse>>
    getCaseSlas(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseSlas(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/slas/{slaId}")
    public ResponseEntity<CaseSlaResponse>
    getCaseSlaById(
            @PathVariable UUID caseId,
            @PathVariable UUID slaId) {

        return ResponseEntity.ok(
                caseService.getCaseSlaById(
                        caseId,
                        slaId
                )
        );
    }

    @PostMapping("/{caseId}/notifications")
    public ResponseEntity<CaseNotificationResponse>
    createCaseNotification(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseNotificationRequest request) {

        CaseNotificationResponse response =
                caseService.createCaseNotification(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/notifications")
    public ResponseEntity<List<CaseNotificationResponse>>
    getCaseNotifications(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseNotifications(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/notifications/{caseNotificationId}")
    public ResponseEntity<CaseNotificationResponse>
    getCaseNotificationById(
            @PathVariable UUID caseId,
            @PathVariable UUID caseNotificationId) {

        return ResponseEntity.ok(
                caseService.getCaseNotificationById(
                        caseId,
                        caseNotificationId
                )
        );
    }

    @PostMapping("/{caseId}/history")
    public ResponseEntity<CaseHistoryResponse>
    createCaseHistory(
            @PathVariable UUID caseId,
            @Valid @RequestBody CaseHistoryRequest request) {

        CaseHistoryResponse response =
                caseService.createCaseHistory(
                        caseId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{caseId}/history")
    public ResponseEntity<List<CaseHistoryResponse>>
    getCaseHistory(
            @PathVariable UUID caseId) {

        return ResponseEntity.ok(
                caseService.getCaseHistory(
                        caseId
                )
        );
    }

    @GetMapping("/{caseId}/history/{historyId}")
    public ResponseEntity<CaseHistoryResponse>
    getCaseHistoryById(
            @PathVariable UUID caseId,
            @PathVariable UUID historyId) {

        return ResponseEntity.ok(
                caseService.getCaseHistoryById(
                        caseId,
                        historyId
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