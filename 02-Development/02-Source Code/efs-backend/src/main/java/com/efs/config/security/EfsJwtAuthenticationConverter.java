package com.efs.config.security;

import com.efs.shared.security.SecurityContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EfsJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final EfsJwtSecurityContextMapper securityContextMapper;

    public EfsJwtAuthenticationConverter(
            EfsJwtSecurityContextMapper securityContextMapper) {

        this.securityContextMapper =
                securityContextMapper;
    }

    @Override
    public AbstractAuthenticationToken convert(
            Jwt jwt) {

        Objects.requireNonNull(
                jwt,
                "jwt is required"
        );

        SecurityContext context =
                securityContextMapper.map(
                        jwt
                );

        return new UsernamePasswordAuthenticationToken(
                context,
                null,
                List.of()
        );
    }
}