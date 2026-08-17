package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerWatchlistRequest;
import com.efs.modules.customer.dto.CustomerWatchlistResponse;
import com.efs.modules.customer.service.CustomerWatchlistServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerWatchlistController {

    private final CustomerWatchlistServiceInterface customerWatchlistService;

    public CustomerWatchlistController(
            CustomerWatchlistServiceInterface customerWatchlistService) {
        this.customerWatchlistService = customerWatchlistService;
    }

    @PostMapping("/{customerId}/watchlists")
    public ResponseEntity<CustomerWatchlistResponse> createWatchlist(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerWatchlistRequest request) {

        CustomerWatchlistResponse response =
                customerWatchlistService.createWatchlist(
                        customerId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/watchlists")
    public ResponseEntity<List<CustomerWatchlistResponse>>
    getWatchlistsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerWatchlistService
                        .getWatchlistsByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/watchlists/{watchlistId}")
    public ResponseEntity<CustomerWatchlistResponse> getWatchlistById(
            @PathVariable UUID customerId,
            @PathVariable UUID watchlistId) {

        CustomerWatchlistResponse response =
                customerWatchlistService
                        .getWatchlistById(watchlistId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer watchlist not found: " + watchlistId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/watchlists/{watchlistId}")
    public ResponseEntity<CustomerWatchlistResponse> updateWatchlist(
            @PathVariable UUID customerId,
            @PathVariable UUID watchlistId,
            @Valid @RequestBody CustomerWatchlistRequest request) {

        CustomerWatchlistResponse existing =
                customerWatchlistService
                        .getWatchlistById(watchlistId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer watchlist not found: " + watchlistId
            );
        }

        return ResponseEntity.ok(
                customerWatchlistService.updateWatchlist(
                        watchlistId,
                        request
                )
        );
    }

    @DeleteMapping("/{customerId}/watchlists/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(
            @PathVariable UUID customerId,
            @PathVariable UUID watchlistId) {

        CustomerWatchlistResponse existing =
                customerWatchlistService
                        .getWatchlistById(watchlistId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer watchlist not found: " + watchlistId
            );
        }

        customerWatchlistService.deleteWatchlist(watchlistId);

        return ResponseEntity.noContent().build();
    }
}