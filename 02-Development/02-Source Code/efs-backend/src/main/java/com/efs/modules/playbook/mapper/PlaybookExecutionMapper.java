package com.efs.modules.playbook.mapper;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.entity.PlaybookExecution;
import org.springframework.stereotype.Component;

@Component
public class PlaybookExecutionMapper {

    public PlaybookExecution toEntity(PlaybookExecutionRequest request) {
        PlaybookExecution entity = new PlaybookExecution();

        entity.setPlaybookVersionId(request.getPlaybookVersionId());
        entity.setAlertId(request.getAlertId());
        entity.setScenarioId(request.getScenarioId());
        entity.setStatus(request.getStatus());
        entity.setStartedAt(request.getStartedAt());
        entity.setCompletedAt(request.getCompletedAt());

        return entity;
    }

    public void updateEntity(
            PlaybookExecution entity,
            PlaybookExecutionRequest request
    ) {
        entity.setPlaybookVersionId(request.getPlaybookVersionId());
        entity.setAlertId(request.getAlertId());
        entity.setScenarioId(request.getScenarioId());
        entity.setStatus(request.getStatus());
        entity.setStartedAt(request.getStartedAt());
        entity.setCompletedAt(request.getCompletedAt());
    }

    public PlaybookExecutionResponse toResponse(PlaybookExecution entity) {
        PlaybookExecutionResponse response = new PlaybookExecutionResponse();

        response.setPlaybookExecutionId(entity.getPlaybookExecutionId());
        response.setPlaybookVersionId(entity.getPlaybookVersionId());
        response.setAlertId(entity.getAlertId());
        response.setScenarioId(entity.getScenarioId());
        response.setStatus(entity.getStatus());
        response.setStartedAt(entity.getStartedAt());
        response.setCompletedAt(entity.getCompletedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }
}