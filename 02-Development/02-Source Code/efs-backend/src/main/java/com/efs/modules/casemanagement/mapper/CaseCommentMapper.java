package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseCommentRequest;
import com.efs.modules.casemanagement.dto.CaseCommentResponse;
import com.efs.modules.casemanagement.entity.CaseComment;
import org.springframework.stereotype.Component;

@Component
public class CaseCommentMapper {

    public CaseComment toEntity(
            CaseCommentRequest request) {

        CaseComment comment =
                new CaseComment();

        comment.setCommentType(
                request.getCommentType()
        );

        comment.setCommentText(
                request.getCommentText()
        );

        comment.setVisibility(
                request.getVisibility()
        );

        comment.setCreatedBy(
                request.getCreatedBy()
        );

        return comment;
    }

    public CaseCommentResponse toResponse(
            CaseComment comment) {

        CaseCommentResponse response =
                new CaseCommentResponse();

        response.setCommentId(
                comment.getCommentId()
        );

        response.setCaseId(
                comment.getCaseId()
        );

        response.setCommentType(
                comment.getCommentType()
        );

        response.setCommentText(
                comment.getCommentText()
        );

        response.setVisibility(
                comment.getVisibility()
        );

        response.setCreatedBy(
                comment.getCreatedBy()
        );

        response.setCreatedAt(
                comment.getCreatedAt()
        );

        return response;
    }
}