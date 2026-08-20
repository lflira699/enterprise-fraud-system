package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleParameterRequest;
import com.efs.modules.rules.dto.RuleParameterResponse;
import com.efs.modules.rules.entity.RuleParameter;
import org.springframework.stereotype.Component;

@Component
public class RuleParameterMapper {

    public RuleParameter toEntity(
            RuleParameterRequest request) {

        RuleParameter parameter =
                new RuleParameter();

        parameter.setParameterName(
                request.getParameterName()
        );

        parameter.setParameterType(
                request.getParameterType()
        );

        parameter.setParameterValue(
                request.getParameterValue()
        );

        parameter.setIsSensitive(
                request.getIsSensitive()
        );

        parameter.setValidationExpression(
                request.getValidationExpression()
        );

        return parameter;
    }

    public RuleParameterResponse toResponse(
            RuleParameter parameter) {

        RuleParameterResponse response =
                new RuleParameterResponse();

        response.setParameterId(
                parameter.getParameterId()
        );

        response.setRuleVersionId(
                parameter.getRuleVersionId()
        );

        response.setParameterName(
                parameter.getParameterName()
        );

        response.setParameterType(
                parameter.getParameterType()
        );

        response.setParameterValue(
                parameter.getParameterValue()
        );

        response.setIsSensitive(
                parameter.getIsSensitive()
        );

        response.setValidationExpression(
                parameter.getValidationExpression()
        );

        return response;
    }
}