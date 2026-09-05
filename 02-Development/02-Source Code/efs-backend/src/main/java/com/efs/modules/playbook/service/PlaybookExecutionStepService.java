package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionStepRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionStepResponse;
import com.efs.modules.playbook.entity.PlaybookExecutionStep;
import com.efs.modules.playbook.mapper.PlaybookExecutionStepMapper;
import com.efs.modules.playbook.repository.PlaybookExecutionRepository;
import com.efs.modules.playbook.repository.PlaybookExecutionStepRepository;
import com.efs.modules.playbook.repository.PlaybookStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlaybookExecutionStepService
        implements PlaybookExecutionStepServiceInterface {

    private final PlaybookExecutionStepRepository
            playbookExecutionStepRepository;

    private final PlaybookExecutionRepository
            playbookExecutionRepository;

    private final PlaybookStepRepository
            playbookStepRepository;

    private final PlaybookExecutionStepMapper
            playbookExecutionStepMapper;

    public PlaybookExecutionStepService(
            PlaybookExecutionStepRepository
                    playbookExecutionStepRepository,
            PlaybookExecutionRepository
                    playbookExecutionRepository,
            PlaybookStepRepository
                    playbookStepRepository,
            PlaybookExecutionStepMapper
                    playbookExecutionStepMapper
    ) {
        this.playbookExecutionStepRepository =
                playbookExecutionStepRepository;
        this.playbookExecutionRepository =
                playbookExecutionRepository;
        this.playbookStepRepository =
                playbookStepRepository;
        this.playbookExecutionStepMapper =
                playbookExecutionStepMapper;
    }

    @Override
    public PlaybookExecutionStepResponse create(
            PlaybookExecutionStepRequest request
    ) {
        validatePlaybookExecutionExists(
                request.getPlaybookExecutionId()
        );

        validatePlaybookStepExists(
                request.getPlaybookStepId()
        );

        validatePlaybookStepBelongsToExecutionVersion(
                request.getPlaybookExecutionId(),
                request.getPlaybookStepId()
        );

        playbookExecutionStepRepository
                .findByPlaybookExecutionIdAndPlaybookStepId(
                        request.getPlaybookExecutionId(),
                        request.getPlaybookStepId()
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Playbook execution step already exists"
                    );
                });

        validateExecutionPeriod(request);

        PlaybookExecutionStep entity =
                playbookExecutionStepMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return playbookExecutionStepMapper.toResponse(
                playbookExecutionStepRepository.save(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookExecutionStepResponse getById(
            UUID playbookExecutionStepId
    ) {
        return playbookExecutionStepMapper.toResponse(
                getEntity(playbookExecutionStepId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookExecutionStepResponse>
            getByPlaybookExecutionId(
                    UUID playbookExecutionId
            ) {

        validatePlaybookExecutionExists(
                playbookExecutionId
        );

        return playbookExecutionStepRepository
                .findByPlaybookExecutionIdOrderByCreatedAtAsc(
                        playbookExecutionId
                )
                .stream()
                .map(playbookExecutionStepMapper::toResponse)
                .toList();
    }

    @Override
    public PlaybookExecutionStepResponse update(
            UUID playbookExecutionStepId,
            PlaybookExecutionStepRequest request
    ) {
        PlaybookExecutionStep entity =
                getEntity(playbookExecutionStepId);

        validatePlaybookExecutionExists(
                request.getPlaybookExecutionId()
        );

        validatePlaybookStepExists(
                request.getPlaybookStepId()
        );

        validatePlaybookStepBelongsToExecutionVersion(
                request.getPlaybookExecutionId(),
                request.getPlaybookStepId()
        );

        validateExecutionPeriod(request);

        playbookExecutionStepRepository
                .findByPlaybookExecutionIdAndPlaybookStepId(
                        request.getPlaybookExecutionId(),
                        request.getPlaybookStepId()
                )
                .filter(existing ->
                        !existing.getPlaybookExecutionStepId()
                                .equals(playbookExecutionStepId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Playbook execution step already exists"
                    );
                });

        playbookExecutionStepMapper.updateEntity(
                entity,
                request
        );

        entity.setUpdatedAt(LocalDateTime.now());

        return playbookExecutionStepMapper.toResponse(
                playbookExecutionStepRepository.save(entity)
        );
    }

    private PlaybookExecutionStep getEntity(
            UUID playbookExecutionStepId
    ) {
        return playbookExecutionStepRepository
                .findById(playbookExecutionStepId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook execution step not found: "
                                        + playbookExecutionStepId
                        )
                );
    }

    private void validatePlaybookStepBelongsToExecutionVersion(
            UUID playbookExecutionId,
            UUID playbookStepId
    ) {
        var execution =
                playbookExecutionRepository
                        .findById(playbookExecutionId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Playbook execution not found: "
                                                + playbookExecutionId
                                )
                        );

        var step =
                playbookStepRepository
                        .findById(playbookStepId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Playbook step not found: "
                                                + playbookStepId
                                )
                        );

        if (!execution.getPlaybookVersionId()
                .equals(step.getPlaybookVersionId())) {

            throw new IllegalArgumentException(
                    "Playbook step does not belong to "
                            + "playbook execution version"
            );
        }
    }

    private void validatePlaybookExecutionExists(
            UUID playbookExecutionId
    ) {
        if (!playbookExecutionRepository.existsById(
                playbookExecutionId
        )) {
            throw new IllegalArgumentException(
                    "Playbook execution not found: "
                            + playbookExecutionId
            );
        }
    }

    private void validatePlaybookStepExists(
            UUID playbookStepId
    ) {
        if (!playbookStepRepository.existsById(
                playbookStepId
        )) {
            throw new IllegalArgumentException(
                    "Playbook step not found: "
                            + playbookStepId
            );
        }
    }

    private void validateExecutionPeriod(
            PlaybookExecutionStepRequest request
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