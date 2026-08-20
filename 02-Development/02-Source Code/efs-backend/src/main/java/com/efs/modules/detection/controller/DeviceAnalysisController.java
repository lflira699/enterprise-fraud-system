package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.modules.detection.service.DeviceAnalysisServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/device-analyses")
public class DeviceAnalysisController {

    private final DeviceAnalysisServiceInterface deviceAnalysisService;

    public DeviceAnalysisController(
            DeviceAnalysisServiceInterface deviceAnalysisService) {

        this.deviceAnalysisService = deviceAnalysisService;
    }

    @PostMapping
    public ResponseEntity<DeviceAnalysisResponse>
    createDeviceAnalysis(
            @Valid @RequestBody DeviceAnalysisRequest request) {

        DeviceAnalysisResponse response =
                deviceAnalysisService.createDeviceAnalysis(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{deviceAnalysisId}")
    public ResponseEntity<DeviceAnalysisResponse>
    getDeviceAnalysisById(
            @PathVariable UUID deviceAnalysisId) {

        return ResponseEntity.ok(
                deviceAnalysisService.getDeviceAnalysisById(
                        deviceAnalysisId
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByCustomer(
                        customerId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByCorrelation(
                        correlationId
                )
        );
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByDeviceId(
            @PathVariable String deviceId) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByDeviceId(
                        deviceId
                )
        );
    }

    @GetMapping("/fingerprint/{deviceFingerprint}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByFingerprint(
            @PathVariable String deviceFingerprint) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByFingerprint(
                        deviceFingerprint
                )
        );
    }

    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByIpAddress(
            @PathVariable String ipAddress) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByIpAddress(
                        ipAddress
                )
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<DeviceAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                deviceAnalysisService.getAnalysesByStatus(
                        analysisStatus
                )
        );
    }
}