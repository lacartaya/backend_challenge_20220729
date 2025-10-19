package com.celonis.challenge.controllers;

import com.celonis.challenge.api.TasksApi;
import com.celonis.challenge.api.model.*;
import com.celonis.challenge.mappers.TaskMapper;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController implements TasksApi {

    private final TaskService taskService;
    private final FileService fileService;

    @Override
    public ResponseEntity<List<Task>> listTasks() {
        log.info("Listing all tasks available");
        final var all = taskService.listTasks();
        final var dtos = all.stream().map(TaskMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<Task> createTask(@Valid TaskCreate body) {
        log.info("Creating new task {}", body.getName());
        final var toCreate = TaskMapper.fromCreate(body);
        final var created = taskService.createTask(toCreate);
        return ResponseEntity.status(CREATED).body(TaskMapper.toDto(created));
    }

    @Override
    public ResponseEntity<Task> getTask(String taskId) {
        log.info("Getting task {}", taskId);
        final var found = taskService.getTask(taskId);
        return ResponseEntity.ok(TaskMapper.toDto(found));
    }

    @Override
    public ResponseEntity<Task> updateTask(String taskId, @Valid TaskUpdate body) {
        log.info("Updating task {}", taskId);
        final var current = taskService.getTask(taskId);
        TaskMapper.applyUpdate(body, current);
        final var saved = taskService.update(taskId, current);
        return ResponseEntity.ok(TaskMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<Void> deleteTask(String taskId) {
        log.info("Deleting task {}", taskId);
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> executeTask(String taskId) {
        log.info("Executing task {}", taskId);
        taskService.executeTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Resource> getResult(String taskId) {
        log.info("Downloading task {}", taskId);
        final var res = fileService.getTaskResult(taskId);
        return ResponseEntity.status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.getBody());
    }


    @Override
    public ResponseEntity<TaskProgress> startCounter(String taskId, @Valid CounterConfig counterConfig) {
        log.info("Starting task's counter {}", taskId);
        Objects.requireNonNull(counterConfig, "CounterConfig body is required");
        final var x = counterConfig.getX();
        final var y = counterConfig.getY();
        Objects.requireNonNull(x, "x is required");
        Objects.requireNonNull(y, "y is required");

        final var dto = taskService.startCounter(taskId, x, y);
        return ResponseEntity.status(ACCEPTED).body(toApiProgress(dto));
    }

    @Override
    public ResponseEntity<TaskProgress> getCounterProgress(String taskId) {
        log.info("Getting Counter progress for task {}", taskId);
        final var dto = taskService.getCounterProgress(taskId);
        return ResponseEntity.ok(toApiProgress(dto));
    }

    @Override
    public ResponseEntity<TaskProgress> cancelCounter(String taskId) {
        log.info("Cancelling Counter progress for task {}", taskId);
        final var dto = taskService.cancelCounter(taskId);
        return ResponseEntity.status(ACCEPTED).body(toApiProgress(dto));
    }

    private static TaskProgress toApiProgress(TaskService.TaskProgressDto s) {
        final var p = new TaskProgress();
        p.setStatus(TaskProgress.StatusEnum.fromValue(s.status));
        p.setProgress(s.progress);
        p.setCurrent(s.current);
        p.setTarget(s.target);
        p.setMessage(s.message);
        return p;
    }
}
