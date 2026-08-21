package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.decision.dto.DecisionEvaluationResponse;
import com.efs.modules.decision.validator.DecisionEvaluationValidator;
import com.efs.modules.risk.entity.RiskAssessment;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DecisionEvaluationService
        implements DecisionEvaluationServiceInterface {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final DecisionEvaluationValidator decisionEvaluationValidator;

    public DecisionEvaluationService(
            RiskAssessmentRepository riskAssessmentRepository,
            DecisionEvaluationValidator decisionEvaluationValidator) {

        this.riskAssessmentRepository =
                riskAssessmentRepository;

        this.decisionEvaluationValidator =
                decisionEvaluationValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionEvaluationResponse evaluateDecision(
            DecisionEvaluationRequest request) {

        RiskAssessment assessment =
                riskAssessmentRepository
                        .findById(request.getRiskAssessmentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Risk assessment not found: "
                                                + request.getRiskAssessmentId()
                                )
                        );

        String riskLevel =
                decisionEvaluationValidator.normalize(
                        assessment.getRiskLevel()
                );

        String confidenceLevel =
                decisionEvaluationValidator.normalize(
                        request.getConfidenceLevel()
                );

        if (!decisionEvaluationValidator
                .isValidRiskLevel(riskLevel)) {

            throw new IllegalArgumentException(
                    "Unsupported risk level: "
                            + assessment.getRiskLevel()
            );
        }

        if (!decisionEvaluationValidator
                .isValidConfidenceLevel(confidenceLevel)) {

            throw new IllegalArgumentException(
                    "Unsupported confidence level: "
                            + request.getConfidenceLevel()
            );
        }

        DecisionRecommendation recommendation =
                resolveRecommendation(
                        riskLevel,
                        confidenceLevel
                );

        DecisionEvaluationResponse response =
                new DecisionEvaluationResponse();

        response.setRiskAssessmentId(
                assessment.getRiskAssessmentId()
        );

        response.setTransactionId(
                assessment.getTransactionId()
        );

        response.setDecisionType(
                recommendation.decisionType()
        );

        response.setConfidenceScore(
                assessment.getConfidenceScore()
        );

        response.setDecisionReason(
                recommendation.decisionReason()
        );

        response.setFinalDecision(
                false
        );

        return response;
    }

    private DecisionRecommendation resolveRecommendation(
            String riskLevel,
            String confidenceLevel) {

        if ("MUY_BAJO".equals(riskLevel)
                && ("MUY_ALTA".equals(confidenceLevel)
                || "ALTA".equals(confidenceLevel))) {

            return new DecisionRecommendation(
                    "APPROVE",
                    "Aprobar"
            );
        }

        if ("BAJO".equals(riskLevel)
                && "ALTA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "APPROVE_WITH_MONITORING",
                    "Aprobar con monitoreo"
            );
        }

        if ("BAJO".equals(riskLevel)
                && "MEDIA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "MONITOR",
                    "Monitoreo adicional"
            );
        }

        if ("MEDIO".equals(riskLevel)
                && "ALTA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "ADDITIONAL_VALIDATION",
                    "Solicitar validaciones adicionales"
            );
        }

        if ("MEDIO".equals(riskLevel)
                && "MEDIA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "MANUAL_REVIEW",
                    "Escalar para revisión según políticas"
            );
        }

        if ("MEDIO".equals(riskLevel)
                && "BAJA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "REQUEST_EVIDENCE",
                    "Recolectar evidencia adicional"
            );
        }

        if ("ALTO".equals(riskLevel)
                && "ALTA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "ESCALATE",
                    "Escalar inmediatamente"
            );
        }

        if ("ALTO".equals(riskLevel)
                && "MUY_ALTA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "RESTRICT",
                    "Suspender o restringir conforme a políticas"
            );
        }

        if ("ALTO".equals(riskLevel)
                && "BAJA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "VALIDATE_EVIDENCE",
                    "Validar evidencia antes de una decisión definitiva"
            );
        }

        if ("CRITICO".equals(riskLevel)
                && "MUY_ALTA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "IMMEDIATE_CONTROLS",
                    "Activar controles inmediatos"
            );
        }

        if ("CRITICO".equals(riskLevel)
                && "ALTA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "PRIORITY_INVESTIGATION",
                    "Investigación prioritaria y medidas de contención"
            );
        }

        if ("CRITICO".equals(riskLevel)
                && "MEDIA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "URGENT_ESCALATION",
                    "Escalamiento urgente con validaciones complementarias"
            );
        }

        if ("CRITICO".equals(riskLevel)
                && "BAJA".equals(confidenceLevel)) {

            return new DecisionRecommendation(
                    "REQUEST_EVIDENCE",
                    "Obtener evidencia adicional antes de adoptar medidas "
                            + "irreversibles, salvo obligaciones regulatorias "
                            + "o riesgos inminentes"
            );
        }

        throw new IllegalArgumentException(
                "Decision matrix combination not configured. "
                        + "Risk level: "
                        + riskLevel
                        + ", confidence level: "
                        + confidenceLevel
        );
    }

    private record DecisionRecommendation(
            String decisionType,
            String decisionReason) {
    }
}