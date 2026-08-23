package com.efs.modules.playbook.repository;

import com.efs.modules.playbook.entity.Playbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaybookRepository
        extends JpaRepository<Playbook, UUID> {

    Optional<Playbook> findByPlaybookCode(String playbookCode);

    List<Playbook> findByStatusOrderByPlaybookNameAsc(String status);
}