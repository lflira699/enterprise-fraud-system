package com.efs.modules.rules.service;

public final class RuleTestDatasetEvaluation {

    private final String datasetReference;

    private final long sampleSize;

    private final long matchCount;

    private final long nonMatchCount;

    public RuleTestDatasetEvaluation(
            String datasetReference,
            long sampleSize,
            long matchCount,
            long nonMatchCount) {

        this.datasetReference =
                datasetReference;

        this.sampleSize =
                sampleSize;

        this.matchCount =
                matchCount;

        this.nonMatchCount =
                nonMatchCount;
    }

    public String getDatasetReference() {
        return datasetReference;
    }

    public long getSampleSize() {
        return sampleSize;
    }

    public long getMatchCount() {
        return matchCount;
    }

    public long getNonMatchCount() {
        return nonMatchCount;
    }
}