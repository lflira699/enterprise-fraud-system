package com.efs.modules.rules.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RuleVersionDatasetEvaluator {

    private final RuleVersionEvaluator ruleVersionEvaluator;

    public RuleVersionDatasetEvaluator(
            RuleVersionEvaluator ruleVersionEvaluator) {

        this.ruleVersionEvaluator =
                ruleVersionEvaluator;
    }

    @Transactional(readOnly = true)
    public RuleTestDatasetEvaluation evaluate(
            UUID ruleVersionId,
            String datasetReference,
            List<Map<String, Object>> records) {

        if (ruleVersionId == null) {
            throw new IllegalArgumentException(
                    "Rule version identifier is required"
            );
        }

        if (datasetReference == null
                || datasetReference.isBlank()) {

            throw new IllegalArgumentException(
                    "Dataset reference is required"
            );
        }

        if (records == null) {
            throw new IllegalArgumentException(
                    "Test dataset records are required"
            );
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException(
                    "Test dataset must contain "
                            + "at least one record"
            );
        }

        long matchCount =
                0L;

        for (int index = 0;
             index < records.size();
             index++) {

            Map<String, Object> facts =
                    records.get(index);

            if (facts == null) {
                throw new IllegalArgumentException(
                        "Test dataset record cannot be null "
                                + "at index "
                                + index
                );
            }

            boolean matched =
                    ruleVersionEvaluator.evaluate(
                            ruleVersionId,
                            facts
                    );

            if (matched) {
                matchCount++;
            }
        }

        long sampleSize =
                records.size();

        return new RuleTestDatasetEvaluation(
                datasetReference,
                sampleSize,
                matchCount,
                sampleSize - matchCount
        );
    }
}