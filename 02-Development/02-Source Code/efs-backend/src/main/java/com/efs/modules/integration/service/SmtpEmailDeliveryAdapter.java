package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class SmtpEmailDeliveryAdapter
        implements ExternalNotificationDeliveryAdapter {

    private static final String EMAIL_CHANNEL =
            "EMAIL";

    private final JavaMailSender javaMailSender;

    private final boolean enabled;

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    private final String fromAddress;

    private final boolean authenticationRequired;

    public SmtpEmailDeliveryAdapter(
            ObjectProvider<JavaMailSender>
                    javaMailSenderProvider,
            @Value(
                    "${efs.integration.notification.smtp.enabled:false}"
            )
            boolean enabled,
            @Value(
                    "${spring.mail.host:}"
            )
            String host,
            @Value(
                    "${spring.mail.port:25}"
            )
            int port,
            @Value(
                    "${spring.mail.username:}"
            )
            String username,
            @Value(
                    "${spring.mail.password:}"
            )
            String password,
            @Value(
                    "${efs.integration.notification.smtp.from:}"
            )
            String fromAddress,
            @Value(
                    "${spring.mail.properties.mail.smtp.auth:false}"
            )
            boolean authenticationRequired) {

        if (javaMailSenderProvider == null) {
            throw new IllegalArgumentException(
                    "JavaMailSender provider is required"
            );
        }

        this.javaMailSender =
                javaMailSenderProvider.getIfUnique();

        this.enabled =
                enabled;

        this.host =
                host;

        this.port =
                port;

        this.username =
                username;

        this.password =
                password;

        this.fromAddress =
                fromAddress;

        this.authenticationRequired =
                authenticationRequired;
    }

    @Override
    public boolean supports(
            UUID organizationId,
            UUID tenantId,
            String channel) {

        if (!enabled) {
            return false;
        }

        if (organizationId == null
                || tenantId == null) {

            return false;
        }

        if (!EMAIL_CHANNEL.equals(
                channel
        )) {

            return false;
        }

        if (javaMailSender == null) {
            return false;
        }

        if (!StringUtils.hasText(
                host
        )) {

            return false;
        }

        if (port <= 0
                || port > 65_535) {

            return false;
        }

        if (!StringUtils.hasText(
                fromAddress
        )) {

            return false;
        }

        if (authenticationRequired
                && (!StringUtils.hasText(
                        username
                )
                || !StringUtils.hasText(
                        password
                ))) {

            return false;
        }

        return true;
    }

    @Override
    public ExternalNotificationDeliveryResult deliver(
            ExternalNotificationDeliveryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "External notification delivery request is required"
            );
        }

        if (!supports(
                request.getOrganizationId(),
                request.getTenantId(),
                request.getChannel()
        )) {

            throw new IllegalStateException(
                    "SMTP email delivery adapter is unavailable for request context"
            );
        }

        if (!StringUtils.hasText(
                request.getDestination()
        )) {

            throw new IllegalArgumentException(
                    "Email destination is required"
            );
        }

        if (!StringUtils.hasText(
                request.getBody()
        )) {

            throw new IllegalArgumentException(
                    "Email body is required"
            );
        }

        SimpleMailMessage mailMessage =
                new SimpleMailMessage();

        mailMessage.setFrom(
                fromAddress
        );

        mailMessage.setTo(
                request.getDestination()
        );

        if (request.getSubject() != null) {
            mailMessage.setSubject(
                    request.getSubject()
            );
        }

        mailMessage.setText(
                request.getBody()
        );

        javaMailSender.send(
                mailMessage
        );

        ExternalNotificationDeliveryResult result =
                new ExternalNotificationDeliveryResult();

        result.setDelivered(
                true
        );

        result.setDeliveryReference(
                null
        );

        result.setDeliveryResult(
                null
        );

        return result;
    }
}