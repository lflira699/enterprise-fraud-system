package com.efs.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class EfsSecurityConfiguration {

    @Bean
    public JwtDecoder efsJwtDecoder(
            @Value("${efs.security.jwt.issuer-uri}")
            String issuerUri,
            @Value("${efs.security.jwt.jwk-set-uri}")
            String jwkSetUri,
            @Value("${efs.security.jwt.audience}")
            String audience) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withJwkSetUri(
                                jwkSetUri
                        )
                        .build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators
                        .createDefaultWithIssuer(
                                issuerUri
                        );

        OAuth2TokenValidator<Jwt> audienceValidator =
                new EfsJwtAudienceValidator(
                        audience
                );

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        issuerValidator,
                        audienceValidator
                )
        );

        return decoder;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "efs.security",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SecurityFilterChain efsSecurityFilterChain(
            HttpSecurity http,
            EfsJwtAuthenticationConverter authenticationConverter)
            throws Exception {

        http
                .csrf(
                        csrf ->
                                csrf.disable()
                )
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        .requestMatchers(
                                                "/actuator/health",
                                                "/actuator/info"
                                        )
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                )
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt ->
                                                jwt.jwtAuthenticationConverter(
                                                        authenticationConverter
                                                )
                                )
                );

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "efs.security",
            name = "enabled",
            havingValue = "false"
    )
    public SecurityFilterChain efsTestSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(
                        csrf ->
                                csrf.disable()
                )
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        .anyRequest()
                                        .permitAll()
                );

        return http.build();
    }
}