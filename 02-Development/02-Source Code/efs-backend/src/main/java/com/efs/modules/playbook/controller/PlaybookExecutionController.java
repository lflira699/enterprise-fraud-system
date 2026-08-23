package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.service.PlaybookExecutionServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playbook-executions")
public class PlaybookExecutionController {

    private final PlaybookExecutionServiceInterface playbookExecutionService;

    public PlaybookExecutionController(
            PlaybookExecutionServiceInterface playbookExecutionService
    ) {
        this.playbookExecutionService = playbookExecutionService;
    }

    @PostMapping
    public ResponseEntity<PlaybookExecutionResponse> create(
            @RequestBody PlaybookExecutionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playbookExecutionService.create(request));
    }

    @GetMapping("/{playbookExecutionId}")
    public ResponseEntity<PlaybookExecutionResponse> getById(
            @PathVariable UUID playbookExecutionId
    ) {
        return ResponseEntity.ok(
                playbookExecutionService.getById(playbookExecutionId)
        );
    }

    @GetMapping("/version/{playbookVersionId}")
    public ResponseEntity<List<PlaybookExecutionResponse>>
            getByPlaybookVersionId(
                    @PathVariable UUID playbookVersionId
            ) {
        return ResponseEntity.ok(
                playbookExecutionService.getByPlaybookVersionId(
                        playbookVersionId
                )
        );
    }

    @GetMapping("/alert/{alertId}")
    public ResponseEntity<List<PlaybookExecutionResponse>>
            getByAlertId(
                    @PathVariable UUID alertId
            ) {
        return ResponseEntity.ok(
                playbookExecutionService.getByAlertId(alertId)
        );
    }

    @GetMapping
    public ResponseEntity<List<PlaybookExecutionResponse>>
            getByStatus(
                    @RequestParam String status
            ) {
        return ResponseEntity.ok(
                playbookExecutionService.getByStatus(status)
        );
    }

    @PutMapping("/{playbookExecutionId}")
    public ResponseEntity<PlaybookExecutionResponse> update(
            @PathVariable UUID playbookExecutionId,
            @RequestBody PlaybookExecutionRequest request
    ) {
        return ResponseEntity.ok(
                playbookExecutionService.update(
                        playbookExecutionId,
                        request
                )
        );
    }
}