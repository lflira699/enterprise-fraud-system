package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleActivationRequest;
import com.efs.modules.rules.dto.RuleDeactivationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.dto.RuleTestingRequest;
import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.dto.RuleUpdateRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.service.RuleAccessServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleAccessServiceInterface
            ruleAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public RuleController(
            RuleAccessServiceInterface ruleAccessService,
            SecurityContextProvider securityContextProvider) {

        this.ruleAccessService =
                ruleAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<RuleResponse> createRule(
            @Valid @RequestBody RuleRequest request) {

        RuleResponse response =
                ruleAccessService.createRule(
                        request,
                        currentContext()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RuleResponse>> getRules() {

        return ResponseEntity.ok(
                ruleAccessService.getRules(
                        currentContext()
                )
        );
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<RuleResponse> getRuleById(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                ruleAccessService.getRuleById(
                        ruleId,
                        currentContext()
                )
        );
    }

    @GetMapping("/code/{ruleCode}")
    public ResponseEntity<RuleResponse> getRuleByCode(
            @PathVariable String ruleCode) {

        return ResponseEntity.ok(
                ruleAccessService.getRuleByCode(
                        ruleCode,
                        currentContext()
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RuleResponse>> getRulesByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                ruleAccessService.getRulesByStatus(
                        status,
                        currentContext()
                )
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<RuleResponse>> getRulesByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                ruleAccessService.getRulesByCategory(
                        category,
                        currentContext()
                )
        );
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<RuleResponse>> getRulesBySeverity(
            @PathVariable String severity) {

        return ResponseEntity.ok(
                ruleAccessService.getRulesBySeverity(
                        severity,
                        currentContext()
                )
        );
    }

    @PatchMapping("/{ruleId}")
    public ResponseEntity<RuleVersionResponse> updateRule(
            @PathVariable UUID ruleId,
            @Valid @RequestBody RuleUpdateRequest request) {

        return ResponseEntity.ok(
                ruleAccessService.updateRule(
                        ruleId,
                        request,
                        currentContext()
                )
        );
    }

    @PostMapping("/{ruleId}/activate")
    public ResponseEntity<RuleResponse> activateRule(
            @PathVariable UUID ruleId,
            @Valid @RequestBody RuleActivationRequest request) {

        return ResponseEntity.ok(
                ruleAccessService.activateRule(
                        ruleId,
                        request,
                        currentContext()
                )
        );
    }

    @PostMapping("/{ruleId}/deactivate")
    public ResponseEntity<RuleResponse> deactivateRule(
            @PathVariable UUID ruleId,
            @Valid @RequestBody RuleDeactivationRequest request) {

        return ResponseEntity.ok(
                ruleAccessService.deactivateRule(
                        ruleId,
                        request,
                        currentContext()
                )
        );
    }

    @PostMapping(
            "/{ruleId}/versions/{ruleVersionId}/test"
    )
    public ResponseEntity<RuleSimulationResponse> testRule(
            @PathVariable UUID ruleId,
            @PathVariable UUID ruleVersionId,
            @Valid @RequestBody RuleTestingRequest request) {

        return ResponseEntity.ok(
                ruleAccessService.testRule(
                        ruleId,
                        ruleVersionId,
                        request,
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