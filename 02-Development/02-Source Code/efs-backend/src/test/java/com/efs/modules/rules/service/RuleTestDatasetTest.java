package com.efs.modules.rules.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleTestDatasetTest {

    @Test
    void shouldCreateImmutableDatasetSnapshot() {

        Map<String, Object> sourceRecord =
                new LinkedHashMap<>();

        sourceRecord.put(
                "transaction.amount",
                7500
        );

        List<Map<String, Object>> sourceRecords =
                new ArrayList<>();

        sourceRecords.add(
                sourceRecord
        );

        RuleTestDataset dataset =
                new RuleTestDataset(
                        " dataset://uc025/controlled ",
                        sourceRecords
                );

        sourceRecord.put(
                "transaction.amount",
                100
        );

        sourceRecords.clear();

        assertEquals(
                "dataset://uc025/controlled",
                dataset.getDatasetReference()
        );

        assertEquals(
                1,
                dataset.getSampleSize()
        );

        assertEquals(
                7500,
                dataset.getRecords()
                        .getFirst()
                        .get(
                                "transaction.amount"
                        )
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> dataset.getRecords()
                        .add(
                                Map.of()
                        )
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> dataset.getRecords()
                        .getFirst()
                        .put(
                                "transaction.amount",
                                9000
                        )
        );
    }

    @Test
    void shouldRejectBlankDatasetReference() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new RuleTestDataset(
                                "   ",
                                List.of(
                                        Map.of(
                                                "transaction.amount",
                                                100
                                        )
                                )
                        )
        );
    }

    @Test
    void shouldRejectNullRecords() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new RuleTestDataset(
                                "dataset://uc025/null",
                                null
                        )
        );
    }

    @Test
    void shouldRejectEmptyRecords() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new RuleTestDataset(
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
                        100
                )
        );

        records.add(
                null
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new RuleTestDataset(
                                        "dataset://uc025/null-record",
                                        records
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "index 1"
                        )
        );
    }
}