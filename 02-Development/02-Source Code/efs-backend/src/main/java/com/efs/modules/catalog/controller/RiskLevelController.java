package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.RiskLevelRequest;
import com.efs.modules.catalog.dto.RiskLevelResponse;
import com.efs.modules.catalog.service.RiskLevelServiceInterface;
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
@RequestMapping("/api/v1/risk-levels")
public class RiskLevelController {

    private final RiskLevelServiceInterface riskLevelService;

    public RiskLevelController(
            RiskLevelServiceInterface riskLevelService) {

        this.riskLevelService =
                riskLevelService;
    }

    @PostMapping
    public ResponseEntity<RiskLevelResponse> createRiskLevel(
            @Valid @RequestBody RiskLevelRequest request) {

        RiskLevelResponse response =
                riskLevelService.createRiskLevel(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{riskLevelId}")
    public ResponseEntity<RiskLevelResponse> getRiskLevelById(
            @PathVariable UUID riskLevelId) {

        return ResponseEntity.ok(
                riskLevelService.getRiskLevelById(
                        riskLevelId
                )
        );
    }

    @GetMapping("/code/{riskCode}")
    public ResponseEntity<RiskLevelResponse> getRiskLevelByRiskCode(
            @PathVariable String riskCode) {

        return ResponseEntity.ok(
                riskLevelService.getRiskLevelByRiskCode(
                        riskCode
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<RiskLevelResponse>> getRiskLevels(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    riskLevelService.getRiskLevelsByStatus(
                            status
                    )
            );
        }

        return ResponseEntity.ok(
                riskLevelService.getAllRiskLevels()
        );
    }
}