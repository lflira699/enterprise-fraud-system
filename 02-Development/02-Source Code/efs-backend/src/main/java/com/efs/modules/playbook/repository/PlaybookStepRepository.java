package com.efs.modules.playbook.repository;

import com.efs.modules.playbook.entity.PlaybookStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaybookStepRepository
        extends JpaRepository<PlaybookStep, UUID> {

    List<PlaybookStep> findByPlaybookVersionIdOrderByStepOrderAsc(
            UUID playbookVersionId
    );
}
