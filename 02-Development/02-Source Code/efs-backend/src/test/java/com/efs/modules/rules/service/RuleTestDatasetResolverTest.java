package com.efs.modules.rules.service;

import com.efs.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RuleTestDatasetResolverTest {

    @Test
    void shouldResolveDatasetFromSingleSupportingProvider() {

        RuleTestDatasetProvider provider =
                mock(
                        RuleTestDatasetProvider.class
                );

        RuleTestDataset expected =
                new RuleTestDataset(
                        "dataset://uc025/provider",
                        List.of(
                                Map.of(
                                        "transaction.amount",
                                        7500
                                )
                        )
                );

        when(
                provider.supports(
                        "dataset://uc025/provider"
                )
        ).thenReturn(
                true
        );

        when(
                provider.load(
                        "dataset://uc025/provider"
                )
        ).thenReturn(
                expected
        );

        RuleTestDatasetResolver resolver =
                new RuleTestDatasetResolver(
                        List.of(
                                provider
                        )
                );

        RuleTestDataset resolved =
                resolver.resolve(
                        " dataset://uc025/provider "
                );

        assertSame(
                expected,
                resolved
        );

        verify(
                provider
        ).supports(
                "dataset://uc025/provider"
        );

        verify(
                provider
        ).load(
                "dataset://uc025/provider"
        );
    }

    @Test
    void shouldRejectBlankDatasetReference() {

        RuleTestDatasetProvider provider =
                mock(
                        RuleTestDatasetProvider.class
                );

        RuleTestDatasetResolver resolver =
                new RuleTestDatasetResolver(
                        List.of(
                                provider
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                "   "
                        )
        );

        verify(
                provider,
                never()
        ).supports(
                "   "
        );
    }

    @Test
    void shouldRejectDatasetWithNoSupportingProvider() {

        RuleTestDatasetProvider provider =
                mock(
                        RuleTestDatasetProvider.class
                );

        when(
                provider.supports(
                        "dataset://uc025/unknown"
                )
        ).thenReturn(
                false
        );

        RuleTestDatasetResolver resolver =
                new RuleTestDatasetResolver(
                        List.of(
                                provider
                        )
                );

        assertThrows(
                ValidationException.class,
                () ->
                        resolver.resolve(
                                "dataset://uc025/unknown"
                        )
        );

        verify(
                provider,
                never()
        ).load(
                "dataset://uc025/unknown"
        );
    }

    @Test
    void shouldRejectAmbiguousDatasetProviderConfiguration() {

        RuleTestDatasetProvider firstProvider =
                mock(
                        RuleTestDatasetProvider.class
                );

        RuleTestDatasetProvider secondProvider =
                mock(
                        RuleTestDatasetProvider.class
                );

        when(
                firstProvider.supports(
                        "dataset://uc025/ambiguous"
                )
        ).thenReturn(
                true
        );

        when(
                secondProvider.supports(
                        "dataset://uc025/ambiguous"
                )
        ).thenReturn(
                true
        );

        RuleTestDatasetResolver resolver =
                new RuleTestDatasetResolver(
                        List.of(
                                firstProvider,
                                secondProvider
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolve(
                                "dataset://uc025/ambiguous"
                        )
        );

        verify(
                firstProvider,
                never()
        ).load(
                "dataset://uc025/ambiguous"
        );

        verify(
                secondProvider,
                never()
        ).load(
                "dataset://uc025/ambiguous"
        );
    }

    @Test
    void shouldRejectNullDatasetReturnedByProvider() {

        RuleTestDatasetProvider provider =
                mock(
                        RuleTestDatasetProvider.class
                );

        when(
                provider.supports(
                        "dataset://uc025/null-result"
                )
        ).thenReturn(
                true
        );

        when(
                provider.load(
                        "dataset://uc025/null-result"
                )
        ).thenReturn(
                null
        );

        RuleTestDatasetResolver resolver =
                new RuleTestDatasetResolver(
                        List.of(
                                provider
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolve(
                                "dataset://uc025/null-result"
                        )
        );
    }
}