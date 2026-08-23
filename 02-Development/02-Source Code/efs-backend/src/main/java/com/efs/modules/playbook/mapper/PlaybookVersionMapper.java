package com.efs.modules.playbook.mapper;

import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.entity.PlaybookVersion;
import org.springframework.stereotype.Component;

@Component
public class PlaybookVersionMapper {

    public PlaybookVersion toEntity(PlaybookVersionRequest request) {
        PlaybookVersion entity = new PlaybookVersion();

        entity.setPlaybookId(request.getPlaybookId());
        entity.setVersionNumber(request.getVersionNumber());
        entity.setStatus(request.getStatus());
        entity.setEffectiveFrom(request.getEffectiveFrom());
        entity.setEffectiveTo(request.getEffectiveTo());

        return entity;
    }

    public void updateEntity(
            PlaybookVersion entity,
            PlaybookVersionRequest request
    ) {
        entity.setPlaybookId(request.getPlaybookId());
        entity.setVersionNumber(request.getVersionNumber());
        entity.setStatus(request.getStatus());
        entity.setEffectiveFrom(request.getEffectiveFrom());
        entity.setEffectiveTo(request.getEffectiveTo());
    }

    public PlaybookVersionResponse toResponse(PlaybookVersion entity) {
        PlaybookVersionResponse response = new PlaybookVersionResponse();

        response.setPlaybookVersionId(entity.getPlaybookVersionId());
        response.setPlaybookId(entity.getPlaybookId());
        response.setVersionNumber(entity.getVersionNumber());
        response.setStatus(entity.getStatus());
        response.setEffectiveFrom(entity.getEffectiveFrom());
        response.setEffectiveTo(entity.getEffectiveTo());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }
}