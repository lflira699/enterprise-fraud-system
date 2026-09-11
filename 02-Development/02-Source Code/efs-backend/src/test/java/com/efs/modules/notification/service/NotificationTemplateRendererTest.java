package com.efs.modules.notification.service;

import com.efs.modules.notification.dto.RenderedNotificationContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationTemplateRendererTest {

    private NotificationTemplateRenderer renderer;

    @BeforeEach
    void setUp() {

        renderer =
                new NotificationTemplateRenderer();
    }

    @Test
    void shouldRenderApprovedPlaceholderSyntax() {

        RenderedNotificationContent result =
                renderer.render(
                        "Case {{caseNumber}} created",
                        "Case {{caseNumber}} has been created.",
                        Map.of(
                                "caseNumber",
                                "CASE-001245"
                        )
                );

        assertEquals(
                "Case CASE-001245 created",
                result.subject()
        );

        assertEquals(
                "Case CASE-001245 has been created.",
                result.body()
        );
    }

    @Test
    void shouldRenderScalarNumberAndBooleanParameters() {

        RenderedNotificationContent result =
                renderer.render(
                        null,
                        "Amount {{amount}} approved={{approved}}",
                        Map.of(
                                "amount",
                                new BigDecimal("125.50"),
                                "approved",
                                true
                        )
                );

        assertNull(
                result.subject()
        );

        assertEquals(
                "Amount 125.50 approved=true",
                result.body()
        );
    }

    @Test
    void shouldIgnoreUnusedParameters() {

        RenderedNotificationContent result =
                renderer.render(
                        "Case {{caseNumber}}",
                        "Status changed",
                        Map.of(
                                "caseNumber",
                                "CASE-100",
                                "unusedParameter",
                                "IGNORED"
                        )
                );

        assertEquals(
                "Case CASE-100",
                result.subject()
        );

        assertEquals(
                "Status changed",
                result.body()
        );
    }

    @Test
    void shouldPreserveReplacementSpecialCharacters() {

        RenderedNotificationContent result =
                renderer.render(
                        null,
                        "Value: {{value}}",
                        Map.of(
                                "value",
                                "$100\\reference"
                        )
                );

        assertEquals(
                "Value: $100\\reference",
                result.body()
        );
    }

    @Test
    void shouldRejectMissingRequiredParameter() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Case {{caseNumber}}",
                                Map.of()
                        )
        );
    }

    @Test
    void shouldRejectNullRequiredParameter() {

        Map<String, Object> parameters =
                new HashMap<>();

        parameters.put(
                "caseNumber",
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Case {{caseNumber}}",
                                parameters
                        )
        );
    }

    @Test
    void shouldRejectNonScalarRequiredParameter() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Case {{caseNumber}}",
                                Map.of(
                                        "caseNumber",
                                        List.of(
                                                "CASE-1",
                                                "CASE-2"
                                        )
                                )
                        )
        );
    }

    @Test
    void shouldRejectBlankBodyTemplate() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                "Subject",
                                "   ",
                                Map.of()
                        )
        );
    }

    @Test
    void shouldRejectNullBodyTemplate() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                "Subject",
                                null,
                                Map.of()
                        )
        );
    }

    @Test
    void shouldRejectNullTemplateParameters() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Body",
                                null
                        )
        );
    }

    @Test
    void shouldRejectMalformedUnresolvedPlaceholder() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Case {{caseNumber",
                                Map.of(
                                        "caseNumber",
                                        "CASE-1"
                                )
                        )
        );
    }

    @Test
    void shouldRejectPlaceholderProducedByParameterValue() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Value {{value}}",
                                Map.of(
                                        "value",
                                        "{{nested}}",
                                        "nested",
                                        "NOT_ALLOWED"
                                )
                        )
        );
    }

    @Test
    void shouldNotResolveNestedPropertyNotation() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        renderer.render(
                                null,
                                "Customer {{customer.name}}",
                                Map.of(
                                        "customer",
                                        Map.of(
                                                "name",
                                                "Alice"
                                        )
                                )
                        )
        );
    }
}