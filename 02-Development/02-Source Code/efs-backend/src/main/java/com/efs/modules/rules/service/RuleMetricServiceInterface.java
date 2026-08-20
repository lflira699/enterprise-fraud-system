package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleMetricRequest;
import com.efs.modules.rules.dto.RuleMetricResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RuleMetricServiceInterface {

    RuleMetricResponse createRuleMetric(
            RuleMetricRequest request
    );

    RuleMetricResponse getRuleMetricById(
            UUID metricId
    );

    List<RuleMetricResponse> getRuleMetricsByRuleId(
            UUID ruleId
    );

    List<RuleMetricResponse> getRuleMetricsByRuleVersionId(
            UUID ruleVersionId
    );

    List<RuleMetricResponse> getRuleMetricsByDate(
            LocalDate metricDate
    );
}