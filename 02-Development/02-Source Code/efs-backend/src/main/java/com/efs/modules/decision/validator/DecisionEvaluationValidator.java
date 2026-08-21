package com.efs.modules.decision.validator;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DecisionEvaluationValidator {

    private static final Set<String> VALID_RISK_LEVELS = Set.of(
            "MUY_BAJO",
            "BAJO",
            "MEDIO",
            "ALTO",
            "CRITICO"
    );

    private static final Set<String> VALID_CONFIDENCE_LEVELS = Set.of(
            "MUY_BAJA",
            "BAJA",
            "MEDIA",
            "ALTA",
            "MUY_ALTA"
    );

    public boolean isValidRiskLevel(String riskLevel) {
        return riskLevel != null
                && VALID_RISK_LEVELS.contains(normalize(riskLevel));
    }

    public boolean isValidConfidenceLevel(String confidenceLevel) {
        return confidenceLevel != null
                && VALID_CONFIDENCE_LEVELS.contains(normalize(confidenceLevel));
    }

    public String normalize(String value) {
        return value
                .trim()
                .toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace(" ", "_");
    }
}