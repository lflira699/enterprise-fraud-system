package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;

import java.util.List;
import java.util.UUID;

public interface PlaybookServiceInterface {

    PlaybookResponse create(PlaybookRequest request);

    PlaybookResponse getById(UUID playbookId);

    PlaybookResponse getByCode(String playbookCode);

    List<PlaybookResponse> getAll();

    List<PlaybookResponse> getByStatus(String status);

    PlaybookResponse update(
            UUID playbookId,
            PlaybookRequest request
    );
}