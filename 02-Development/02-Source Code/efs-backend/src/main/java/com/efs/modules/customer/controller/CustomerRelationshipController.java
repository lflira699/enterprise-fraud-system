package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerRelationshipRequest;
import com.efs.modules.customer.dto.CustomerRelationshipResponse;
import com.efs.modules.customer.service.CustomerRelationshipServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerRelationshipController {

    private final CustomerRelationshipServiceInterface customerRelationshipService;

    public CustomerRelationshipController(
            CustomerRelationshipServiceInterface customerRelationshipService) {
        this.customerRelationshipService = customerRelationshipService;
    }

    @PostMapping("/{customerId}/relationships")
    public ResponseEntity<CustomerRelationshipResponse> createRelationship(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerRelationshipRequest request) {

        CustomerRelationshipResponse response =
                customerRelationshipService.createRelationship(
                        customerId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/relationships")
    public ResponseEntity<List<CustomerRelationshipResponse>>
    getRelationshipsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerRelationshipService
                        .getRelationshipsByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/relationships/{relationshipId}")
    public ResponseEntity<CustomerRelationshipResponse> getRelationshipById(
            @PathVariable UUID customerId,
            @PathVariable UUID relationshipId) {

        CustomerRelationshipResponse response =
                customerRelationshipService
                        .getRelationshipById(relationshipId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer relationship not found: " + relationshipId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/relationships/{relationshipId}")
    public ResponseEntity<CustomerRelationshipResponse> updateRelationship(
            @PathVariable UUID customerId,
            @PathVariable UUID relationshipId,
            @Valid @RequestBody CustomerRelationshipRequest request) {

        CustomerRelationshipResponse existing =
                customerRelationshipService
                        .getRelationshipById(relationshipId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer relationship not found: " + relationshipId
            );
        }

        return ResponseEntity.ok(
                customerRelationshipService.updateRelationship(
                        relationshipId,
                        request
                )
        );
    }

    @DeleteMapping("/{customerId}/relationships/{relationshipId}")
    public ResponseEntity<Void> deleteRelationship(
            @PathVariable UUID customerId,
            @PathVariable UUID relationshipId,
            @RequestParam(required = false) UUID deletedBy) {

        CustomerRelationshipResponse existing =
                customerRelationshipService
                        .getRelationshipById(relationshipId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer relationship not found: " + relationshipId
            );
        }

        customerRelationshipService.deleteRelationship(
                relationshipId,
                deletedBy
        );

        return ResponseEntity.noContent().build();
    }
}