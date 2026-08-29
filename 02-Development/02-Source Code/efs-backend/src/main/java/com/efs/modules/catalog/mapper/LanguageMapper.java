package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.LanguageRequest;
import com.efs.modules.catalog.dto.LanguageResponse;
import com.efs.modules.catalog.entity.Language;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper {

    public Language toEntity(
            LanguageRequest request) {

        Language language =
                new Language();

        language.setLanguageCode(
                request.getLanguageCode()
        );

        language.setAlpha3Code(
                request.getAlpha3Code()
        );

        language.setLanguageName(
                request.getLanguageName()
        );

        language.setStatus(
                request.getStatus()
        );

        return language;
    }

    public LanguageResponse toResponse(
            Language language) {

        LanguageResponse response =
                new LanguageResponse();

        response.setLanguageId(
                language.getLanguageId()
        );

        response.setLanguageCode(
                language.getLanguageCode()
        );

        response.setAlpha3Code(
                language.getAlpha3Code()
        );

        response.setLanguageName(
                language.getLanguageName()
        );

        response.setStatus(
                language.getStatus()
        );

        response.setCreatedAt(
                language.getCreatedAt()
        );

        return response;
    }
}