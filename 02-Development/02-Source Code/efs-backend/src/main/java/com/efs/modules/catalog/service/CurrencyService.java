package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CurrencyRequest;
import com.efs.modules.catalog.dto.CurrencyResponse;
import com.efs.modules.catalog.entity.Currency;
import com.efs.modules.catalog.mapper.CurrencyMapper;
import com.efs.modules.catalog.repository.CurrencyRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CurrencyService
        implements CurrencyServiceInterface {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public CurrencyService(
            CurrencyRepository currencyRepository,
            CurrencyMapper currencyMapper) {

        this.currencyRepository =
                currencyRepository;

        this.currencyMapper =
                currencyMapper;
    }

    @Override
    public CurrencyResponse createCurrency(
            CurrencyRequest request) {

        Currency currency =
                currencyMapper.toEntity(
                        request
                );

        Currency savedCurrency =
                currencyRepository.save(
                        currency
                );

        return currencyMapper.toResponse(
                savedCurrency
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyById(
            UUID currencyId) {

        Currency currency =
                currencyRepository
                        .findById(currencyId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Currency not found: "
                                                        + currencyId
                                        )
                        );

        return currencyMapper.toResponse(
                currency
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyByCurrencyCode(
            String currencyCode) {

        Currency currency =
                currencyRepository
                        .findByCurrencyCode(
                                currencyCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Currency not found for currency code: "
                                                        + currencyCode
                                        )
                        );

        return currencyMapper.toResponse(
                currency
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyByNumericCode(
            String numericCode) {

        Currency currency =
                currencyRepository
                        .findByNumericCode(
                                numericCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Currency not found for numeric code: "
                                                        + numericCode
                                        )
                        );

        return currencyMapper.toResponse(
                currency
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyResponse> getCurrenciesByStatus(
            String status) {

        return currencyRepository
                .findByStatusOrderByCurrencyNameAsc(
                        status
                )
                .stream()
                .map(currencyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyResponse> getAllCurrencies() {

        return currencyRepository
                .findAllByOrderByCurrencyNameAsc()
                .stream()
                .map(currencyMapper::toResponse)
                .toList();
    }
}