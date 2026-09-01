package com.efs.modules.alert.controller;

import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;
import com.efs.modules.alert.service.AlertServiceInterface;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertServiceInterface alertService;

    public AlertController(
            AlertServiceInterface alertService) {

        this.alertService =
                alertService;
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(
            @Valid @RequestBody AlertRequest request) {

        AlertResponse response =
                alertService.createAlert(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{alertId}")
    public ResponseEntity<AlertResponse> getAlertById(
            @PathVariable UUID alertId) {

        return ResponseEntity.ok(
                alertService.getAlertById(
                        alertId
                )
        );
    }

    @PatchMapping("/{alertId}/status")
    public ResponseEntity<AlertResponse> updateAlertStatus(
            @PathVariable UUID alertId,
            @Valid @RequestBody AlertStatusUpdateRequest request) {

        return ResponseEntity.ok(
                alertService.updateAlertStatus(
                        alertId,
                        request
                )
        );
    }

    @PatchMapping("/{alertId}/assignment")
    public ResponseEntity<AlertResponse> assignAlert(
            @PathVariable UUID alertId,
            @Valid @RequestBody AlertAssignmentRequest request) {

        return ResponseEntity.ok(
                alertService.assignAlert(
                        alertId,
                        request
                )
        );
    }

    @PostMapping("/{alertId}/close")
    public ResponseEntity<AlertResponse> closeAlert(
            @PathVariable UUID alertId,
            @Valid @RequestBody AlertClosureRequest request) {

        return ResponseEntity.ok(
                alertService.closeAlert(
                        alertId,
                        request
                )
        );
    }

    @GetMapping("/{alertId}/history")
    public ResponseEntity<List<AlertHistoryResponse>>
    getAlertHistory(
            @PathVariable UUID alertId) {

        return ResponseEntity.ok(
                alertService.getAlertHistory(
                        alertId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<AlertResponse>>
    getAlertsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                alertService.getAlertsByTransactionId(
                        transactionId
                )
        );
    }

    @GetMapping("/decision/{decisionId}")
    public ResponseEntity<List<AlertResponse>>
    getAlertsByDecisionId(
            @PathVariable UUID decisionId) {

        return ResponseEntity.ok(
                alertService.getAlertsByDecisionId(
                        decisionId
                )
        );
    }

    @GetMapping
    public ResponseEntity<PageResponse<AlertResponse>> searchAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdTo,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String scenarioCode,
            @RequestParam(required = false) UUID caseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "generatedAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        return ResponseEntity.ok(
                alertService.searchAlerts(
                        status,
                        priority,
                        riskLevel,
                        assignedTo,
                        createdFrom,
                        createdTo,
                        customerId,
                        scenarioCode,
                        caseId,
                        page,
                        size,
                        sort,
                        direction
                )
        );
    }
}
