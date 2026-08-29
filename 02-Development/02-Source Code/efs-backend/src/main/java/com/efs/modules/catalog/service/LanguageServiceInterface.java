package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.LanguageRequest;
import com.efs.modules.catalog.dto.LanguageResponse;

import java.util.List;
import java.util.UUID;

public interface LanguageServiceInterface {

    LanguageResponse createLanguage(
            LanguageRequest request
    );

    LanguageResponse getLanguageById(
            UUID languageId
    );

    LanguageResponse getLanguageByLanguageCode(
            String languageCode
    );

    LanguageResponse getLanguageByAlpha3Code(
            String alpha3Code
    );

    List<LanguageResponse> getLanguagesByStatus(
            String status
    );

    List<LanguageResponse> getAllLanguages();
}