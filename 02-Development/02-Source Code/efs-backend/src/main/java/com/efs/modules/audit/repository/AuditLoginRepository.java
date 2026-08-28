package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLoginRepository
        extends JpaRepository<AuditLogin, UUID> {

    List<AuditLogin> findByUserIdOrderByLoginTimestampDesc(
            UUID userId
    );

    List<AuditLogin> findByLoginResultOrderByLoginTimestampDesc(
            String loginResult
    );

    List<AuditLogin> findByIpAddressOrderByLoginTimestampDesc(
            InetAddress ipAddress
    );

    List<AuditLogin> findByAuthenticationMethodOrderByLoginTimestampDesc(
            String authenticationMethod
    );
}