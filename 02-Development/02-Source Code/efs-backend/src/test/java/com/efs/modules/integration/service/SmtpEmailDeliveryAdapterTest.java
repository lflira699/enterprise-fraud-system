package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmtpEmailDeliveryAdapterTest {

    @Mock
    private ObjectProvider<JavaMailSender>
            javaMailSenderProvider;

    @Mock
    private JavaMailSender
            javaMailSender;

    @Test
    void shouldSupportConfiguredEmailDelivery() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        true,
                        "smtp.example.com",
                        587,
                        "",
                        "",
                        "efs@example.com",
                        false
                );

        assertTrue(
                adapter.supports(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldNotSupportWhenDisabled() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        false,
                        "smtp.example.com",
                        587,
                        "",
                        "",
                        "efs@example.com",
                        false
                );

        assertFalse(
                adapter.supports(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldNotSupportUnsupportedChannel() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        true,
                        "smtp.example.com",
                        587,
                        "",
                        "",
                        "efs@example.com",
                        false
                );

        assertFalse(
                adapter.supports(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "SMS"
                )
        );
    }

    @Test
    void shouldRequireAuthenticationCredentialsWhenAuthenticationEnabled() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        true,
                        "smtp.example.com",
                        587,
                        "",
                        "",
                        "efs@example.com",
                        true
                );

        assertFalse(
                adapter.supports(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldNotSupportMissingMandatoryConfiguration() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        true,
                        "",
                        587,
                        "",
                        "",
                        "",
                        false
                );

        assertFalse(
                adapter.supports(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldSendEmailAndReturnConfirmedDelivery() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        true,
                        "smtp.example.com",
                        587,
                        "",
                        "",
                        "efs@example.com",
                        false
                );

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        ExternalNotificationDeliveryRequest request =
                createRequest(
                        organizationId,
                        tenantId
                );

        ExternalNotificationDeliveryResult result =
                adapter.deliver(
                        request
                );

        assertTrue(
                result.isDelivered()
        );

        assertNull(
                result.getDeliveryReference()
        );

        assertNull(
                result.getDeliveryResult()
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(
                        SimpleMailMessage.class
                );

        verify(
                javaMailSender
        ).send(
                captor.capture()
        );

        SimpleMailMessage message =
                captor.getValue();

        assertArrayEquals(
                new String[]{
                        "recipient@example.com"
                },
                message.getTo()
        );

        assertTrue(
                "efs@example.com".equals(
                        message.getFrom()
                )
        );

        assertTrue(
                "Case CASE-1 created".equals(
                        message.getSubject()
                )
        );

        assertTrue(
                "Case CASE-1 has been created.".equals(
                        message.getText()
                )
        );
    }

    @Test
    void shouldPropagateMailFailure() {

        SmtpEmailDeliveryAdapter adapter =
                createAdapter(
                        true,
                        "smtp.example.com",
                        587,
                        "",
                        "",
                        "efs@example.com",
                        false
                );

        ExternalNotificationDeliveryRequest request =
                createRequest(
                        UUID.randomUUID(),
                        UUID.randomUUID()
                );

        doThrow(
                new MailSendException(
                        "SMTP failure"
                )
        ).when(
                javaMailSender
        ).send(
                any(
                        SimpleMailMessage.class
                )
        );

        assertThrows(
                MailSendException.class,
                () ->
                        adapter.deliver(
                                request
                        )
        );
    }

    private SmtpEmailDeliveryAdapter createAdapter(
            boolean enabled,
            String host,
            int port,
            String username,
            String password,
            String fromAddress,
            boolean authenticationRequired) {

        when(
                javaMailSenderProvider
                        .getIfUnique()
        ).thenReturn(
                javaMailSender
        );

        return new SmtpEmailDeliveryAdapter(
                javaMailSenderProvider,
                enabled,
                host,
                port,
                username,
                password,
                fromAddress,
                authenticationRequired
        );
    }

    private ExternalNotificationDeliveryRequest createRequest(
            UUID organizationId,
            UUID tenantId) {

        ExternalNotificationDeliveryRequest request =
                new ExternalNotificationDeliveryRequest();

        request.setNotificationDeliveryId(
                UUID.randomUUID()
        );

        request.setCorrelationId(
                UUID.randomUUID()
        );

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setChannel(
                "EMAIL"
        );

        request.setRecipientUserId(
                UUID.randomUUID()
        );

        request.setDestination(
                "recipient@example.com"
        );

        request.setSubject(
                "Case CASE-1 created"
        );

        request.setBody(
                "Case CASE-1 has been created."
        );

        return request;
    }
}