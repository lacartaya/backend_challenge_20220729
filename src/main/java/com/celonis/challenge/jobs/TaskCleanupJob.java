package com.celonis.challenge.jobs;

import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCleanupJob {

    private final ProjectGenerationTaskRepository repo;

    @Value("${tasks.cleanup.enabled:true}")
    private boolean enabled;

    @Value("${tasks.cleanup.retention-days:7}")
    private int retentionDays;

    // @Scheduled(fixedDelay = 15000)
    @Scheduled(cron = "${tasks.cleanup.cron:0 0 3 * * *}")
    @Transactional
    public void cleanupOldPendingTasks() {
        if (!enabled) return;

        final var threshold = computeThreshold();
        final var candidates = findOldPending(threshold);
        if (candidates.isEmpty()) return;

        int filesDeleted = deleteTempFiles(candidates);
        deleteTasks(candidates);
        logSummary(candidates.size(), filesDeleted);
    }

    private Date computeThreshold() {
        return Date.from(Instant.now().minus(retentionDays, ChronoUnit.DAYS));
    }

    private List<ProjectGenerationTask> findOldPending(Date threshold) {
        return repo.findByStatusAndCreationDateBefore(TaskStatusEnum.PENDING, threshold);
    }

    private int deleteTempFiles(List<ProjectGenerationTask> tasks) {
        return tasks.stream()
                .map(ProjectGenerationTask::getStorageLocation)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Paths::get)
                .mapToInt(path -> {
                    try {
                        return Files.deleteIfExists(path) ? 1 : 0;
                    } catch (IOException ex) {
                        log.warn("Could not delete temp file {}", path, ex);
                        return 0;
                    }
                })
                .sum();
    }

    private void deleteTasks(List<ProjectGenerationTask> tasks) {
        repo.deleteAll(tasks);
    }

    private void logSummary(int tasksDeleted, int filesDeleted) {
        log.info("CleanupJob: deleted {} pending tasks older than {} days ({} temp files removed).",
                tasksDeleted, retentionDays, filesDeleted);
    }
}
