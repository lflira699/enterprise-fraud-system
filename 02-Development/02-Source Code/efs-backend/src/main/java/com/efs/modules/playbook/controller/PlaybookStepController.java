package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;
import com.efs.modules.playbook.service.PlaybookStepServiceInterface;
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
@RequestMapping("/api/v1/playbook-steps")
public class PlaybookStepController {

    private final PlaybookStepServiceInterface playbookStepService;

    public PlaybookStepController(
            PlaybookStepServiceInterface playbookStepService
    ) {
        this.playbookStepService = playbookStepService;
    }

    @PostMapping
    public ResponseEntity<PlaybookStepResponse> create(
            @RequestBody PlaybookStepRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playbookStepService.create(request));
    }

    @GetMapping("/{playbookStepId}")
    public ResponseEntity<PlaybookStepResponse> getById(
            @PathVariable UUID playbookStepId
    ) {
        return ResponseEntity.ok(
                playbookStepService.getById(playbookStepId)
        );
    }

    @GetMapping("/version/{playbookVersionId}")
    public ResponseEntity<List<PlaybookStepResponse>>
            getByPlaybookVersionId(
                    @PathVariable UUID playbookVersionId
            ) {
        return ResponseEntity.ok(
                playbookStepService.getByPlaybookVersionId(
                        playbookVersionId
                )
        );
    }

    @PutMapping("/{playbookStepId}")
    public ResponseEntity<PlaybookStepResponse> update(
            @PathVariable UUID playbookStepId,
            @RequestBody PlaybookStepRequest request
    ) {
        return ResponseEntity.ok(
                playbookStepService.update(
                        playbookStepId,
                        request
                )
        );
    }
}