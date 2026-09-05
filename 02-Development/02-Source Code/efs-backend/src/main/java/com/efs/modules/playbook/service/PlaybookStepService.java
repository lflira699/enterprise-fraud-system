package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;
import com.efs.modules.playbook.entity.PlaybookStep;
import com.efs.modules.playbook.mapper.PlaybookStepMapper;
import com.efs.modules.playbook.repository.PlaybookExecutionRepository;
import com.efs.modules.playbook.repository.PlaybookStepRepository;
import com.efs.modules.playbook.repository.PlaybookVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlaybookStepService
        implements PlaybookStepServiceInterface {

    private final PlaybookStepRepository playbookStepRepository;
    private final PlaybookVersionRepository playbookVersionRepository;
    private final PlaybookExecutionRepository playbookExecutionRepository;
    private final PlaybookStepMapper playbookStepMapper;

    public PlaybookStepService(
            PlaybookStepRepository playbookStepRepository,
            PlaybookVersionRepository playbookVersionRepository,
            PlaybookExecutionRepository playbookExecutionRepository,
            PlaybookStepMapper playbookStepMapper
    ) {
        this.playbookStepRepository = playbookStepRepository;
        this.playbookVersionRepository = playbookVersionRepository;
        this.playbookExecutionRepository = playbookExecutionRepository;
        this.playbookStepMapper = playbookStepMapper;
    }

    @Override
    public PlaybookStepResponse create(
            PlaybookStepRequest request
    ) {
        validatePlaybookVersionExists(
                request.getPlaybookVersionId()
        );

        validatePlaybookVersionNotExecuted(
                request.getPlaybookVersionId()
        );

        validateStepOrder(request.getStepOrder());
        validateExpectedDuration(
                request.getExpectedDurationMinutes()
        );

        PlaybookStep entity =
                playbookStepMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return playbookStepMapper.toResponse(
                playbookStepRepository.save(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookStepResponse getById(
            UUID playbookStepId
    ) {
        return playbookStepMapper.toResponse(
                getEntity(playbookStepId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookStepResponse> getByPlaybookVersionId(
            UUID playbookVersionId
    ) {
        validatePlaybookVersionExists(playbookVersionId);

        return playbookStepRepository
                .findByPlaybookVersionIdOrderByStepOrderAsc(
                        playbookVersionId
                )
                .stream()
                .map(playbookStepMapper::toResponse)
                .toList();
    }

    @Override
    public PlaybookStepResponse update(
            UUID playbookStepId,
            PlaybookStepRequest request
    ) {
        PlaybookStep entity = getEntity(playbookStepId);

        UUID currentPlaybookVersionId =
                entity.getPlaybookVersionId();

        validatePlaybookVersionNotExecuted(
                currentPlaybookVersionId
        );

        validatePlaybookVersionExists(
                request.getPlaybookVersionId()
        );

        if (!currentPlaybookVersionId.equals(
                request.getPlaybookVersionId()
        )) {
            validatePlaybookVersionNotExecuted(
                    request.getPlaybookVersionId()
            );
        }

        validateStepOrder(request.getStepOrder());
        validateExpectedDuration(
                request.getExpectedDurationMinutes()
        );

        playbookStepMapper.updateEntity(entity, request);
        entity.setUpdatedAt(LocalDateTime.now());

        return playbookStepMapper.toResponse(
                playbookStepRepository.save(entity)
        );
    }

    private PlaybookStep getEntity(UUID playbookStepId) {
        return playbookStepRepository
                .findById(playbookStepId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook step not found: "
                                        + playbookStepId
                        )
                );
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

    private void validatePlaybookVersionNotExecuted(
            UUID playbookVersionId
    ) {
        if (!playbookExecutionRepository
                .findByPlaybookVersionIdOrderByStartedAtDesc(
                        playbookVersionId
                )
                .isEmpty()) {
            throw new IllegalArgumentException(
                    "Playbook version cannot be modified after execution: "
                            + playbookVersionId
            );
        }
    }

    private void validateStepOrder(Integer stepOrder) {
        if (stepOrder == null || stepOrder <= 0) {
            throw new IllegalArgumentException(
                    "stepOrder must be greater than zero"
            );
        }
    }

    private void validateExpectedDuration(
            Integer expectedDurationMinutes
    ) {
        if (expectedDurationMinutes != null
                && expectedDurationMinutes < 0) {
            throw new IllegalArgumentException(
                    "expectedDurationMinutes cannot be negative"
            );
        }
    }
}