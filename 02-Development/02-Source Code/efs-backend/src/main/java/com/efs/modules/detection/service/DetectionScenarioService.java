package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.mapper.DetectionScenarioMapper;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.pagination.PageResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DetectionScenarioService
        implements DetectionScenarioServiceInterface {

    private static final int MAX_PAGE_SIZE = 100;

    private static final String DEFAULT_SCENARIO_SORT =
            "scenarioName";

    private static final String SORT_DIRECTION_ASC =
            "ASC";

    private static final String SORT_DIRECTION_DESC =
            "DESC";

    private final DetectionScenarioRepository detectionScenarioRepository;
    private final DetectionScenarioMapper detectionScenarioMapper;

    public DetectionScenarioService(
            DetectionScenarioRepository detectionScenarioRepository,
            DetectionScenarioMapper detectionScenarioMapper) {

        this.detectionScenarioRepository =
                detectionScenarioRepository;

        this.detectionScenarioMapper =
                detectionScenarioMapper;
    }

    @Override
    @Transactional
    public DetectionScenarioResponse createScenario(
            DetectionScenarioRequest request) {

        DetectionScenario scenario =
                detectionScenarioMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        if (scenario.getVersion() == null) {
            scenario.setVersion(1);
        }

        scenario.setCreatedAt(now);
        scenario.setUpdatedAt(now);

        DetectionScenario savedScenario =
                detectionScenarioRepository.save(
                        scenario
                );

        return detectionScenarioMapper.toResponse(
                savedScenario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DetectionScenarioResponse getScenarioById(
            UUID scenarioId) {

        DetectionScenario scenario =
                detectionScenarioRepository
                        .findByScenarioId(
                                scenarioId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Detection scenario not found: "
                                                + scenarioId
                                )
                        );

        return detectionScenarioMapper.toResponse(
                scenario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DetectionScenarioResponse
    getScenarioByCodeAndVersion(
            String scenarioCode,
            Integer version) {

        DetectionScenario scenario =
                detectionScenarioRepository
                        .findByScenarioCodeAndVersion(
                                scenarioCode,
                                version
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Detection scenario not found: "
                                                + scenarioCode
                                                + " version "
                                                + version
                                )
                        );

        return detectionScenarioMapper.toResponse(
                scenario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByCode(
            String scenarioCode) {

        return detectionScenarioRepository
                .findByScenarioCodeOrderByVersionDesc(
                        scenarioCode
                )
                .stream()
                .map(
                        detectionScenarioMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByCategory(
            String category) {

        return detectionScenarioRepository
                .findByCategoryOrderByScenarioNameAsc(
                        category
                )
                .stream()
                .map(
                        detectionScenarioMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByStatus(
            String status) {

        return detectionScenarioRepository
                .findByStatusOrderByScenarioNameAsc(
                        status
                )
                .stream()
                .map(
                        detectionScenarioMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByCriticality(
            String criticality) {

        return detectionScenarioRepository
                .findByCriticalityOrderByScenarioNameAsc(
                        criticality
                )
                .stream()
                .map(
                        detectionScenarioMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByOwner(
            String owner) {

        return detectionScenarioRepository
                .findByOwnerOrderByScenarioNameAsc(
                        owner
                )
                .stream()
                .map(
                        detectionScenarioMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DetectionScenarioResponse>
    searchScenarios(
            String scenarioCode,
            String category,
            String status,
            String criticality,
            String owner,
            int page,
            int size,
            String sort,
            String direction) {

        validateScenarioSearchRequest(
                page,
                size,
                sort,
                direction
        );

        Sort.Direction sortDirection =
                SORT_DIRECTION_ASC.equals(
                        direction
                )
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sort
                        )
                );

        Specification<DetectionScenario> specification =
                (
                        root,
                        query,
                        criteriaBuilder
                ) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();

                    if (hasText(scenarioCode)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "scenarioCode"
                                        ),
                                        scenarioCode
                                )
                        );
                    }

                    if (hasText(category)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "category"
                                        ),
                                        category
                                )
                        );
                    }

                    if (hasText(status)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "status"
                                        ),
                                        status
                                )
                        );
                    }

                    if (hasText(criticality)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "criticality"
                                        ),
                                        criticality
                                )
                        );
                    }

                    if (hasText(owner)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "owner"
                                        ),
                                        owner
                                )
                        );
                    }

                    return criteriaBuilder.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
                };

        Page<DetectionScenario> scenarioPage =
                detectionScenarioRepository.findAll(
                        specification,
                        pageRequest
                );

        List<DetectionScenarioResponse> content =
                scenarioPage
                        .getContent()
                        .stream()
                        .map(
                                detectionScenarioMapper::toResponse
                        )
                        .toList();

        return new PageResponse<>(
                content,
                scenarioPage.getNumber(),
                scenarioPage.getSize(),
                scenarioPage.getTotalElements(),
                scenarioPage.getTotalPages(),
                scenarioPage.hasNext(),
                scenarioPage.hasPrevious()
        );
    }

    private void validateScenarioSearchRequest(
            int page,
            int size,
            String sort,
            String direction) {

        if (page < 0) {
            throw new RequestValidationException(
                    "page must be greater than or equal to 0"
            );
        }

        if (
                size < 1
                        || size > MAX_PAGE_SIZE
        ) {
            throw new RequestValidationException(
                    "size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }

        if (
                !DEFAULT_SCENARIO_SORT.equals(
                        sort
                )
        ) {
            throw new RequestValidationException(
                    "Unsupported sort field: "
                            + sort
            );
        }

        if (
                !SORT_DIRECTION_ASC.equals(
                        direction
                )
                        && !SORT_DIRECTION_DESC.equals(
                                direction
                        )
        ) {
            throw new RequestValidationException(
                    "Unsupported sort direction: "
                            + direction
            );
        }
    }

    private boolean hasText(
            String value) {

        return value != null
                && !value.isBlank();
    }
}