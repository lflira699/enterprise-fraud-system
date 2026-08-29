package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LanguageRepository
        extends JpaRepository<Language, UUID> {

    Optional<Language> findByLanguageCode(
            String languageCode
    );

    Optional<Language> findByAlpha3Code(
            String alpha3Code
    );

    List<Language> findByStatusOrderByLanguageNameAsc(
            String status
    );

    List<Language> findAllByOrderByLanguageNameAsc();
}