package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseTaskRequest;
import com.efs.modules.casemanagement.dto.CaseTaskResponse;
import com.efs.modules.casemanagement.entity.CaseTask;
import org.springframework.stereotype.Component;

@Component
public class CaseTaskMapper {

    public CaseTask toEntity(
            CaseTaskRequest request) {

        CaseTask task =
                new CaseTask();

        task.setTaskName(
                request.getTaskName()
        );

        task.setTaskDescription(
                request.getTaskDescription()
        );

        task.setAssignedTo(
                request.getAssignedTo()
        );

        task.setPriority(
                request.getPriority()
        );

        task.setStatus(
                request.getStatus()
        );

        task.setDueDate(
                request.getDueDate()
        );

        return task;
    }

    public CaseTaskResponse toResponse(
            CaseTask task) {

        CaseTaskResponse response =
                new CaseTaskResponse();

        response.setTaskId(
                task.getTaskId()
        );

        response.setCaseId(
                task.getCaseId()
        );

        response.setTaskName(
                task.getTaskName()
        );

        response.setTaskDescription(
                task.getTaskDescription()
        );

        response.setAssignedTo(
                task.getAssignedTo()
        );

        response.setPriority(
                task.getPriority()
        );

        response.setStatus(
                task.getStatus()
        );

        response.setDueDate(
                task.getDueDate()
        );

        response.setCompletedAt(
                task.getCompletedAt()
        );

        response.setCreatedAt(
                task.getCreatedAt()
        );

        return response;
    }
}