package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.TimezoneRequest;
import com.efs.modules.catalog.dto.TimezoneResponse;

import java.util.List;
import java.util.UUID;

public interface TimezoneServiceInterface {

    TimezoneResponse createTimezone(
            TimezoneRequest request
    );

    TimezoneResponse getTimezoneById(
            UUID timezoneId
    );

    TimezoneResponse getTimezoneByTimezoneCode(
            String timezoneCode
    );

    List<TimezoneResponse> getTimezonesByStatus(
            String status
    );

    List<TimezoneResponse> getAllTimezones();
}