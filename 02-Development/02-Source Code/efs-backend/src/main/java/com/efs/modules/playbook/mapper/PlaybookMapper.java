package com.efs.modules.playbook.mapper;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.entity.Playbook;
import org.springframework.stereotype.Component;

@Component
public class PlaybookMapper {

    public Playbook toEntity(PlaybookRequest request) {
        Playbook entity = new Playbook();

        entity.setPlaybookCode(request.getPlaybookCode());
        entity.setPlaybookName(request.getPlaybookName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            Playbook entity,
            PlaybookRequest request
    ) {
        entity.setPlaybookCode(request.getPlaybookCode());
        entity.setPlaybookName(request.getPlaybookName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
    }

    public PlaybookResponse toResponse(Playbook entity) {
        PlaybookResponse response = new PlaybookResponse();

        response.setPlaybookId(entity.getPlaybookId());
        response.setPlaybookCode(entity.getPlaybookCode());
        response.setPlaybookName(entity.getPlaybookName());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }
}