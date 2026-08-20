package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleMetricRequest;
import com.efs.modules.rules.dto.RuleMetricResponse;
import com.efs.modules.rules.entity.RuleMetric;
import org.springframework.stereotype.Component;

@Component
public class RuleMetricMapper {

    public RuleMetric toEntity(
            RuleMetricRequest request) {

        RuleMetric metric =
                new RuleMetric();

        metric.setRuleId(
                request.getRuleId()
        );

        metric.setRuleVersionId(
                request.getRuleVersionId()
        );

        metric.setMetricDate(
                request.getMetricDate()
        );

        metric.setExecutionCount(
                request.getExecutionCount()
        );

        metric.setMatchCount(
                request.getMatchCount()
        );

        metric.setConfirmedFraudCount(
                request.getConfirmedFraudCount()
        );

        metric.setFalsePositiveCount(
                request.getFalsePositiveCount()
        );

        metric.setFalseNegativeCount(
                request.getFalseNegativeCount()
        );

        metric.setAverageExecutionMs(
                request.getAverageExecutionMs()
        );

        metric.setPreventedAmount(
                request.getPreventedAmount()
        );

        metric.setCurrencyCode(
                request.getCurrencyCode()
        );

        return metric;
    }

    public RuleMetricResponse toResponse(
            RuleMetric metric) {

        RuleMetricResponse response =
                new RuleMetricResponse();

        response.setMetricId(
                metric.getMetricId()
        );

        response.setRuleId(
                metric.getRuleId()
        );

        response.setRuleVersionId(
                metric.getRuleVersionId()
        );

        response.setMetricDate(
                metric.getMetricDate()
        );

        response.setExecutionCount(
                metric.getExecutionCount()
        );

        response.setMatchCount(
                metric.getMatchCount()
        );

        response.setConfirmedFraudCount(
                metric.getConfirmedFraudCount()
        );

        response.setFalsePositiveCount(
                metric.getFalsePositiveCount()
        );

        response.setFalseNegativeCount(
                metric.getFalseNegativeCount()
        );

        response.setAverageExecutionMs(
                metric.getAverageExecutionMs()
        );

        response.setPreventedAmount(
                metric.getPreventedAmount()
        );

        response.setCurrencyCode(
                metric.getCurrencyCode()
        );

        response.setCalculatedAt(
                metric.getCalculatedAt()
        );

        return response;
    }
}