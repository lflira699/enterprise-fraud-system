package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.CountryRequest;
import com.efs.modules.catalog.dto.CountryResponse;
import com.efs.modules.catalog.service.CountryServiceInterface;
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
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryServiceInterface countryService;

    public CountryController(
            CountryServiceInterface countryService) {

        this.countryService =
                countryService;
    }

    @PostMapping
    public ResponseEntity<CountryResponse> createCountry(
            @Valid @RequestBody CountryRequest request) {

        CountryResponse response =
                countryService.createCountry(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{countryId}")
    public ResponseEntity<CountryResponse> getCountryById(
            @PathVariable UUID countryId) {

        return ResponseEntity.ok(
                countryService.getCountryById(
                        countryId
                )
        );
    }

    @GetMapping("/code/{countryCode}")
    public ResponseEntity<CountryResponse> getCountryByCountryCode(
            @PathVariable String countryCode) {

        return ResponseEntity.ok(
                countryService.getCountryByCountryCode(
                        countryCode
                )
        );
    }

    @GetMapping("/alpha3/{alpha3Code}")
    public ResponseEntity<CountryResponse> getCountryByAlpha3Code(
            @PathVariable String alpha3Code) {

        return ResponseEntity.ok(
                countryService.getCountryByAlpha3Code(
                        alpha3Code
                )
        );
    }

    @GetMapping("/numeric/{numericCode}")
    public ResponseEntity<CountryResponse> getCountryByNumericCode(
            @PathVariable String numericCode) {

        return ResponseEntity.ok(
                countryService.getCountryByNumericCode(
                        numericCode
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<CountryResponse>> getCountries(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    countryService.getCountriesByStatus(
                            status
                    )
            );
        }

        return ResponseEntity.ok(
                countryService.getAllCountries()
        );
    }
}