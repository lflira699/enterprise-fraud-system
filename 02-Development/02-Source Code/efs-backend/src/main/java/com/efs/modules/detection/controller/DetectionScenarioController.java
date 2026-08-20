package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.modules.detection.service.DetectionScenarioServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenarios")
public class DetectionScenarioController {

    private final DetectionScenarioServiceInterface detectionScenarioService;

    public DetectionScenarioController(
            DetectionScenarioServiceInterface detectionScenarioService) {

        this.detectionScenarioService =
                detectionScenarioService;
    }

    @PostMapping
    public ResponseEntity<DetectionScenarioResponse>
    createScenario(
            @Valid @RequestBody DetectionScenarioRequest request) {

        DetectionScenarioResponse response =
                detectionScenarioService.createScenario(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{scenarioId}")
    public ResponseEntity<DetectionScenarioResponse>
    getScenarioById(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                detectionScenarioService.getScenarioById(
                        scenarioId
                )
        );
    }

    @GetMapping("/code/{scenarioCode}/version/{version}")
    public ResponseEntity<DetectionScenarioResponse>
    getScenarioByCodeAndVersion(
            @PathVariable String scenarioCode,
            @PathVariable Integer version) {

        return ResponseEntity.ok(
                detectionScenarioService
                        .getScenarioByCodeAndVersion(
                                scenarioCode,
                                version
                        )
        );
    }

    @GetMapping("/code/{scenarioCode}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByCode(
            @PathVariable String scenarioCode) {

        return ResponseEntity.ok(
                detectionScenarioService.getScenariosByCode(
                        scenarioCode
                )
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                detectionScenarioService.getScenariosByCategory(
                        category
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                detectionScenarioService.getScenariosByStatus(
                        status
                )
        );
    }

    @GetMapping("/criticality/{criticality}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByCriticality(
            @PathVariable String criticality) {

        return ResponseEntity.ok(
                detectionScenarioService.getScenariosByCriticality(
                        criticality
                )
        );
    }

    @GetMapping("/owner/{owner}")
    public ResponseEntity<List<DetectionScenarioResponse>>
    getScenariosByOwner(
            @PathVariable String owner) {

        return ResponseEntity.ok(
                detectionScenarioService.getScenariosByOwner(
                        owner
                )
        );
    }
}