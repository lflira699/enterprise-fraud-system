package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.decision.dto.DecisionEvaluationResponse;

public interface DecisionEvaluationServiceInterface {

    DecisionEvaluationResponse evaluateDecision(
            DecisionEvaluationRequest request
    );
}