package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.CountryRequest;
import com.efs.modules.catalog.dto.CountryResponse;
import com.efs.modules.catalog.entity.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

    public Country toEntity(
            CountryRequest request) {

        Country country =
                new Country();

        country.setCountryCode(
                request.getCountryCode()
        );

        country.setAlpha3Code(
                request.getAlpha3Code()
        );

        country.setNumericCode(
                request.getNumericCode()
        );

        country.setCountryName(
                request.getCountryName()
        );

        country.setStatus(
                request.getStatus()
        );

        return country;
    }

    public CountryResponse toResponse(
            Country country) {

        CountryResponse response =
                new CountryResponse();

        response.setCountryId(
                country.getCountryId()
        );

        response.setCountryCode(
                country.getCountryCode()
        );

        response.setAlpha3Code(
                country.getAlpha3Code()
        );

        response.setNumericCode(
                country.getNumericCode()
        );

        response.setCountryName(
                country.getCountryName()
        );

        response.setStatus(
                country.getStatus()
        );

        response.setCreatedAt(
                country.getCreatedAt()
        );

        return response;
    }
}