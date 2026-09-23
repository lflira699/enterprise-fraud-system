package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.modules.detection.service.DeviceAnalysisAccessServiceInterface;
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
@RequestMapping("/api/v1/detection/device-analyses")
public class DeviceAnalysisController {

    private final DeviceAnalysisAccessServiceInterface
            deviceAnalysisAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public DeviceAnalysisController(
            DeviceAnalysisAccessServiceInterface deviceAnalysisAccessService,
            SecurityContextProvider securityContextProvider) {

        this.deviceAnalysisAccessService =
                deviceAnalysisAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<DeviceAnalysisResponse>
    createDeviceAnalysis(
            @Valid @RequestBody DeviceAnalysisRequest request) {

        DeviceAnalysisResponse response =
                deviceAnalysisAccessService
                        .createDeviceAnalysis(
                                request,
                                currentContext()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{deviceAnalysisId}")
    public ResponseEntity<DeviceAnalysisResponse>
    getDeviceAnalysisById(
            @PathVariable UUID deviceAnalysisId) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getDeviceAnalysisById(
                                deviceAnalysisId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByCustomer(
                                customerId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByTransaction(
                                transactionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByCorrelation(
                                correlationId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByDeviceId(
            @PathVariable String deviceId) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByDeviceId(
                                deviceId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/fingerprint/{deviceFingerprint}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByFingerprint(
            @PathVariable String deviceFingerprint) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByFingerprint(
                                deviceFingerprint,
                                currentContext()
                        )
        );
    }

    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByIpAddress(
            @PathVariable String ipAddress) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByIpAddress(
                                ipAddress,
                                currentContext()
                        )
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                deviceAnalysisAccessService
                        .getAnalysesByStatus(
                                analysisStatus,
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