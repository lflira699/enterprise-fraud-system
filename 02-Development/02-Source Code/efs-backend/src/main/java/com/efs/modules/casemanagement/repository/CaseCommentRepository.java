package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseCommentRepository
        extends JpaRepository<CaseComment, UUID> {

    Optional<CaseComment> findByCommentId(
            UUID commentId
    );

    List<CaseComment> findByCaseIdOrderByCreatedAtDesc(
            UUID caseId
    );

    List<CaseComment> findByCaseIdAndCommentTypeOrderByCreatedAtDesc(
            UUID caseId,
            String commentType
    );

    List<CaseComment> findByCreatedByOrderByCreatedAtDesc(
            UUID createdBy
    );
}