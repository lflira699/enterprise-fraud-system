package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRepository
        extends JpaRepository<Currency, UUID> {

    Optional<Currency> findByCurrencyCode(
            String currencyCode
    );

    Optional<Currency> findByNumericCode(
            String numericCode
    );

    List<Currency> findByStatusOrderByCurrencyNameAsc(
            String status
    );

    List<Currency> findAllByOrderByCurrencyNameAsc();
}