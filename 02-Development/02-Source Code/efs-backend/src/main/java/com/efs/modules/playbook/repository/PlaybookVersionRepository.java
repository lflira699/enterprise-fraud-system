package com.efs.modules.playbook.repository;

import com.efs.modules.playbook.entity.PlaybookVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaybookVersionRepository
        extends JpaRepository<PlaybookVersion, UUID> {

    List<PlaybookVersion> findByPlaybookIdOrderByVersionNumberDesc(
            UUID playbookId
    );

    Optional<PlaybookVersion> findByPlaybookIdAndVersionNumber(
            UUID playbookId,
            Integer versionNumber
    );
}