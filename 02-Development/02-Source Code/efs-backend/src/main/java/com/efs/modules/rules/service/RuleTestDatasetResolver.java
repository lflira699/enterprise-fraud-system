package com.efs.modules.rules.service;

import com.efs.shared.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleTestDatasetResolver {

    private final List<RuleTestDatasetProvider> providers;

    public RuleTestDatasetResolver(
            List<RuleTestDatasetProvider> providers) {

        this.providers =
                providers == null
                        ? List.of()
                        : List.copyOf(
                                providers
                        );
    }

    public RuleTestDataset resolve(
            String datasetReference) {

        if (datasetReference == null
                || datasetReference.isBlank()) {

            throw new IllegalArgumentException(
                    "Dataset reference is required"
            );
        }

        String normalizedReference =
                datasetReference.trim();

        List<RuleTestDatasetProvider>
                supportingProviders =
                new ArrayList<>();

        for (RuleTestDatasetProvider provider
                : providers) {

            if (provider.supports(
                    normalizedReference
            )) {

                supportingProviders.add(
                        provider
                );
            }
        }

        if (supportingProviders.isEmpty()) {
            throw new ValidationException(
                    "Rule test dataset is not available: "
                            + normalizedReference
            );
        }

        if (supportingProviders.size() > 1) {
            throw new IllegalStateException(
                    "Multiple RuleTestDatasetProvider "
                            + "implementations support dataset: "
                            + normalizedReference
            );
        }

        RuleTestDataset dataset =
                supportingProviders
                        .getFirst()
                        .load(
                                normalizedReference
                        );

        if (dataset == null) {
            throw new IllegalStateException(
                    "RuleTestDatasetProvider returned "
                            + "no dataset for: "
                            + normalizedReference
            );
        }

        return dataset;
    }
}