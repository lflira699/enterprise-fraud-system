package com.efs.modules.rules.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RuleVersionDatasetEvaluatorTest {

    private RuleVersionEvaluator ruleVersionEvaluator;

    private RuleVersionDatasetEvaluator datasetEvaluator;

    @BeforeEach
    void setUp() {

        ruleVersionEvaluator =
                mock(
                        RuleVersionEvaluator.class
                );

        datasetEvaluator =
                new RuleVersionDatasetEvaluator(
                        ruleVersionEvaluator
                );
    }

    @Test
    void shouldCalculateSampleAndMatchCountsFromDataset() {

        UUID ruleVersionId =
                UUID.randomUUID();

        Map<String, Object> first =
                Map.of(
                        "transaction.amount",
                        7500
                );

        Map<String, Object> second =
                Map.of(
                        "transaction.amount",
                        2500
                );

        Map<String, Object> third =
                Map.of(
                        "transaction.amount",
                        9000
                );

        when(
                ruleVersionEvaluator.evaluate(
                        ruleVersionId,
                        first
                )
        )
                .thenReturn(true);

        when(
                ruleVersionEvaluator.evaluate(
                        ruleVersionId,
                        second
                )
        )
                .thenReturn(false);

        when(
                ruleVersionEvaluator.evaluate(
                        ruleVersionId,
                        third
                )
        )
                .thenReturn(true);

        RuleTestDatasetEvaluation result =
                datasetEvaluator.evaluate(
                        ruleVersionId,
                        "dataset://uc025/normalized-001",
                        List.of(
                                first,
                                second,
                                third
                        )
                );

        assertEquals(
                "dataset://uc025/normalized-001",
                result.getDatasetReference()
        );

        assertEquals(
                3L,
                result.getSampleSize()
        );

        assertEquals(
                2L,
                result.getMatchCount()
        );

        assertEquals(
                1L,
                result.getNonMatchCount()
        );

        verify(
                ruleVersionEvaluator,
                times(3)
        ).evaluate(
                org.mockito.ArgumentMatchers.eq(
                        ruleVersionId
                ),
                org.mockito.ArgumentMatchers.anyMap()
        );
    }

    @Test
    void shouldReturnZeroMatchesWhenNoRecordMatches() {

        UUID ruleVersionId =
                UUID.randomUUID();

        Map<String, Object> first =
                Map.of(
                        "transaction.amount",
                        1000
                );

        Map<String, Object> second =
                Map.of(
                        "transaction.amount",
                        2000
                );

        when(
                ruleVersionEvaluator.evaluate(
                        ruleVersionId,
                        first
                )
        )
                .thenReturn(false);

        when(
                ruleVersionEvaluator.evaluate(
                        ruleVersionId,
                        second
                )
        )
                .thenReturn(false);

        RuleTestDatasetEvaluation result =
                datasetEvaluator.evaluate(
                        ruleVersionId,
                        "dataset://uc025/no-match",
                        List.of(
                                first,
                                second
                        )
                );

        assertEquals(
                2L,
                result.getSampleSize()
        );

        assertEquals(
                0L,
                result.getMatchCount()
        );

        assertEquals(
                2L,
                result.getNonMatchCount()
        );
    }

    @Test
    void shouldRejectBlankDatasetReference() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        datasetEvaluator.evaluate(
                                UUID.randomUUID(),
                                "   ",
                                List.of(
                                        Map.of(
                                                "transaction.amount",
                                                5000
                                        )
                                )
                        )
        );
    }

    @Test
    void shouldRejectNullDataset() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        datasetEvaluator.evaluate(
                                UUID.randomUUID(),
                                "dataset://uc025/null",
                                null
                        )
        );
    }

    @Test
    void shouldRejectEmptyDataset() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        datasetEvaluator.evaluate(
                                UUID.randomUUID(),
                                "dataset://uc025/empty",
                                List.of()
                        )
        );
    }

    @Test
    void shouldRejectNullRecordInsideDataset() {

        List<Map<String, Object>> records =
                new ArrayList<>();

        records.add(
                Map.of(
                        "transaction.amount",
                        5000
                )
        );

        records.add(
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        datasetEvaluator.evaluate(
                                UUID.randomUUID(),
                                "dataset://uc025/null-record",
                                records
                        )
        );
    }
}