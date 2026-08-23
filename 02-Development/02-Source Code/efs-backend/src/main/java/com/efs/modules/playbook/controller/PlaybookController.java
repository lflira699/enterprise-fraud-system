package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.service.PlaybookServiceInterface;
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
@RequestMapping("/api/v1/playbooks")
public class PlaybookController {

    private final PlaybookServiceInterface playbookService;

    public PlaybookController(
            PlaybookServiceInterface playbookService
    ) {
        this.playbookService = playbookService;
    }

    @PostMapping
    public ResponseEntity<PlaybookResponse> create(
            @RequestBody PlaybookRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playbookService.create(request));
    }

    @GetMapping("/{playbookId}")
    public ResponseEntity<PlaybookResponse> getById(
            @PathVariable UUID playbookId
    ) {
        return ResponseEntity.ok(
                playbookService.getById(playbookId)
        );
    }

    @GetMapping("/code/{playbookCode}")
    public ResponseEntity<PlaybookResponse> getByCode(
            @PathVariable String playbookCode
    ) {
        return ResponseEntity.ok(
                playbookService.getByCode(playbookCode)
        );
    }

    @GetMapping
    public ResponseEntity<List<PlaybookResponse>> getAll(
            @RequestParam(required = false) String status
    ) {
        if (status != null) {
            return ResponseEntity.ok(
                    playbookService.getByStatus(status)
            );
        }

        return ResponseEntity.ok(
                playbookService.getAll()
        );
    }

    @PutMapping("/{playbookId}")
    public ResponseEntity<PlaybookResponse> update(
            @PathVariable UUID playbookId,
            @RequestBody PlaybookRequest request
    ) {
        return ResponseEntity.ok(
                playbookService.update(
                        playbookId,
                        request
                )
        );
    }
}