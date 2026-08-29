package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.LanguageRequest;
import com.efs.modules.catalog.dto.LanguageResponse;
import com.efs.modules.catalog.entity.Language;
import com.efs.modules.catalog.mapper.LanguageMapper;
import com.efs.modules.catalog.repository.LanguageRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LanguageService
        implements LanguageServiceInterface {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    public LanguageService(
            LanguageRepository languageRepository,
            LanguageMapper languageMapper) {

        this.languageRepository =
                languageRepository;

        this.languageMapper =
                languageMapper;
    }

    @Override
    public LanguageResponse createLanguage(
            LanguageRequest request) {

        Language language =
                languageMapper.toEntity(
                        request
                );

        Language savedLanguage =
                languageRepository.save(
                        language
                );

        return languageMapper.toResponse(
                savedLanguage
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LanguageResponse getLanguageById(
            UUID languageId) {

        Language language =
                languageRepository
                        .findById(languageId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Language not found: "
                                                        + languageId
                                        )
                        );

        return languageMapper.toResponse(
                language
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LanguageResponse getLanguageByLanguageCode(
            String languageCode) {

        Language language =
                languageRepository
                        .findByLanguageCode(
                                languageCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Language not found for language code: "
                                                        + languageCode
                                        )
                        );

        return languageMapper.toResponse(
                language
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LanguageResponse getLanguageByAlpha3Code(
            String alpha3Code) {

        Language language =
                languageRepository
                        .findByAlpha3Code(
                                alpha3Code
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Language not found for alpha3 code: "
                                                        + alpha3Code
                                        )
                        );

        return languageMapper.toResponse(
                language
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LanguageResponse> getLanguagesByStatus(
            String status) {

        return languageRepository
                .findByStatusOrderByLanguageNameAsc(
                        status
                )
                .stream()
                .map(languageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LanguageResponse> getAllLanguages() {

        return languageRepository
                .findAllByOrderByLanguageNameAsc()
                .stream()
                .map(languageMapper::toResponse)
                .toList();
    }
}