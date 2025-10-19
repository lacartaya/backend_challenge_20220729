package com.celonis.challenge.controllers;

import com.celonis.challenge.api.TasksApi;
import com.celonis.challenge.api.model.*;
import com.celonis.challenge.mappers.TaskMapper;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TaskController implements TasksApi {

    private final TaskService taskService;
    private final FileService fileService;

    public TaskController(TaskService taskService, FileService fileService) {
        this.taskService = taskService;
        this.fileService = fileService;
    }

    // ---------- Legacy CRUD + ZIP execution ----------

    @Override
    public ResponseEntity<List<Task>> listTasks() {
        List<ProjectGenerationTask> all = taskService.listTasks();
        List<Task> dtos = all.stream().map(TaskMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<Task> createTask(@Valid TaskCreate body) {
        ProjectGenerationTask toCreate = TaskMapper.fromCreate(body);
        ProjectGenerationTask created = taskService.createTask(toCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskMapper.toDto(created));
    }

    @Override
    public ResponseEntity<Task> getTask(String taskId) {
        ProjectGenerationTask found = taskService.getTask(taskId);
        return ResponseEntity.ok(TaskMapper.toDto(found));
    }

    @Override
    public ResponseEntity<Task> updateTask(String taskId, @Valid TaskUpdate body) {
        ProjectGenerationTask current = taskService.getTask(taskId);
        TaskMapper.applyUpdate(body, current);
        ProjectGenerationTask saved = taskService.update(taskId, current);
        return ResponseEntity.ok(TaskMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<Void> deleteTask(String taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> executeTask(String taskId) {
        taskService.executeTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Resource> getResult(String taskId) {
        ResponseEntity<FileSystemResource> res = fileService.getTaskResult(taskId);
        return ResponseEntity.status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.getBody());
    }

    // ---------- New: Counter endpoints ----------

    @Override
    public ResponseEntity<TaskProgress> startCounter(String taskId, @Valid CounterConfig counterConfig) {
        // Spring validará que el body existe por OpenAPI (required=true). Aun así, protegemos:
        Objects.requireNonNull(counterConfig, "CounterConfig body is required");
        Integer x = counterConfig.getX();
        Integer y = counterConfig.getY();
        Objects.requireNonNull(x, "x is required");
        Objects.requireNonNull(y, "y is required");

        TaskService.TaskProgressDto dto = taskService.startCounter(taskId, x, y);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toApiProgress(dto));
    }

    @Override
    public ResponseEntity<TaskProgress> getCounterProgress(String taskId) {
        TaskService.TaskProgressDto dto = taskService.getCounterProgress(taskId);
        return ResponseEntity.ok(toApiProgress(dto));
    }

    @Override
    public ResponseEntity<TaskProgress> cancelCounter(String taskId) {
        TaskService.TaskProgressDto dto = taskService.cancelCounter(taskId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toApiProgress(dto));
    }

    // ---------- Mappers privados ----------

    private static TaskProgress toApiProgress(TaskService.TaskProgressDto s) {
        TaskProgress p = new TaskProgress();
        p.setStatus(TaskProgress.StatusEnum.fromValue(s.status));
        p.setProgress(s.progress);
        p.setCurrent(s.current);
        p.setTarget(s.target);
        p.setMessage(s.message);
        return p;
    }
}
