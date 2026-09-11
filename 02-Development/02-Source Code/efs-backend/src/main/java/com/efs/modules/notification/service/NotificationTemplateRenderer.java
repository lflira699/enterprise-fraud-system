package com.efs.modules.notification.service;

import com.efs.modules.notification.dto.RenderedNotificationContent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NotificationTemplateRenderer {

    private static final Pattern PLACEHOLDER_PATTERN =
            Pattern.compile(
                    "\\{\\{([^{}]+)\\}\\}"
            );

    public RenderedNotificationContent render(
            String subjectTemplate,
            String bodyTemplate,
            Map<String, Object> templateParameters) {

        if (bodyTemplate == null
                || bodyTemplate.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification body template is required"
            );
        }

        if (templateParameters == null) {

            throw new IllegalArgumentException(
                    "Notification template parameters are required"
            );
        }

        String renderedSubject =
                subjectTemplate == null
                        ? null
                        : renderTemplate(
                                "subject",
                                subjectTemplate,
                                templateParameters
                        );

        String renderedBody =
                renderTemplate(
                        "body",
                        bodyTemplate,
                        templateParameters
                );

        return new RenderedNotificationContent(
                renderedSubject,
                renderedBody
        );
    }

    private String renderTemplate(
            String templatePart,
            String template,
            Map<String, Object> templateParameters) {

        Matcher matcher =
                PLACEHOLDER_PATTERN.matcher(
                        template
                );

        StringBuffer rendered =
                new StringBuffer();

        while (matcher.find()) {

            String parameterName =
                    matcher.group(1);

            if (parameterName.isBlank()) {

                throw invalidPlaceholder(
                        templatePart,
                        parameterName
                );
            }

            if (!templateParameters.containsKey(
                    parameterName
            )) {

                throw new IllegalArgumentException(
                        "Missing notification template parameter: "
                                + parameterName
                );
            }

            Object parameterValue =
                    templateParameters.get(
                            parameterName
                    );

            if (parameterValue == null) {

                throw new IllegalArgumentException(
                        "Notification template parameter cannot be null: "
                                + parameterName
                );
            }

            if (!isScalar(
                    parameterValue
            )) {

                throw new IllegalArgumentException(
                        "Notification template parameter must be scalar: "
                                + parameterName
                );
            }

            matcher.appendReplacement(
                    rendered,
                    Matcher.quoteReplacement(
                            String.valueOf(
                                    parameterValue
                            )
                    )
            );
        }

        matcher.appendTail(
                rendered
        );

        String result =
                rendered.toString();

        if (result.contains("{{")
                || result.contains("}}")) {

            throw new IllegalArgumentException(
                    "Unresolved notification template placeholder in "
                            + templatePart
            );
        }

        return result;
    }

    private boolean isScalar(
            Object value) {

        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean;
    }

    private IllegalArgumentException invalidPlaceholder(
            String templatePart,
            String parameterName) {

        return new IllegalArgumentException(
                "Invalid notification template placeholder in "
                        + templatePart
                        + ": "
                        + parameterName
        );
    }
}