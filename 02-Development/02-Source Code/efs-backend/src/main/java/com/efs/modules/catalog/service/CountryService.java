package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CountryRequest;
import com.efs.modules.catalog.dto.CountryResponse;
import com.efs.modules.catalog.entity.Country;
import com.efs.modules.catalog.mapper.CountryMapper;
import com.efs.modules.catalog.repository.CountryRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CountryService
        implements CountryServiceInterface {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryService(
            CountryRepository countryRepository,
            CountryMapper countryMapper) {

        this.countryRepository =
                countryRepository;

        this.countryMapper =
                countryMapper;
    }

    @Override
    public CountryResponse createCountry(
            CountryRequest request) {

        Country country =
                countryMapper.toEntity(
                        request
                );

        Country savedCountry =
                countryRepository.save(
                        country
                );

        return countryMapper.toResponse(
                savedCountry
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponse getCountryById(
            UUID countryId) {

        Country country =
                countryRepository.findById(
                                countryId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Country not found: "
                                                        + countryId
                                        )
                        );

        return countryMapper.toResponse(
                country
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponse getCountryByCountryCode(
            String countryCode) {

        Country country =
                countryRepository.findByCountryCode(
                                countryCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Country not found: "
                                                        + countryCode
                                        )
                        );

        return countryMapper.toResponse(
                country
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponse getCountryByAlpha3Code(
            String alpha3Code) {

        Country country =
                countryRepository.findByAlpha3Code(
                                alpha3Code
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Country not found: "
                                                        + alpha3Code
                                        )
                        );

        return countryMapper.toResponse(
                country
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponse getCountryByNumericCode(
            String numericCode) {

        Country country =
                countryRepository.findByNumericCode(
                                numericCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Country not found: "
                                                        + numericCode
                                        )
                        );

        return countryMapper.toResponse(
                country
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryResponse> getCountriesByStatus(
            String status) {

        return countryRepository
                .findByStatusOrderByCountryNameAsc(
                        status
                )
                .stream()
                .map(
                        countryMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryResponse> getAllCountries() {

        return countryRepository
                .findAllByOrderByCountryNameAsc()
                .stream()
                .map(
                        countryMapper::toResponse
                )
                .toList();
    }
}