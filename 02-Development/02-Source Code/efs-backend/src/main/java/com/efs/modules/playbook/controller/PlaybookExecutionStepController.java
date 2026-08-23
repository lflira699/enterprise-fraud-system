package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookExecutionStepRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionStepResponse;
import com.efs.modules.playbook.service.PlaybookExecutionStepServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playbook-execution-steps")
public class PlaybookExecutionStepController {

    private final PlaybookExecutionStepServiceInterface
            playbookExecutionStepService;

    public PlaybookExecutionStepController(
            PlaybookExecutionStepServiceInterface
                    playbookExecutionStepService
    ) {
        this.playbookExecutionStepService =
                playbookExecutionStepService;
    }

    @PostMapping
    public ResponseEntity<PlaybookExecutionStepResponse> create(
            @RequestBody PlaybookExecutionStepRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        playbookExecutionStepService.create(request)
                );
    }

    @GetMapping("/{playbookExecutionStepId}")
    public ResponseEntity<PlaybookExecutionStepResponse> getById(
            @PathVariable UUID playbookExecutionStepId
    ) {
        return ResponseEntity.ok(
                playbookExecutionStepService.getById(
                        playbookExecutionStepId
                )
        );
    }

    @GetMapping("/execution/{playbookExecutionId}")
    public ResponseEntity<List<PlaybookExecutionStepResponse>>
            getByPlaybookExecutionId(
                    @PathVariable UUID playbookExecutionId
            ) {
        return ResponseEntity.ok(
                playbookExecutionStepService
                        .getByPlaybookExecutionId(
                                playbookExecutionId
                        )
        );
    }

    @PutMapping("/{playbookExecutionStepId}")
    public ResponseEntity<PlaybookExecutionStepResponse> update(
            @PathVariable UUID playbookExecutionStepId,
            @RequestBody PlaybookExecutionStepRequest request
    ) {
        return ResponseEntity.ok(
                playbookExecutionStepService.update(
                        playbookExecutionStepId,
                        request
                )
        );
    }
}