package com.celonis.challenge.repository;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.enums.TaskStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {

    List<ProjectGenerationTask> findByStatusAndCreationDateBefore(TaskStatusEnum status, Date threshold);
}