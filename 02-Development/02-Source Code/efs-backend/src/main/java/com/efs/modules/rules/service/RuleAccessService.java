package com.efs.modules.rules.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.rules.dto.RuleActivationRequest;
import com.efs.modules.rules.dto.RuleDeactivationRequest;
import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.dto.RuleTestingRequest;
import com.efs.modules.rules.dto.RuleUpdateRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleAccessService
        implements RuleAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "rule.view";

    private static final String CREATE_PERMISSION =
            "rule.create";

    private static final String UPDATE_PERMISSION =
            "rule.update";

    private static final String ACTIVATE_PERMISSION =
            "rule.activate";

    private static final String DEACTIVATE_PERMISSION =
            "rule.deactivate";

    private static final String TEST_PERMISSION =
            "rule.test";

    private final RuleServiceInterface
            ruleService;

    private final RuleTestingService
            ruleTestingService;

    private final RuleAuthorizationServiceInterface
            ruleAuthorizationService;

    public RuleAccessService(
            RuleServiceInterface ruleService,
            RuleTestingService ruleTestingService,
            RuleAuthorizationServiceInterface
                    ruleAuthorizationService) {

        this.ruleService =
                ruleService;

        this.ruleTestingService =
                ruleTestingService;

        this.ruleAuthorizationService =
                ruleAuthorizationService;
    }

    @Override
    public RuleResponse createRule(
            RuleRequest request,
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                CREATE_PERMISSION
        );

        return ruleService.createRule(
                request
        );
    }

    @Override
    public List<RuleResponse> getRules(
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                VIEW_PERMISSION
        );

        return ruleService.getRules();
    }

    @Override
    public RuleResponse getRuleById(
            UUID ruleId,
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                VIEW_PERMISSION
        );

        return ruleService.getRuleById(
                ruleId
        );
    }

    @Override
    public RuleResponse getRuleByCode(
            String ruleCode,
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                VIEW_PERMISSION
        );

        return ruleService.getRuleByCode(
                ruleCode
        );
    }

    @Override
    public List<RuleResponse> getRulesByStatus(
            String status,
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                VIEW_PERMISSION
        );

        return ruleService.getRulesByStatus(
                status
        );
    }

    @Override
    public List<RuleResponse> getRulesByCategory(
            String category,
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                VIEW_PERMISSION
        );

        return ruleService.getRulesByCategory(
                category
        );
    }

    @Override
    public List<RuleResponse> getRulesBySeverity(
            String severity,
            SecurityContext securityContext) {

        ruleAuthorizationService.authorize(
                securityContext,
                VIEW_PERMISSION
        );

        return ruleService.getRulesBySeverity(
                severity
        );
    }

    @Override
    public RuleVersionResponse updateRule(
            UUID ruleId,
            RuleUpdateRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                ruleAuthorizationService.authorize(
                        securityContext,
                        UPDATE_PERMISSION
                );

        ruleAuthorizationService.requireActor(
                actor,
                request.getChangedBy(),
                "changedBy"
        );

        return ruleService.updateRule(
                ruleId,
                request
        );
    }

    @Override
    public RuleResponse activateRule(
            UUID ruleId,
            RuleActivationRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                ruleAuthorizationService.authorize(
                        securityContext,
                        ACTIVATE_PERMISSION
                );

        ruleAuthorizationService.requireActor(
                actor,
                request.getChangedBy(),
                "changedBy"
        );

        return ruleService.activateRule(
                ruleId,
                request
        );
    }

    @Override
    public RuleResponse deactivateRule(
            UUID ruleId,
            RuleDeactivationRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                ruleAuthorizationService.authorize(
                        securityContext,
                        DEACTIVATE_PERMISSION
                );

        ruleAuthorizationService.requireActor(
                actor,
                request.getChangedBy(),
                "changedBy"
        );

        return ruleService.deactivateRule(
                ruleId,
                request
        );
    }

    @Override
    public RuleSimulationResponse testRule(
            UUID ruleId,
            UUID ruleVersionId,
            RuleTestingRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                ruleAuthorizationService.authorize(
                        securityContext,
                        TEST_PERMISSION
                );

        ruleAuthorizationService.requireActor(
                actor,
                request.getExecutedBy(),
                "executedBy"
        );

        return ruleTestingService.execute(
                ruleId,
                ruleVersionId,
                request.getSimulationName(),
                request.getDatasetReference(),
                request.getExecutedBy(),
                request.getCorrelationId()
        );
    }
}