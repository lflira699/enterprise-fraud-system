package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.CurrencyRequest;
import com.efs.modules.catalog.dto.CurrencyResponse;
import com.efs.modules.catalog.entity.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public Currency toEntity(
            CurrencyRequest request) {

        Currency currency =
                new Currency();

        currency.setCurrencyCode(
                request.getCurrencyCode()
        );

        currency.setNumericCode(
                request.getNumericCode()
        );

        currency.setCurrencyName(
                request.getCurrencyName()
        );

        currency.setMinorUnit(
                request.getMinorUnit()
        );

        currency.setStatus(
                request.getStatus()
        );

        return currency;
    }

    public CurrencyResponse toResponse(
            Currency currency) {

        CurrencyResponse response =
                new CurrencyResponse();

        response.setCurrencyId(
                currency.getCurrencyId()
        );

        response.setCurrencyCode(
                currency.getCurrencyCode()
        );

        response.setNumericCode(
                currency.getNumericCode()
        );

        response.setCurrencyName(
                currency.getCurrencyName()
        );

        response.setMinorUnit(
                currency.getMinorUnit()
        );

        response.setStatus(
                currency.getStatus()
        );

        response.setCreatedAt(
                currency.getCreatedAt()
        );

        return response;
    }
}