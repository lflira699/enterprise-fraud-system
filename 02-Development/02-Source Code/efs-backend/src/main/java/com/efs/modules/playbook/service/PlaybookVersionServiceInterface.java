package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;

import java.util.List;
import java.util.UUID;

public interface PlaybookVersionServiceInterface {

    PlaybookVersionResponse create(PlaybookVersionRequest request);

    PlaybookVersionResponse getById(UUID playbookVersionId);

    List<PlaybookVersionResponse> getByPlaybookId(UUID playbookId);

    PlaybookVersionResponse getByPlaybookIdAndVersionNumber(
            UUID playbookId,
            Integer versionNumber
    );

    PlaybookVersionResponse update(
            UUID playbookVersionId,
            PlaybookVersionRequest request
    );
}