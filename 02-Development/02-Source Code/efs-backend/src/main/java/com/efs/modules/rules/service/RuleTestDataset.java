package com.efs.modules.rules.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RuleTestDataset {

    private final String datasetReference;

    private final List<Map<String, Object>> records;

    public RuleTestDataset(
            String datasetReference,
            List<Map<String, Object>> records) {

        if (datasetReference == null
                || datasetReference.isBlank()) {

            throw new IllegalArgumentException(
                    "Dataset reference is required"
            );
        }

        if (records == null) {
            throw new IllegalArgumentException(
                    "Dataset records are required"
            );
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException(
                    "Dataset records must not be empty"
            );
        }

        List<Map<String, Object>> copiedRecords =
                new ArrayList<>(
                        records.size()
                );

        for (int index = 0;
             index < records.size();
             index++) {

            Map<String, Object> record =
                    records.get(index);

            if (record == null) {
                throw new IllegalArgumentException(
                        "Dataset record at index "
                                + index
                                + " must not be null"
                );
            }

            copiedRecords.add(
                    Collections.unmodifiableMap(
                            new LinkedHashMap<>(
                                    record
                            )
                    )
            );
        }

        this.datasetReference =
                datasetReference.trim();

        this.records =
                Collections.unmodifiableList(
                        copiedRecords
                );
    }

    public String getDatasetReference() {
        return datasetReference;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public int getSampleSize() {
        return records.size();
    }
}