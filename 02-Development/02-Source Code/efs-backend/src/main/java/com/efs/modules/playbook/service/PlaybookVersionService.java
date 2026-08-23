package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.entity.PlaybookVersion;
import com.efs.modules.playbook.mapper.PlaybookVersionMapper;
import com.efs.modules.playbook.repository.PlaybookRepository;
import com.efs.modules.playbook.repository.PlaybookVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlaybookVersionService
        implements PlaybookVersionServiceInterface {

    private final PlaybookVersionRepository playbookVersionRepository;
    private final PlaybookRepository playbookRepository;
    private final PlaybookVersionMapper playbookVersionMapper;

    public PlaybookVersionService(
            PlaybookVersionRepository playbookVersionRepository,
            PlaybookRepository playbookRepository,
            PlaybookVersionMapper playbookVersionMapper
    ) {
        this.playbookVersionRepository = playbookVersionRepository;
        this.playbookRepository = playbookRepository;
        this.playbookVersionMapper = playbookVersionMapper;
    }

    @Override
    public PlaybookVersionResponse create(
            PlaybookVersionRequest request
    ) {
        validatePlaybookExists(request.getPlaybookId());

        playbookVersionRepository
                .findByPlaybookIdAndVersionNumber(
                        request.getPlaybookId(),
                        request.getVersionNumber()
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Playbook version already exists for playbook "
                                    + request.getPlaybookId()
                                    + ": "
                                    + request.getVersionNumber()
                    );
                });

        validateEffectivePeriod(request);

        PlaybookVersion entity =
                playbookVersionMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return playbookVersionMapper.toResponse(
                playbookVersionRepository.save(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookVersionResponse getById(
            UUID playbookVersionId
    ) {
        return playbookVersionMapper.toResponse(
                getEntity(playbookVersionId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookVersionResponse> getByPlaybookId(
            UUID playbookId
    ) {
        validatePlaybookExists(playbookId);

        return playbookVersionRepository
                .findByPlaybookIdOrderByVersionNumberDesc(playbookId)
                .stream()
                .map(playbookVersionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookVersionResponse getByPlaybookIdAndVersionNumber(
            UUID playbookId,
            Integer versionNumber
    ) {
        PlaybookVersion entity = playbookVersionRepository
                .findByPlaybookIdAndVersionNumber(
                        playbookId,
                        versionNumber
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook version not found for playbook "
                                        + playbookId
                                        + ": "
                                        + versionNumber
                        )
                );

        return playbookVersionMapper.toResponse(entity);
    }

    @Override
    public PlaybookVersionResponse update(
            UUID playbookVersionId,
            PlaybookVersionRequest request
    ) {
        PlaybookVersion entity = getEntity(playbookVersionId);

        validatePlaybookExists(request.getPlaybookId());
        validateEffectivePeriod(request);

        playbookVersionRepository
                .findByPlaybookIdAndVersionNumber(
                        request.getPlaybookId(),
                        request.getVersionNumber()
                )
                .filter(existing ->
                        !existing.getPlaybookVersionId()
                                .equals(playbookVersionId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Playbook version already exists for playbook "
                                    + request.getPlaybookId()
                                    + ": "
                                    + request.getVersionNumber()
                    );
                });

        playbookVersionMapper.updateEntity(entity, request);
        entity.setUpdatedAt(LocalDateTime.now());

        return playbookVersionMapper.toResponse(
                playbookVersionRepository.save(entity)
        );
    }

    private PlaybookVersion getEntity(UUID playbookVersionId) {
        return playbookVersionRepository
                .findById(playbookVersionId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook version not found: "
                                        + playbookVersionId
                        )
                );
    }

    private void validatePlaybookExists(UUID playbookId) {
        if (!playbookRepository.existsById(playbookId)) {
            throw new IllegalArgumentException(
                    "Playbook not found: " + playbookId
            );
        }
    }

    private void validateEffectivePeriod(
            PlaybookVersionRequest request
    ) {
        if (request.getEffectiveFrom() != null
                && request.getEffectiveTo() != null
                && request.getEffectiveTo()
                        .isBefore(request.getEffectiveFrom())) {
            throw new IllegalArgumentException(
                    "effectiveTo cannot be before effectiveFrom"
            );
        }
    }
}