package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.enums.TaskTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class TaskService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;
    private final FileService fileService;
    private final ScheduledExecutorService scheduler;
    private final ConcurrentMap<String, ScheduledFuture<?>> runningCounters = new ConcurrentHashMap<>();

    @Autowired
    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       FileService fileService) {
        this(projectGenerationTaskRepository, fileService, Executors.newScheduledThreadPool(4));
    }

    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       FileService fileService,
                       ScheduledExecutorService scheduler) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
        this.fileService = fileService;
        this.scheduler = scheduler;
    }

    public List<ProjectGenerationTask> listTasks() {
        return projectGenerationTaskRepository.findAll();
    }

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        if (projectGenerationTask.getId() == null || projectGenerationTask.getId().isBlank()) {
            projectGenerationTask.setId(UUID.randomUUID().toString());
        }
        projectGenerationTask.setCreationDate(new Date());

        if (projectGenerationTask.getStatus() == null) {
            projectGenerationTask.setStatus(TaskStatusEnum.PENDING);
        }
        if (projectGenerationTask.getProgress() == null) {
            projectGenerationTask.setProgress(0);
        }
        if (projectGenerationTask.getType() == null) {
            projectGenerationTask.setType(TaskTypeEnum.ZIP_GENERATION);
        }

        return projectGenerationTaskRepository.save(projectGenerationTask);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return get(taskId);
    }

    public ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask) {
        ProjectGenerationTask existing = get(taskId);
        existing.setName(projectGenerationTask.getName());
        existing.setCreationDate(projectGenerationTask.getCreationDate());
        return projectGenerationTaskRepository.save(existing);
    }

    public void delete(String taskId) {
        cancelCounter(taskId);
        projectGenerationTaskRepository.deleteById(taskId);
    }

    public void executeTask(String taskId) {
        ClassPathResource zipResource = new ClassPathResource("challenge.zip");
        if (!zipResource.exists()) {
            throw new InternalException("Zip file not found in classpath at /challenge.zip");
        }
        try (InputStream is = zipResource.getInputStream()) {
            fileService.storeResult(taskId, is, "challenge.zip");
        } catch (IOException e) {
            throw new InternalException(e);
        }
    }

    public TaskProgressDto startCounter(String taskId, int x, int y) {
        if (y < x) {
            throw new InternalException("Invalid range: y must be >= x");
        }

        ProjectGenerationTask task = get(taskId);

        if (runningCounters.containsKey(taskId) || task.getStatus() == TaskStatusEnum.RUNNING) {
            throw new InternalException("Task is already running");
        }

        task.setType(TaskTypeEnum.COUNTER);
        task.setStartValue(x);
        task.setTargetValue(y);
        task.setCurrentValue(x);
        task.setProgress(percentage(x, x, y));
        task.setStatus(TaskStatusEnum.RUNNING);
        projectGenerationTaskRepository.save(task);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                ProjectGenerationTask t = projectGenerationTaskRepository.findById(taskId).orElse(null);
                if (t == null) {
                    cancelFuture(taskId);
                    return;
                }
                if (t.getStatus() != TaskStatusEnum.RUNNING) {
                    cancelFuture(taskId);
                    return;
                }

                int cur = Optional.ofNullable(t.getCurrentValue()).orElse(x);
                int target = Optional.ofNullable(t.getTargetValue()).orElse(y);

                if (cur >= target) {
                    t.setCurrentValue(target);
                    t.setProgress(100);
                    t.setStatus(TaskStatusEnum.COMPLETED);
                    projectGenerationTaskRepository.save(t);
                    cancelFuture(taskId);
                    return;
                }

                cur += 1;
                t.setCurrentValue(cur);
                t.setProgress(percentage(cur, x, target));
                projectGenerationTaskRepository.save(t);

            } catch (Exception ex) {
                ProjectGenerationTask t = projectGenerationTaskRepository.findById(taskId).orElse(null);
                if (t != null) {
                    t.setStatus(TaskStatusEnum.FAILED);
                    t.setProgress(0);
                    projectGenerationTaskRepository.save(t);
                }
                cancelFuture(taskId);
            }
        }, 0, 1, TimeUnit.SECONDS);

        runningCounters.put(taskId, future);

        return toProgressDto(task);
    }

    public TaskProgressDto getCounterProgress(String taskId) {
        ProjectGenerationTask t = get(taskId);
        return toProgressDto(t);
    }

    public TaskProgressDto cancelCounter(String taskId) {
        ProjectGenerationTask t = get(taskId);
        if (t.getStatus() == TaskStatusEnum.RUNNING) {
            cancelFuture(taskId);
            t.setStatus(TaskStatusEnum.CANCELED);
            projectGenerationTaskRepository.save(t);
        }
        return toProgressDto(t);
    }

    private void cancelFuture(String taskId) {
        ScheduledFuture<?> f = runningCounters.remove(taskId);
        if (f != null) f.cancel(true);
    }

    private static int percentage(int cur, int x, int y) {
        int denom = Math.max(1, y - x);
        int p = (int) Math.round(((cur - x) * 100.0) / denom);
        if (p < 0) p = 0;
        if (p > 100) p = 100;
        return p;
    }

    private ProjectGenerationTask get(String taskId) {
        Optional<ProjectGenerationTask> projectGenerationTask = projectGenerationTaskRepository.findById(taskId);
        return projectGenerationTask.orElseThrow(NotFoundException::new);
    }

    public static class TaskProgressDto {
        public String status;
        public int progress;
        public int current;
        public int target;
        public String message;
    }

    private TaskProgressDto toProgressDto(ProjectGenerationTask t) {
        TaskProgressDto dto = new TaskProgressDto();
        dto.status = t.getStatus() == null ? TaskStatusEnum.PENDING.name() : t.getStatus().name();
        dto.progress = t.getProgress() == null ? 0 : t.getProgress();
        dto.current = t.getCurrentValue() == null ? 0 : t.getCurrentValue();
        dto.target = t.getTargetValue() == null ? 0 : t.getTargetValue();
        dto.message = null;
        return dto;
    }
}
