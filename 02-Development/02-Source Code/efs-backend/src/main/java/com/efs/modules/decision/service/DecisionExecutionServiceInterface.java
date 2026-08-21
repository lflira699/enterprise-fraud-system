package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;

public interface DecisionExecutionServiceInterface {

    TransactionDecisionResponse evaluateAndPersistDecision(
            DecisionEvaluationRequest request
    );
}