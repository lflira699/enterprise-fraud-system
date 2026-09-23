package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.modules.detection.service.DetectionScenarioAccessServiceInterface;
import com.efs.shared.pagination.PageResponse;
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
@RequestMapping("/api/v1/detection/scenarios")
public class DetectionScenarioController {

    private final DetectionScenarioAccessServiceInterface
            detectionScenarioAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public DetectionScenarioController(
            DetectionScenarioAccessServiceInterface
                    detectionScenarioAccessService,
            SecurityContextProvider securityContextProvider) {

        this.detectionScenarioAccessService =
                detectionScenarioAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<DetectionScenarioResponse>
    createScenario(
            @Valid @RequestBody DetectionScenarioRequest request) {

        DetectionScenarioResponse response =
                detectionScenarioAccessService.createScenario(
                        request,
                        currentContext()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<DetectionScenarioResponse>>
    searchScenarios(
            @RequestParam(required = false)
            String scenarioCode,
            @RequestParam(required = false)
            String category,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            String criticality,
            @RequestParam(required = false)
            String owner,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "25")
            int size,
            @RequestParam(defaultValue = "scenarioName")
            String sort,
            @RequestParam(defaultValue = "ASC")
            String direction) {

        return ResponseEntity.ok(
                detectionScenarioAccessService.searchScenarios(
                        scenarioCode,
                        category,
                        status,
                        criticality,
                        owner,
                        page,
                        size,
                        sort,
                        direction,
                        currentContext()
                )
        );
    }

    @GetMapping("/{scenarioId}")
    public ResponseEntity<DetectionScenarioResponse>
    getScenarioById(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                detectionScenarioAccessService.getScenarioById(
                        scenarioId,
                        currentContext()
                )
        );
    }

    @GetMapping("/code/{scenarioCode}/version/{version}")
    public ResponseEntity<DetectionScenarioResponse>
    getScenarioByCodeAndVersion(
            @PathVariable String scenarioCode,
            @PathVariable Integer version) {

        return ResponseEntity.ok(
                detectionScenarioAccessService
                        .getScenarioByCodeAndVersion(
                                scenarioCode,
                                version,
                                currentContext()
                        )
        );
    }

    @GetMapping("/code/{scenarioCode}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByCode(
            @PathVariable String scenarioCode) {

        return ResponseEntity.ok(
                detectionScenarioAccessService.getScenariosByCode(
                        scenarioCode,
                        currentContext()
                )
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                detectionScenarioAccessService
                        .getScenariosByCategory(
                                category,
                                currentContext()
                        )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                detectionScenarioAccessService
                        .getScenariosByStatus(
                                status,
                                currentContext()
                        )
        );
    }

    @GetMapping("/criticality/{criticality}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByCriticality(
            @PathVariable String criticality) {

        return ResponseEntity.ok(
                detectionScenarioAccessService
                        .getScenariosByCriticality(
                                criticality,
                                currentContext()
                        )
        );
    }

    @GetMapping("/owner/{owner}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByOwner(
            @PathVariable String owner) {

        return ResponseEntity.ok(
                detectionScenarioAccessService
                        .getScenariosByOwner(
                                owner,
                                currentContext()
                        )
        );
    }

    private SecurityContext currentContext() {

        return securityContextProvider
                .getCurrentContext();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}