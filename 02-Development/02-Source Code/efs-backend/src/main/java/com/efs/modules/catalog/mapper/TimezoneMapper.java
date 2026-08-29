package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.TimezoneRequest;
import com.efs.modules.catalog.dto.TimezoneResponse;
import com.efs.modules.catalog.entity.Timezone;
import org.springframework.stereotype.Component;

@Component
public class TimezoneMapper {

    public Timezone toEntity(
            TimezoneRequest request) {

        Timezone timezone =
                new Timezone();

        timezone.setTimezoneCode(
                request.getTimezoneCode()
        );

        timezone.setTimezoneName(
                request.getTimezoneName()
        );

        timezone.setStatus(
                request.getStatus()
        );

        return timezone;
    }

    public TimezoneResponse toResponse(
            Timezone timezone) {

        TimezoneResponse response =
                new TimezoneResponse();

        response.setTimezoneId(
                timezone.getTimezoneId()
        );

        response.setTimezoneCode(
                timezone.getTimezoneCode()
        );

        response.setTimezoneName(
                timezone.getTimezoneName()
        );

        response.setStatus(
                timezone.getStatus()
        );

        response.setCreatedAt(
                timezone.getCreatedAt()
        );

        return response;
    }
}