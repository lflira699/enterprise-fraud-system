package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.TimezoneRequest;
import com.efs.modules.catalog.dto.TimezoneResponse;
import com.efs.modules.catalog.entity.Timezone;
import com.efs.modules.catalog.mapper.TimezoneMapper;
import com.efs.modules.catalog.repository.TimezoneRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TimezoneService
        implements TimezoneServiceInterface {

    private final TimezoneRepository timezoneRepository;
    private final TimezoneMapper timezoneMapper;

    public TimezoneService(
            TimezoneRepository timezoneRepository,
            TimezoneMapper timezoneMapper) {

        this.timezoneRepository =
                timezoneRepository;

        this.timezoneMapper =
                timezoneMapper;
    }

    @Override
    public TimezoneResponse createTimezone(
            TimezoneRequest request) {

        Timezone timezone =
                timezoneMapper.toEntity(
                        request
                );

        Timezone savedTimezone =
                timezoneRepository.save(
                        timezone
                );

        return timezoneMapper.toResponse(
                savedTimezone
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TimezoneResponse getTimezoneById(
            UUID timezoneId) {

        Timezone timezone =
                timezoneRepository
                        .findById(timezoneId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Timezone not found: "
                                                        + timezoneId
                                        )
                        );

        return timezoneMapper.toResponse(
                timezone
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TimezoneResponse getTimezoneByTimezoneCode(
            String timezoneCode) {

        Timezone timezone =
                timezoneRepository
                        .findByTimezoneCode(
                                timezoneCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Timezone not found for timezone code: "
                                                        + timezoneCode
                                        )
                        );

        return timezoneMapper.toResponse(
                timezone
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimezoneResponse> getTimezonesByStatus(
            String status) {

        return timezoneRepository
                .findByStatusOrderByTimezoneNameAsc(
                        status
                )
                .stream()
                .map(timezoneMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimezoneResponse> getAllTimezones() {

        return timezoneRepository
                .findAllByOrderByTimezoneNameAsc()
                .stream()
                .map(timezoneMapper::toResponse)
                .toList();
    }
}