package com.efs.modules.rules.service;

public interface RuleTestDatasetProvider {

    boolean supports(
            String datasetReference
    );

    RuleTestDataset load(
            String datasetReference
    );
}