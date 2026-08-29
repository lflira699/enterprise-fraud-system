package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CountryRepository
        extends JpaRepository<Country, UUID> {

    Optional<Country> findByCountryCode(
            String countryCode
    );

    Optional<Country> findByAlpha3Code(
            String alpha3Code
    );

    Optional<Country> findByNumericCode(
            String numericCode
    );

    List<Country> findByStatusOrderByCountryNameAsc(
            String status
    );

    List<Country> findAllByOrderByCountryNameAsc();
}