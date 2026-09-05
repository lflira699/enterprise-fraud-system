package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.entity.PlaybookExecution;
import com.efs.modules.playbook.mapper.PlaybookExecutionMapper;
import com.efs.modules.playbook.repository.PlaybookExecutionRepository;
import com.efs.modules.playbook.repository.PlaybookVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlaybookExecutionService
        implements PlaybookExecutionServiceInterface {

    private final PlaybookExecutionRepository playbookExecutionRepository;
    private final PlaybookVersionRepository playbookVersionRepository;
    private final PlaybookExecutionMapper playbookExecutionMapper;

    public PlaybookExecutionService(
            PlaybookExecutionRepository playbookExecutionRepository,
            PlaybookVersionRepository playbookVersionRepository,
            PlaybookExecutionMapper playbookExecutionMapper
    ) {
        this.playbookExecutionRepository = playbookExecutionRepository;
        this.playbookVersionRepository = playbookVersionRepository;
        this.playbookExecutionMapper = playbookExecutionMapper;
    }

    @Override
    public PlaybookExecutionResponse create(
            PlaybookExecutionRequest request
    ) {
        validatePlaybookVersionExists(
                request.getPlaybookVersionId()
        );

        validateExecutionPeriod(request);

        PlaybookExecution entity =
                playbookExecutionMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        if (entity.getStartedAt() == null) {
            entity.setStartedAt(now);
        }

        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return playbookExecutionMapper.toResponse(
                playbookExecutionRepository.save(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookExecutionResponse getById(
            UUID playbookExecutionId
    ) {
        return playbookExecutionMapper.toResponse(
                getEntity(playbookExecutionId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookExecutionResponse> getByPlaybookVersionId(
            UUID playbookVersionId
    ) {
        validatePlaybookVersionExists(playbookVersionId);

        return playbookExecutionRepository
                .findByPlaybookVersionIdOrderByStartedAtDesc(
                        playbookVersionId
                )
                .stream()
                .map(playbookExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookExecutionResponse> getByAlertId(
            UUID alertId
    ) {
        return playbookExecutionRepository
                .findByAlertIdOrderByStartedAtDesc(alertId)
                .stream()
                .map(playbookExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookExecutionResponse> getByStatus(
            String status
    ) {
        return playbookExecutionRepository
                .findByStatusOrderByStartedAtDesc(status)
                .stream()
                .map(playbookExecutionMapper::toResponse)
                .toList();
    }

    @Override
    public PlaybookExecutionResponse update(
            UUID playbookExecutionId,
            PlaybookExecutionRequest request
    ) {
        PlaybookExecution entity =
                getEntity(playbookExecutionId);

        validatePlaybookExecutionVersionUnchanged(
                entity,
                request.getPlaybookVersionId()
        );

        validatePlaybookVersionExists(
                request.getPlaybookVersionId()
        );

        validateExecutionPeriod(request);

        playbookExecutionMapper.updateEntity(
                entity,
                request
        );

        entity.setUpdatedAt(LocalDateTime.now());

        return playbookExecutionMapper.toResponse(
                playbookExecutionRepository.save(entity)
        );
    }

    private PlaybookExecution getEntity(
            UUID playbookExecutionId
    ) {
        return playbookExecutionRepository
                .findById(playbookExecutionId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook execution not found: "
                                        + playbookExecutionId
                        )
                );
    }

    private void validatePlaybookExecutionVersionUnchanged(
            PlaybookExecution entity,
            UUID requestedPlaybookVersionId
    ) {
        if (!entity.getPlaybookVersionId()
                .equals(requestedPlaybookVersionId)) {
            throw new IllegalArgumentException(
                    "Playbook execution version cannot be changed: "
                            + entity.getPlaybookExecutionId()
            );
        }
    }

    private void validatePlaybookVersionExists(
            UUID playbookVersionId
    ) {
        if (!playbookVersionRepository.existsById(
                playbookVersionId
        )) {
            throw new IllegalArgumentException(
                    "Playbook version not found: "
                            + playbookVersionId
            );
        }
    }

    private void validateExecutionPeriod(
            PlaybookExecutionRequest request
    ) {
        if (request.getStartedAt() != null
                && request.getCompletedAt() != null
                && request.getCompletedAt()
                        .isBefore(request.getStartedAt())) {
            throw new IllegalArgumentException(
                    "completedAt cannot be before startedAt"
            );
        }
    }
}