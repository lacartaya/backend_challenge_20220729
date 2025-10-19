package com.celonis.challenge.jobs;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskStatus;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TaskCleanupJob {

    private final ProjectGenerationTaskRepository repo;

    @Value("${tasks.cleanup.enabled:true}")
    private boolean enabled;

    @Value("${tasks.cleanup.retention-days:7}")
    private int retentionDays;

    public TaskCleanupJob(ProjectGenerationTaskRepository repo) {
        this.repo = repo;
    }

    /**
     * Se ejecuta por defecto todos los días a las 03:00.
     * Puedes sobreescribir con tasks.cleanup.cron en application.properties.
     */
    //@Scheduled(fixedDelay = 15000) // cada 15 segundos
    @Scheduled(cron = "${tasks.cleanup.cron:0 0 3 * * *}")
    @Transactional
    public void cleanupOldPendingTasks() {
        if (!enabled) {
            return;
        }

        Instant thresholdInstant = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        Date threshold = Date.from(thresholdInstant);

        List<ProjectGenerationTask> oldPending =
                repo.findByStatusAndCreationDateBefore(TaskStatus.PENDING, threshold);

        if (oldPending.isEmpty()) {
            return;
        }

        int filesDeleted = 0;
        for (ProjectGenerationTask t : oldPending) {
            // si hay archivo temporal asociado, lo intentamos borrar
            if (t.getStorageLocation() != null && !t.getStorageLocation().isBlank()) {
                File f = new File(t.getStorageLocation());
                if (f.exists() && f.isFile()) {
                    if (f.delete()) {
                        filesDeleted++;
                    }
                }
            }
            repo.delete(t); // delete individual para poder manejar archivos antes
        }

        log.info("CleanupJob: deleted {} pending tasks older than {} days ({} temp files removed).",
                oldPending.size(), retentionDays, filesDeleted);
    }
}
