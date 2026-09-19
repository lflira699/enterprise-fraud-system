package com.efs.shared.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdLoggingFilter
        extends OncePerRequestFilter {

    public static final String HEADER_NAME =
            "X-Correlation-ID";

    public static final String MDC_KEY =
            "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId =
                resolveCorrelationId(
                        request.getHeader(
                                HEADER_NAME
                        )
                );

        HttpServletRequest wrappedRequest =
                new CorrelationIdRequestWrapper(
                        request,
                        correlationId
                );

        response.setHeader(
                HEADER_NAME,
                correlationId
        );

        String previousCorrelationId =
                MDC.get(
                        MDC_KEY
                );

        MDC.put(
                MDC_KEY,
                correlationId
        );

        try {

            filterChain.doFilter(
                    wrappedRequest,
                    response
            );
        }
        finally {

            if (previousCorrelationId == null) {

                MDC.remove(
                        MDC_KEY
                );
            }
            else {

                MDC.put(
                        MDC_KEY,
                        previousCorrelationId
                );
            }
        }
    }

    private String resolveCorrelationId(
            String candidate) {

        if (
                candidate == null ||
                candidate.trim().isEmpty()
        ) {

            return UUID.randomUUID()
                    .toString();
        }

        return candidate;
    }

    private static final class
            CorrelationIdRequestWrapper
            extends HttpServletRequestWrapper {

        private final String correlationId;

        private CorrelationIdRequestWrapper(
                HttpServletRequest request,
                String correlationId) {

            super(request);

            this.correlationId =
                    correlationId;
        }

        @Override
        public String getHeader(
                String name) {

            if (
                    HEADER_NAME.equalsIgnoreCase(
                            name
                    )
            ) {

                return correlationId;
            }

            return super.getHeader(
                    name
            );
        }

        @Override
        public Enumeration<String> getHeaders(
                String name) {

            if (
                    HEADER_NAME.equalsIgnoreCase(
                            name
                    )
            ) {

                return Collections.enumeration(
                        List.of(
                                correlationId
                        )
                );
            }

            return super.getHeaders(
                    name
            );
        }

        @Override
        public Enumeration<String> getHeaderNames() {

            Enumeration<String> original =
                    super.getHeaderNames();

            List<String> names =
                    new ArrayList<>();

            if (original != null) {

                while (
                        original.hasMoreElements()
                ) {

                    String name =
                            original.nextElement();

                    if (
                            !HEADER_NAME.equalsIgnoreCase(
                                    name
                            )
                    ) {

                        names.add(
                                name
                        );
                    }
                }
            }

            names.add(
                    HEADER_NAME
            );

            return Collections.enumeration(
                    names
            );
        }
    }
}
