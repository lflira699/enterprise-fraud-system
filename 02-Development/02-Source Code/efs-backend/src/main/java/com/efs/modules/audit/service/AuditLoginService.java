package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.dto.AuditLoginResponse;
import com.efs.modules.audit.entity.AuditLogin;
import com.efs.modules.audit.mapper.AuditLoginMapper;
import com.efs.modules.audit.repository.AuditLoginRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditLoginService
        implements AuditLoginServiceInterface {

    private final AuditLoginRepository auditLoginRepository;
    private final AuditLoginMapper auditLoginMapper;

    public AuditLoginService(
            AuditLoginRepository auditLoginRepository,
            AuditLoginMapper auditLoginMapper) {

        this.auditLoginRepository =
                auditLoginRepository;

        this.auditLoginMapper =
                auditLoginMapper;
    }

    @Override
    @Transactional
    public AuditLoginResponse createAuditLogin(
            AuditLoginRequest request) {

        AuditLogin auditLogin =
                auditLoginMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        auditLogin.setLoginTimestamp(now);
        auditLogin.setCreatedAt(now);

        AuditLogin savedAuditLogin =
                auditLoginRepository.save(auditLogin);

        return auditLoginMapper.toResponse(
                savedAuditLogin
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLoginResponse getAuditLoginById(
            UUID loginId) {

        AuditLogin auditLogin =
                auditLoginRepository
                        .findById(loginId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Audit login not found: "
                                                + loginId
                                )
                        );

        return auditLoginMapper.toResponse(
                auditLogin
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLoginResponse> getAuditLoginsByUserId(
            UUID userId) {

        return auditLoginRepository
                .findByUserIdOrderByLoginTimestampDesc(
                        userId
                )
                .stream()
                .map(auditLoginMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLoginResponse> getAuditLoginsByLoginResult(
            String loginResult) {

        return auditLoginRepository
                .findByLoginResultOrderByLoginTimestampDesc(
                        loginResult
                )
                .stream()
                .map(auditLoginMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLoginResponse> getAuditLoginsByIpAddress(
            String ipAddress) {

        try {
            InetAddress address =
                    InetAddress.getByName(
                            ipAddress
                    );

            return auditLoginRepository
                    .findByIpAddressOrderByLoginTimestampDesc(
                            address
                    )
                    .stream()
                    .map(auditLoginMapper::toResponse)
                    .toList();

        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException(
                    "Invalid IP address: "
                            + ipAddress,
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLoginResponse>
    getAuditLoginsByAuthenticationMethod(
            String authenticationMethod) {

        return auditLoginRepository
                .findByAuthenticationMethodOrderByLoginTimestampDesc(
                        authenticationMethod
                )
                .stream()
                .map(auditLoginMapper::toResponse)
                .toList();
    }
}