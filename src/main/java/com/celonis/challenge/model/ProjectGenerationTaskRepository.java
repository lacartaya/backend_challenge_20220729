package com.celonis.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {

    List<ProjectGenerationTask> findByStatusAndCreationDateBefore(TaskStatus status, Date threshold);
}