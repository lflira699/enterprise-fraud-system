package com.efs.shared.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CorrelationIdLoggingFilterTest {

    private final CorrelationIdLoggingFilter filter =
            new CorrelationIdLoggingFilter();

    @AfterEach
    void clearMdc() {

        MDC.clear();
    }

    @Test
    void shouldPreserveOpaqueNonEmptyCorrelationId()
            throws Exception {

        String correlationId =
                "uc042-invalid-format";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                CorrelationIdLoggingFilter.HEADER_NAME,
                correlationId
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> requestValue =
                new AtomicReference<>();

        AtomicReference<String> mdcValue =
                new AtomicReference<>();

        FilterChain chain =
                (servletRequest, servletResponse) -> {

                    requestValue.set(
                            ((HttpServletRequest) servletRequest)
                                    .getHeader(
                                            CorrelationIdLoggingFilter.HEADER_NAME
                                    )
                    );

                    mdcValue.set(
                            MDC.get(
                                    CorrelationIdLoggingFilter.MDC_KEY
                            )
                    );
                };

        filter.doFilter(
                request,
                response,
                chain
        );

        assertEquals(
                correlationId,
                requestValue.get()
        );

        assertEquals(
                correlationId,
                response.getHeader(
                        CorrelationIdLoggingFilter.HEADER_NAME
                )
        );

        assertEquals(
                correlationId,
                mdcValue.get()
        );

        assertNull(
                MDC.get(
                        CorrelationIdLoggingFilter.MDC_KEY
                )
        );
    }

    @Test
    void shouldGenerateUuidWhenHeaderIsMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> requestValue =
                new AtomicReference<>();

        AtomicReference<String> mdcValue =
                new AtomicReference<>();

        FilterChain chain =
                (servletRequest, servletResponse) -> {

                    requestValue.set(
                            ((HttpServletRequest) servletRequest)
                                    .getHeader(
                                            CorrelationIdLoggingFilter.HEADER_NAME
                                    )
                    );

                    mdcValue.set(
                            MDC.get(
                                    CorrelationIdLoggingFilter.MDC_KEY
                            )
                    );
                };

        filter.doFilter(
                request,
                response,
                chain
        );

        String generated =
                requestValue.get();

        assertNotNull(
                generated
        );

        assertDoesNotThrow(
                () ->
                        UUID.fromString(
                                generated
                        )
        );

        assertEquals(
                generated,
                response.getHeader(
                        CorrelationIdLoggingFilter.HEADER_NAME
                )
        );

        assertEquals(
                generated,
                mdcValue.get()
        );

        assertNull(
                MDC.get(
                        CorrelationIdLoggingFilter.MDC_KEY
                )
        );
    }

    @Test
    void shouldGenerateUuidWhenHeaderIsBlank()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                CorrelationIdLoggingFilter.HEADER_NAME,
                "   "
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> requestValue =
                new AtomicReference<>();

        FilterChain chain =
                (servletRequest, servletResponse) ->
                        requestValue.set(
                                ((HttpServletRequest) servletRequest)
                                        .getHeader(
                                                CorrelationIdLoggingFilter.HEADER_NAME
                                        )
                        );

        filter.doFilter(
                request,
                response,
                chain
        );

        String generated =
                requestValue.get();

        assertNotNull(
                generated
        );

        assertDoesNotThrow(
                () ->
                        UUID.fromString(
                                generated
                        )
        );

        assertEquals(
                generated,
                response.getHeader(
                        CorrelationIdLoggingFilter.HEADER_NAME
                )
        );
    }

    @Test
    void shouldPreserveExactIncomingValue()
            throws Exception {

        String correlationId =
                "External-System-ABC-123";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                CorrelationIdLoggingFilter.HEADER_NAME,
                correlationId
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> observed =
                new AtomicReference<>();

        FilterChain chain =
                (servletRequest, servletResponse) ->
                        observed.set(
                                ((HttpServletRequest) servletRequest)
                                        .getHeader(
                                                CorrelationIdLoggingFilter.HEADER_NAME
                                        )
                        );

        filter.doFilter(
                request,
                response,
                chain
        );

        assertEquals(
                correlationId,
                observed.get()
        );

        assertEquals(
                correlationId,
                response.getHeader(
                        CorrelationIdLoggingFilter.HEADER_NAME
                )
        );
    }

    @Test
    void shouldRestorePreviousMdcValue()
            throws Exception {

        String previous =
                "previous-correlation";

        String current =
                "current-correlation";

        MDC.put(
                CorrelationIdLoggingFilter.MDC_KEY,
                previous
        );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                CorrelationIdLoggingFilter.HEADER_NAME,
                current
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> inside =
                new AtomicReference<>();

        FilterChain chain =
                (servletRequest, servletResponse) ->
                        inside.set(
                                MDC.get(
                                        CorrelationIdLoggingFilter.MDC_KEY
                                )
                        );

        filter.doFilter(
                request,
                response,
                chain
        );

        assertEquals(
                current,
                inside.get()
        );

        assertEquals(
                previous,
                MDC.get(
                        CorrelationIdLoggingFilter.MDC_KEY
                )
        );
    }
}
