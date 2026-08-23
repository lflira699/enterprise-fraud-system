package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.service.PlaybookVersionServiceInterface;
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
@RequestMapping("/api/v1/playbook-versions")
public class PlaybookVersionController {

    private final PlaybookVersionServiceInterface playbookVersionService;

    public PlaybookVersionController(
            PlaybookVersionServiceInterface playbookVersionService
    ) {
        this.playbookVersionService = playbookVersionService;
    }

    @PostMapping
    public ResponseEntity<PlaybookVersionResponse> create(
            @RequestBody PlaybookVersionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playbookVersionService.create(request));
    }

    @GetMapping("/{playbookVersionId}")
    public ResponseEntity<PlaybookVersionResponse> getById(
            @PathVariable UUID playbookVersionId
    ) {
        return ResponseEntity.ok(
                playbookVersionService.getById(playbookVersionId)
        );
    }

    @GetMapping("/playbook/{playbookId}")
    public ResponseEntity<List<PlaybookVersionResponse>>
            getByPlaybookId(
                    @PathVariable UUID playbookId
            ) {
        return ResponseEntity.ok(
                playbookVersionService.getByPlaybookId(playbookId)
        );
    }

    @GetMapping(
            "/playbook/{playbookId}/version/{versionNumber}"
    )
    public ResponseEntity<PlaybookVersionResponse>
            getByPlaybookIdAndVersionNumber(
                    @PathVariable UUID playbookId,
                    @PathVariable Integer versionNumber
            ) {
        return ResponseEntity.ok(
                playbookVersionService
                        .getByPlaybookIdAndVersionNumber(
                                playbookId,
                                versionNumber
                        )
        );
    }

    @PutMapping("/{playbookVersionId}")
    public ResponseEntity<PlaybookVersionResponse> update(
            @PathVariable UUID playbookVersionId,
            @RequestBody PlaybookVersionRequest request
    ) {
        return ResponseEntity.ok(
                playbookVersionService.update(
                        playbookVersionId,
                        request
                )
        );
    }
}