package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;

import java.util.List;
import java.util.UUID;

public interface PlaybookExecutionServiceInterface {

    PlaybookExecutionResponse create(
            PlaybookExecutionRequest request
    );

    PlaybookExecutionResponse getById(
            UUID playbookExecutionId
    );

    List<PlaybookExecutionResponse> getByPlaybookVersionId(
            UUID playbookVersionId
    );

    List<PlaybookExecutionResponse> getByAlertId(
            UUID alertId
    );

    List<PlaybookExecutionResponse> getByStatus(
            String status
    );

    PlaybookExecutionResponse update(
            UUID playbookExecutionId,
            PlaybookExecutionRequest request
    );
}