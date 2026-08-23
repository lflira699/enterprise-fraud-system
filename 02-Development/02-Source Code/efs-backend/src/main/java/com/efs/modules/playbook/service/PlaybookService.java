package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.entity.Playbook;
import com.efs.modules.playbook.mapper.PlaybookMapper;
import com.efs.modules.playbook.repository.PlaybookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlaybookService implements PlaybookServiceInterface {

    private final PlaybookRepository playbookRepository;
    private final PlaybookMapper playbookMapper;

    public PlaybookService(
            PlaybookRepository playbookRepository,
            PlaybookMapper playbookMapper
    ) {
        this.playbookRepository = playbookRepository;
        this.playbookMapper = playbookMapper;
    }

    @Override
    public PlaybookResponse create(PlaybookRequest request) {
        if (playbookRepository
                .findByPlaybookCode(request.getPlaybookCode())
                .isPresent()) {
            throw new IllegalArgumentException(
                    "Playbook code already exists: "
                            + request.getPlaybookCode()
            );
        }

        Playbook entity = playbookMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return playbookMapper.toResponse(
                playbookRepository.save(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookResponse getById(UUID playbookId) {
        return playbookMapper.toResponse(
                getEntity(playbookId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PlaybookResponse getByCode(String playbookCode) {
        Playbook entity = playbookRepository
                .findByPlaybookCode(playbookCode)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook not found: "
                                        + playbookCode
                        )
                );

        return playbookMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookResponse> getAll() {
        return playbookRepository
                .findAll()
                .stream()
                .map(playbookMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaybookResponse> getByStatus(String status) {
        return playbookRepository
                .findByStatusOrderByPlaybookNameAsc(status)
                .stream()
                .map(playbookMapper::toResponse)
                .toList();
    }

    @Override
    public PlaybookResponse update(
            UUID playbookId,
            PlaybookRequest request
    ) {
        Playbook entity = getEntity(playbookId);

        playbookRepository
                .findByPlaybookCode(request.getPlaybookCode())
                .filter(existing ->
                        !existing.getPlaybookId().equals(playbookId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Playbook code already exists: "
                                    + request.getPlaybookCode()
                    );
                });

        playbookMapper.updateEntity(entity, request);
        entity.setUpdatedAt(LocalDateTime.now());

        return playbookMapper.toResponse(
                playbookRepository.save(entity)
        );
    }

    private Playbook getEntity(UUID playbookId) {
        return playbookRepository
                .findById(playbookId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Playbook not found: "
                                        + playbookId
                        )
                );
    }
}