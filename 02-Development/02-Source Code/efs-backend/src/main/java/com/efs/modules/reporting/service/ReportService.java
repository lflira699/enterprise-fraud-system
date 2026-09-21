package com.efs.modules.reporting.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.dto.DashboardComponent;
import com.efs.modules.dashboard.dto.DashboardFilterCriteria;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.modules.reporting.dto.ReportCriteriaRequest;
import com.efs.modules.reporting.dto.ReportDefinitionResponse;
import com.efs.modules.reporting.dto.ReportGenerationRequest;
import com.efs.modules.reporting.entity.GeneratedReport;
import com.efs.modules.reporting.repository.GeneratedReportRepository;
import com.efs.shared.exception.DashboardDataUnavailableException;
import com.efs.shared.exception.InvalidDashboardFilterException;
import com.efs.shared.exception.ReportException;
import com.efs.shared.exception.ValidationException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class ReportService
        implements ReportServiceInterface {

    private static final String
            REPORT_GENERATE_PERMISSION =
            "report.generate";

    private static final String
            REPORT_VIEW_PERMISSION =
            "report.view";

    private static final String
            REPORT_GENERATION_EVENT =
            "REPORT_GENERATION";

    private static final String
            REPORT_ENTITY_TYPE =
            "REPORT";

    private static final String
            REPORT_ACTION =
            "GENERATE";

    private static final String
            REPORT_SOURCE_COMPONENT =
            "REPORTING";

    private static final String
            MAX_RECORDS_CONFIGURATION =
            "EFS.REPORT.MAX_RECORDS";

    private static final int
            DEFAULT_MAX_RECORDS =
            100;

    private static final int
            CASE_PAGE_SIZE =
            100;

    private static final String
            CASE_SORT =
            "createdAt";

    private static final String
            CASE_DIRECTION =
            "DESC";

    private static final TypeReference<
            Map<String, Object>>
            MAP_TYPE =
            new TypeReference<>() {
            };

    private static final Definition
            OPERATIONAL_SUMMARY =
            new Definition(
                    "OPERATIONAL_SUMMARY",
                    "OPERATIONAL",
                    "dashboard.view",
                    List.of(
                            "tenantId",
                            "components"
                    )
            );

    private static final Definition
            INVESTIGATION_CASES =
            new Definition(
                    "INVESTIGATION_CASES",
                    "OPERATIONAL",
                    "case.view",
                    List.of(
                            "status",
                            "priority",
                            "assignedUser",
                            "assignedTeam"
                    )
            );

    private static final List<Definition>
            DEFINITIONS =
            List.of(
                    OPERATIONAL_SUMMARY,
                    INVESTIGATION_CASES
            );

    private final GeneratedReportRepository
            generatedReportRepository;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final SystemConfigurationServiceInterface
            systemConfigurationService;

    private final DashboardServiceInterface
            dashboardService;

    private final CaseServiceInterface
            caseService;

    private final AuditEventServiceInterface
            auditEventService;

    private final ObjectMapper
            objectMapper;

    public ReportService(
            GeneratedReportRepository generatedReportRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            SystemConfigurationServiceInterface systemConfigurationService,
            DashboardServiceInterface dashboardService,
            CaseServiceInterface caseService,
            AuditEventServiceInterface auditEventService,
            ObjectMapper objectMapper) {

        this.generatedReportRepository =
                generatedReportRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.systemConfigurationService =
                systemConfigurationService;

        this.dashboardService =
                dashboardService;

        this.caseService =
                caseService;

        this.auditEventService =
                auditEventService;

        this.objectMapper =
                objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDefinitionResponse>
    getAvailableDefinitions(
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requirePermission(
                securityContext,
                REPORT_GENERATE_PERMISSION
        );

        resolveAuthorizedUser(
                securityContext
        );

        return DEFINITIONS.stream()
                .filter(
                        definition ->
                                securityContext
                                        .hasPermission(
                                                definition
                                                        .sourcePermission()
                                        )
                )
                .map(
                        definition ->
                                new ReportDefinitionResponse(
                                        definition.code(),
                                        definition.category(),
                                        definition.allowedCriteria()
                                )
                )
                .toList();
    }

    @Override
    @Transactional
    public GeneratedReportResponse generateReport(
            ReportGenerationRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        if (!securityContext.hasPermission(
                REPORT_GENERATE_PERMISSION
        )) {

            recordRejected(
                    securityContext,
                    null,
                    securityContext.getTenantId(),
                    safeReportCode(request),
                    safeCriteria(request),
                    "MISSING_PERMISSION"
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + REPORT_GENERATE_PERMISSION
            );
        }

        UserAccountReference authorizedUser;

        try {
            authorizedUser =
                    resolveAuthorizedUser(
                            securityContext
                    );
        }
        catch (RuntimeException exception) {

            recordFailure(
                    securityContext,
                    null,
                    securityContext.getTenantId(),
                    safeReportCode(request),
                    safeCriteria(request),
                    "AUTHORIZED_SCOPE_RESOLUTION_FAILED",
                    exception
            );

            throw exception;
        }

        UUID organizationId =
                authorizedUser.organizationId();

        UUID authoritativeTenantId =
                authorizedUser.tenantId();

        try {

            Definition definition =
                    resolveDefinition(
                            request
                    );

            requireSourcePermission(
                    securityContext,
                    definition
            );

            ReportCriteriaRequest criteria =
                    normalizeCriteria(
                            request
                    );

            validateCriteria(
                    definition,
                    criteria
            );

            ReportMaterial material =
                    generateMaterial(
                            definition,
                            criteria,
                            authorizedUser,
                            securityContext
                    );

            LocalDateTime generatedAt =
                    LocalDateTime.now();

            GeneratedReport generatedReport =
                    new GeneratedReport(
                            organizationId,
                            material.tenantId(),
                            definition.code(),
                            material.criteria(),
                            material.content(),
                            securityContext.getUserId(),
                            generatedAt
                    );

            GeneratedReport saved =
                    generatedReportRepository
                            .saveAndFlush(
                                    generatedReport
                            );

            recordSuccess(
                    securityContext,
                    organizationId,
                    material.tenantId(),
                    saved.getReportId(),
                    definition.code(),
                    material.criteria(),
                    generatedAt
            );

            return toResponse(
                    saved
            );

        }
        catch (AccessDeniedException exception) {

            recordRejected(
                    securityContext,
                    organizationId,
                    authoritativeTenantId,
                    safeReportCode(request),
                    safeCriteria(request),
                    "MISSING_SOURCE_PERMISSION"
            );

            throw exception;
        }
        catch (ReportException exception) {

            if (
                    exception.getStatus()
                            .is5xxServerError()
            ) {
                recordFailure(
                        securityContext,
                        organizationId,
                        authoritativeTenantId,
                        safeReportCode(request),
                        safeCriteria(request),
                        exception.getErrorCode(),
                        exception
                );
            }
            else {
                recordRejected(
                        securityContext,
                        organizationId,
                        authoritativeTenantId,
                        safeReportCode(request),
                        safeCriteria(request),
                        exception.getErrorCode()
                );
            }

            throw exception;
        }
        catch (RuntimeException exception) {

            ReportException controlled =
                    new ReportException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "REPORT_GENERATION_FAILED",
                            "Report generation failed"
                    );

            recordFailure(
                    securityContext,
                    organizationId,
                    authoritativeTenantId,
                    safeReportCode(request),
                    safeCriteria(request),
                    controlled.getErrorCode(),
                    exception
            );

            throw controlled;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GeneratedReportResponse getReport(
            UUID reportId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                reportId,
                "reportId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requirePermission(
                securityContext,
                REPORT_VIEW_PERMISSION
        );

        UserAccountReference authorizedUser =
                resolveAuthorizedUser(
                        securityContext
                );

        GeneratedReport report;

        if (authorizedUser.tenantId() == null) {

            report =
                    generatedReportRepository
                            .findByReportIdAndOrganizationId(
                                    reportId,
                                    authorizedUser.organizationId()
                            )
                            .orElseThrow(
                                    () ->
                                            new ReportException(
                                                    HttpStatus.NOT_FOUND,
                                                    "REPORT_NOT_FOUND",
                                                    "Generated report was not found"
                                            )
                            );
        }
        else {

            report =
                    generatedReportRepository
                            .findByReportIdAndOrganizationIdAndTenantId(
                                    reportId,
                                    authorizedUser.organizationId(),
                                    authorizedUser.tenantId()
                            )
                            .orElseThrow(
                                    () ->
                                            new ReportException(
                                                    HttpStatus.NOT_FOUND,
                                                    "REPORT_NOT_FOUND",
                                                    "Generated report was not found"
                                            )
                            );
        }

        requireSourcePermissionForPersistedReport(
                securityContext,
                report
        );

        return toResponse(
                report
        );
    }

    private ReportMaterial generateMaterial(
            Definition definition,
            ReportCriteriaRequest criteria,
            UserAccountReference authorizedUser,
            SecurityContext securityContext) {

        if (
                definition.code().equals(
                        OPERATIONAL_SUMMARY.code()
                )
        ) {
            return generateOperationalSummary(
                    criteria,
                    securityContext
            );
        }

        if (
                definition.code().equals(
                        INVESTIGATION_CASES.code()
                )
        ) {
            return generateInvestigationCases(
                    criteria,
                    authorizedUser,
                    securityContext
            );
        }

        throw new ReportException(
                HttpStatus.BAD_REQUEST,
                "INVALID_REPORT_REQUEST",
                "Unsupported report definition"
        );
    }

    private ReportMaterial generateOperationalSummary(
            ReportCriteriaRequest criteria,
            SecurityContext securityContext) {

        List<String> components =
                normalizeComponents(
                        criteria.getComponents()
                );

        String componentFilter =
                components == null
                        ? null
                        : String.join(
                                ",",
                                components
                        );

        DashboardResponse dashboardResponse;

        try {

            dashboardResponse =
                    dashboardService
                            .getDashboard(
                                    securityContext,
                                    new DashboardFilterCriteria(
                                            criteria.getTenantId(),
                                            componentFilter
                                    )
                            );

        }
        catch (AccessDeniedException exception) {
            throw exception;
        }
        catch (InvalidDashboardFilterException exception) {

            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_CRITERIA",
                    exception.getMessage()
            );
        }
        catch (DashboardDataUnavailableException exception) {

            throw new ReportException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "REPORT_DATA_UNAVAILABLE",
                    "Dashboard data required by the report is unavailable"
            );
        }
        catch (RuntimeException exception) {

            throw new ReportException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "REPORT_DATA_UNAVAILABLE",
                    "Dashboard data required by the report is unavailable"
            );
        }

        if (
                dashboardResponse.getEffectiveFilters()
                        == null
        ) {
            throw new ReportException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "REPORT_GENERATION_FAILED",
                    "Dashboard effective filters are unavailable"
            );
        }

        Map<String, Object> effectiveCriteria =
                new LinkedHashMap<>();

        UUID effectiveTenantId =
                dashboardResponse
                        .getEffectiveFilters()
                        .getTenantId();

        if (effectiveTenantId != null) {
            effectiveCriteria.put(
                    "tenantId",
                    effectiveTenantId
            );
        }

        effectiveCriteria.put(
                "components",
                dashboardResponse
                        .getEffectiveFilters()
                        .getComponents()
                        .stream()
                        .map(Enum::name)
                        .toList()
        );

        Map<String, Object> content;

        try {
            content =
                    objectMapper.convertValue(
                            dashboardResponse,
                            MAP_TYPE
                    );
        }
        catch (RuntimeException exception) {

            throw new ReportException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "REPORT_GENERATION_FAILED",
                    "Dashboard snapshot could not be generated"
            );
        }

        return new ReportMaterial(
                effectiveTenantId,
                effectiveCriteria,
                content
        );
    }

    private ReportMaterial generateInvestigationCases(
            ReportCriteriaRequest criteria,
            UserAccountReference authorizedUser,
            SecurityContext securityContext) {

        int maxRecords =
                resolveMaxRecords(
                        authorizedUser.organizationId(),
                        authorizedUser.tenantId()
                );

        int pageSize =
                Math.min(
                        CASE_PAGE_SIZE,
                        maxRecords
                );

        PageResponse<CaseResponse> firstPage =
                searchCases(
                        criteria,
                        0,
                        pageSize,
                        securityContext
                );

        if (firstPage.getTotalElements() == 0) {

            throw new ReportException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "REPORT_NO_DATA",
                    "No data is available for the requested report"
            );
        }

        if (
                firstPage.getTotalElements()
                        > maxRecords
        ) {
            throw new ReportException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "REPORT_RESULT_LIMIT_EXCEEDED",
                    "Report result exceeds the configured maximum of "
                            + maxRecords
                            + " records"
            );
        }

        List<CaseResponse> cases =
                new ArrayList<>();

        if (firstPage.getContent() != null) {
            cases.addAll(
                    firstPage.getContent()
            );
        }

        for (
                int page = 1;
                page < firstPage.getTotalPages();
                page++
        ) {

            PageResponse<CaseResponse> nextPage =
                    searchCases(
                            criteria,
                            page,
                            pageSize,
                            securityContext
                    );

            if (nextPage.getContent() != null) {
                cases.addAll(
                        nextPage.getContent()
                );
            }
        }

        if (cases.size() > maxRecords) {
            throw new ReportException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "REPORT_RESULT_LIMIT_EXCEEDED",
                    "Report result exceeds the configured maximum of "
                            + maxRecords
                            + " records"
            );
        }

        List<Map<String, Object>> caseSnapshots =
                new ArrayList<>();

        try {

            for (CaseResponse caseResponse : cases) {

                caseSnapshots.add(
                        objectMapper.convertValue(
                                caseResponse,
                                MAP_TYPE
                        )
                );
            }

        }
        catch (RuntimeException exception) {

            throw new ReportException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "REPORT_GENERATION_FAILED",
                    "Investigation case snapshot could not be generated"
            );
        }

        Map<String, Object> content =
                new LinkedHashMap<>();

        content.put(
                "recordCount",
                caseSnapshots.size()
        );

        content.put(
                "cases",
                caseSnapshots
        );

        return new ReportMaterial(
                authorizedUser.tenantId(),
                investigationCriteria(
                        criteria
                ),
                content
        );
    }

    private PageResponse<CaseResponse> searchCases(
            ReportCriteriaRequest criteria,
            int page,
            int size,
            SecurityContext securityContext) {

        try {

            return caseService.searchCases(
                    normalizeOptionalText(
                            criteria.getStatus()
                    ),
                    normalizeOptionalText(
                            criteria.getPriority()
                    ),
                    criteria.getAssignedUser(),
                    normalizeOptionalText(
                            criteria.getAssignedTeam()
                    ),
                    page,
                    size,
                    CASE_SORT,
                    CASE_DIRECTION,
                    securityContext
            );

        }
        catch (AccessDeniedException exception) {
            throw exception;
        }
        catch (
                ValidationException
                | IllegalArgumentException exception
        ) {

            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_CRITERIA",
                    exception.getMessage()
            );
        }
        catch (RuntimeException exception) {

            throw new ReportException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "REPORT_DATA_UNAVAILABLE",
                    "Investigation data required by the report is unavailable"
            );
        }
    }

    private int resolveMaxRecords(
            UUID organizationId,
            UUID tenantId) {

        String configuredValue =
                systemConfigurationService
                        .resolveConfigurationValue(
                                MAX_RECORDS_CONFIGURATION,
                                organizationId,
                                tenantId
                        )
                        .orElse(
                                Integer.toString(
                                        DEFAULT_MAX_RECORDS
                                )
                        );

        try {

            int value =
                    Integer.parseInt(
                            configuredValue.trim()
                    );

            if (value < 1) {
                throw new NumberFormatException(
                        "non-positive"
                );
            }

            return value;

        }
        catch (NumberFormatException exception) {

            throw new ReportException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "REPORT_GENERATION_FAILED",
                    "Invalid EFS.REPORT.MAX_RECORDS configuration"
            );
        }
    }

    private Definition resolveDefinition(
            ReportGenerationRequest request) {

        if (request == null) {

            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_REQUEST",
                    "Report request is required"
            );
        }

        if (
                !request.getUnsupportedFields()
                        .isEmpty()
        ) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_REQUEST",
                    "Report request contains unsupported fields"
            );
        }

        String reportCode =
                request.getReportCode();

        if (
                reportCode == null
                        || reportCode.isBlank()
        ) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_REQUEST",
                    "reportCode is required"
            );
        }

        String normalized =
                reportCode.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return DEFINITIONS.stream()
                .filter(
                        definition ->
                                definition.code()
                                        .equals(
                                                normalized
                                        )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new ReportException(
                                        HttpStatus.BAD_REQUEST,
                                        "INVALID_REPORT_REQUEST",
                                        "Unknown reportCode: "
                                                + reportCode
                                )
                );
    }

    private ReportCriteriaRequest normalizeCriteria(
            ReportGenerationRequest request) {

        if (request.getCriteria() == null) {
            return new ReportCriteriaRequest();
        }

        return request.getCriteria();
    }

    private void validateCriteria(
            Definition definition,
            ReportCriteriaRequest criteria) {

        if (
                !criteria.getUnsupportedCriteria()
                        .isEmpty()
        ) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_CRITERIA",
                    "Report criteria contain unsupported fields"
            );
        }

        if (
                definition.code().equals(
                        OPERATIONAL_SUMMARY.code()
                )
        ) {

            if (
                    criteria.getStatus() != null
                            || criteria.getPriority() != null
                            || criteria.getAssignedUser() != null
                            || criteria.getAssignedTeam() != null
            ) {
                throw new ReportException(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_REPORT_CRITERIA",
                        "OPERATIONAL_SUMMARY accepts only tenantId and components"
                );
            }

            normalizeComponents(
                    criteria.getComponents()
            );

            return;
        }

        if (
                definition.code().equals(
                        INVESTIGATION_CASES.code()
                )
        ) {

            if (
                    criteria.getTenantId() != null
                            || criteria.getComponents() != null
            ) {
                throw new ReportException(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_REPORT_CRITERIA",
                        "INVESTIGATION_CASES accepts only status, priority, assignedUser and assignedTeam"
                );
            }

            validateOptionalText(
                    "status",
                    criteria.getStatus()
            );

            validateOptionalText(
                    "priority",
                    criteria.getPriority()
            );

            validateOptionalText(
                    "assignedTeam",
                    criteria.getAssignedTeam()
            );

            return;
        }

        throw new ReportException(
                HttpStatus.BAD_REQUEST,
                "INVALID_REPORT_REQUEST",
                "Unsupported report definition"
        );
    }

    private List<String> normalizeComponents(
            List<String> rawComponents) {

        if (rawComponents == null) {
            return null;
        }

        if (rawComponents.isEmpty()) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_CRITERIA",
                    "components must not be empty"
            );
        }

        Set<String> normalized =
                new LinkedHashSet<>();

        for (String component : rawComponents) {

            if (
                    component == null
                            || component.isBlank()
            ) {
                throw new ReportException(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_REPORT_CRITERIA",
                        "components contains an empty value"
                );
            }

            String value =
                    component.trim()
                            .toUpperCase(
                                    Locale.ROOT
                            );

            try {
                DashboardComponent.valueOf(
                        value
                );
            }
            catch (IllegalArgumentException exception) {

                throw new ReportException(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_REPORT_CRITERIA",
                        "Unknown Dashboard component: "
                                + component
                );
            }

            normalized.add(
                    value
            );
        }

        return List.copyOf(
                normalized
        );
    }

    private void validateOptionalText(
            String field,
            String value) {

        if (
                value != null
                        && value.isBlank()
        ) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_CRITERIA",
                    field + " must not be blank"
            );
        }
    }

    private String normalizeOptionalText(
            String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private Map<String, Object>
    investigationCriteria(
            ReportCriteriaRequest criteria) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        if (criteria.getStatus() != null) {
            result.put(
                    "status",
                    criteria.getStatus().trim()
            );
        }

        if (criteria.getPriority() != null) {
            result.put(
                    "priority",
                    criteria.getPriority().trim()
            );
        }

        if (criteria.getAssignedUser() != null) {
            result.put(
                    "assignedUser",
                    criteria.getAssignedUser()
            );
        }

        if (criteria.getAssignedTeam() != null) {
            result.put(
                    "assignedTeam",
                    criteria.getAssignedTeam()
                            .trim()
            );
        }

        return result;
    }

    private void requireSourcePermissionForPersistedReport(
            SecurityContext securityContext,
            GeneratedReport report) {

        Definition definition =
                DEFINITIONS.stream()
                        .filter(
                                candidate ->
                                        candidate.code().equals(
                                                report.getReportCode()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new ReportException(
                                                HttpStatus.NOT_FOUND,
                                                "REPORT_NOT_FOUND",
                                                "Generated report was not found"
                                        )
                        );

        requireSourcePermission(
                securityContext,
                definition
        );
    }

    private void requireSourcePermission(
            SecurityContext securityContext,
            Definition definition) {

        if (
                !securityContext.hasPermission(
                        definition.sourcePermission()
                )
        ) {
            throw new AccessDeniedException(
                    "Missing required source permission: "
                            + definition.sourcePermission()
            );
        }
    }

    private void requirePermission(
            SecurityContext securityContext,
            String permission) {

        if (
                !securityContext.hasPermission(
                        permission
                )
        ) {
            throw new AccessDeniedException(
                    "Missing required permission: "
                            + permission
            );
        }
    }

    private UserAccountReference resolveAuthorizedUser(
            SecurityContext securityContext) {

        UserAccountReference authorizedUser =
                userAccountLookupService
                        .getAuthorizedUser(
                                securityContext.getUserId()
                        );

        if (
                authorizedUser.organizationId()
                        == null
        ) {
            throw new IllegalStateException(
                    "Authorized user organization is required"
            );
        }

        if (
                !Objects.equals(
                        securityContext.getTenantId(),
                        authorizedUser.tenantId()
                )
        ) {
            throw new IllegalStateException(
                    "Authenticated tenant scope does not match authorized user scope"
            );
        }

        return authorizedUser;
    }

    private GeneratedReportResponse toResponse(
            GeneratedReport report) {

        return new GeneratedReportResponse(
                report.getReportId(),
                report.getOrganizationId(),
                report.getTenantId(),
                report.getReportCode(),
                report.getCriteria(),
                report.getContent(),
                report.getGeneratedBy(),
                report.getGeneratedAt()
        );
    }

    private String safeReportCode(
            ReportGenerationRequest request) {

        if (
                request == null
                        || request.getReportCode() == null
        ) {
            return null;
        }

        return request.getReportCode();
    }

    private Map<String, Object> safeCriteria(
            ReportGenerationRequest request) {

        if (
                request == null
                        || request.getCriteria() == null
        ) {
            return Map.of();
        }

        ReportCriteriaRequest criteria =
                request.getCriteria();

        Map<String, Object> result =
                new LinkedHashMap<>();

        if (criteria.getTenantId() != null) {
            result.put(
                    "tenantId",
                    criteria.getTenantId()
            );
        }

        if (criteria.getComponents() != null) {
            result.put(
                    "components",
                    criteria.getComponents()
            );
        }

        if (criteria.getStatus() != null) {
            result.put(
                    "status",
                    criteria.getStatus()
            );
        }

        if (criteria.getPriority() != null) {
            result.put(
                    "priority",
                    criteria.getPriority()
            );
        }

        if (criteria.getAssignedUser() != null) {
            result.put(
                    "assignedUser",
                    criteria.getAssignedUser()
            );
        }

        if (criteria.getAssignedTeam() != null) {
            result.put(
                    "assignedTeam",
                    criteria.getAssignedTeam()
            );
        }

        if (
                !criteria.getUnsupportedCriteria()
                        .isEmpty()
        ) {
            result.put(
                    "unsupportedCriteria",
                    criteria.getUnsupportedCriteria()
                            .keySet()
            );
        }

        return result;
    }

    private void recordSuccess(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            UUID reportId,
            String reportCode,
            Map<String, Object> criteria,
            LocalDateTime generatedAt) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        tenantId,
                        reportId,
                        "SUCCESS"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reportCode",
                reportCode
        );

        details.put(
                "criteria",
                criteria
        );

        details.put(
                "organizationId",
                organizationId
        );

        details.put(
                "tenantId",
                tenantId
        );

        details.put(
                "generatedAt",
                generatedAt
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void recordRejected(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            String reportCode,
            Map<String, Object> criteria,
            String reason) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        tenantId,
                        null,
                        "REJECTED"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reportCode",
                reportCode
        );

        details.put(
                "criteria",
                criteria
        );

        details.put(
                "reason",
                reason
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void recordFailure(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            String reportCode,
            Map<String, Object> criteria,
            String reason,
            RuntimeException exception) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        tenantId,
                        null,
                        "FAILURE"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reportCode",
                reportCode
        );

        details.put(
                "criteria",
                criteria
        );

        details.put(
                "reason",
                reason
        );

        details.put(
                "errorType",
                exception.getClass()
                        .getSimpleName()
        );

        details.put(
                "errorMessage",
                exception.getMessage()
        );

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private AuditEventRequest baseAuditRequest(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            UUID reportId,
            String eventResult) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                REPORT_GENERATION_EVENT
        );

        request.setEntityType(
                REPORT_ENTITY_TYPE
        );

        request.setEntityId(
                reportId
        );

        request.setAction(
                REPORT_ACTION
        );

        request.setSourceComponent(
                REPORT_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        return request;
    }

    private record Definition(
            String code,
            String category,
            String sourcePermission,
            List<String> allowedCriteria) {
    }

    private record ReportMaterial(
            UUID tenantId,
            Map<String, Object> criteria,
            Map<String, Object> content) {
    }
}
