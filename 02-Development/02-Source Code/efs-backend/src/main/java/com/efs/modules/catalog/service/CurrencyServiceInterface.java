package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CurrencyRequest;
import com.efs.modules.catalog.dto.CurrencyResponse;

import java.util.List;
import java.util.UUID;

public interface CurrencyServiceInterface {

    CurrencyResponse createCurrency(
            CurrencyRequest request
    );

    CurrencyResponse getCurrencyById(
            UUID currencyId
    );

    CurrencyResponse getCurrencyByCurrencyCode(
            String currencyCode
    );

    CurrencyResponse getCurrencyByNumericCode(
            String numericCode
    );

    List<CurrencyResponse> getCurrenciesByStatus(
            String status
    );

    List<CurrencyResponse> getAllCurrencies();
}