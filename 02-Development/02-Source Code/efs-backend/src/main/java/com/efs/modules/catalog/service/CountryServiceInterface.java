package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CountryRequest;
import com.efs.modules.catalog.dto.CountryResponse;

import java.util.List;
import java.util.UUID;

public interface CountryServiceInterface {

    CountryResponse createCountry(
            CountryRequest request
    );

    CountryResponse getCountryById(
            UUID countryId
    );

    CountryResponse getCountryByCountryCode(
            String countryCode
    );

    CountryResponse getCountryByAlpha3Code(
            String alpha3Code
    );

    CountryResponse getCountryByNumericCode(
            String numericCode
    );

    List<CountryResponse> getCountriesByStatus(
            String status
    );

    List<CountryResponse> getAllCountries();
}