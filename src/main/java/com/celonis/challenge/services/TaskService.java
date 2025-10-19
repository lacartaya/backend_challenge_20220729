package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import com.celonis.challenge.model.TaskStatus;
import com.celonis.challenge.model.TaskType;
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

/**
 * Servicio de tareas.
 * - Mantiene comportamiento legacy: ZIP execute (challenge.zip desde classpath).
 * - Añade nuevo tipo COUNTER con start/progress/cancel ejecutado en background.
 */
@Service
public class TaskService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;
    private final FileService fileService;
    private final ScheduledExecutorService scheduler;

    // Futures por taskId para poder cancelar el contador en ejecución
    private final ConcurrentMap<String, ScheduledFuture<?>> runningCounters = new ConcurrentHashMap<>();

    @Autowired
    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       FileService fileService) {
        this(projectGenerationTaskRepository, fileService, Executors.newScheduledThreadPool(4));
    }

    // ctor visible para inyectar un scheduler compartido si lo prefieres por @Bean
    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       FileService fileService,
                       ScheduledExecutorService scheduler) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
        this.fileService = fileService;
        this.scheduler = scheduler;
    }

    // ===================== CRUD =====================

    public List<ProjectGenerationTask> listTasks() {
        return projectGenerationTaskRepository.findAll();
    }

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        // Generamos id si viene nulo
        if (projectGenerationTask.getId() == null || projectGenerationTask.getId().isBlank()) {
            projectGenerationTask.setId(UUID.randomUUID().toString());
        }
        projectGenerationTask.setCreationDate(new Date());

        // Valores por defecto para progreso/estado/tipo si existen en la entidad
        if (projectGenerationTask.getStatus() == null) {
            projectGenerationTask.setStatus(TaskStatus.PENDING);
        }
        if (projectGenerationTask.getProgress() == null) {
            projectGenerationTask.setProgress(0);
        }
        if (projectGenerationTask.getType() == null) {
            projectGenerationTask.setType(TaskType.ZIP_GENERATION);
        }

        return projectGenerationTaskRepository.save(projectGenerationTask);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return get(taskId);
    }

    public ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask) {
        ProjectGenerationTask existing = get(taskId);
        // Copiamos solo campos editables (ajusta según tu entidad)
        existing.setName(projectGenerationTask.getName());
        // Normalmente no dejamos tocar creationDate/id desde API, pero si lo necesitas:
        existing.setCreationDate(projectGenerationTask.getCreationDate());
        return projectGenerationTaskRepository.save(existing);
    }

    public void delete(String taskId) {
        // Si hay un counter corriendo, cancelarlo antes de borrar
        cancelCounter(taskId);
        projectGenerationTaskRepository.deleteById(taskId);
    }

    // ===================== Legacy: ejecutar ZIP =====================

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

    // ===================== Nuevo tipo: COUNTER =====================

    /**
     * Arranca contador de x a y (inclusive), incrementando +1 por segundo.
     * Marca la tarea como RUNNING y persiste progreso.
     */
    public TaskProgressDto startCounter(String taskId, int x, int y) {
        if (y < x) {
            // Mantengo InternalException para no introducir nuevas excepciones; tu GlobalExceptionHandler
            // puede mapear IllegalArgumentException/BadRequest si lo prefieres.
            throw new InternalException("Invalid range: y must be >= x");
        }

        ProjectGenerationTask task = get(taskId);

        // Si ya está corriendo, no arrancar de nuevo
        if (runningCounters.containsKey(taskId) || task.getStatus() == TaskStatus.RUNNING) {
            throw new InternalException("Task is already running");
        }

        // Configurar como COUNTER e inicializar valores
        task.setType(TaskType.COUNTER);
        task.setStartValue(x);
        task.setTargetValue(y);
        task.setCurrentValue(x);
        task.setProgress(percentage(x, x, y));
        task.setStatus(TaskStatus.RUNNING);
        projectGenerationTaskRepository.save(task);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                ProjectGenerationTask t = projectGenerationTaskRepository.findById(taskId).orElse(null);
                if (t == null) {
                    cancelFuture(taskId);
                    return;
                }
                if (t.getStatus() != TaskStatus.RUNNING) {
                    cancelFuture(taskId);
                    return;
                }

                int cur = Optional.ofNullable(t.getCurrentValue()).orElse(x);
                int target = Optional.ofNullable(t.getTargetValue()).orElse(y);

                if (cur >= target) {
                    t.setCurrentValue(target);
                    t.setProgress(100);
                    t.setStatus(TaskStatus.COMPLETED);
                    projectGenerationTaskRepository.save(t);
                    cancelFuture(taskId);
                    return;
                }

                // Avanza 1
                cur += 1;
                t.setCurrentValue(cur);
                t.setProgress(percentage(cur, x, target));
                projectGenerationTaskRepository.save(t);

            } catch (Exception ex) {
                // Ante cualquier error, marcar FAILED y limpiar el future
                ProjectGenerationTask t = projectGenerationTaskRepository.findById(taskId).orElse(null);
                if (t != null) {
                    t.setStatus(TaskStatus.FAILED);
                    t.setProgress(0);
                    projectGenerationTaskRepository.save(t);
                }
                cancelFuture(taskId);
            }
        }, 0, 1, TimeUnit.SECONDS);

        runningCounters.put(taskId, future);

        return toProgressDto(task);
    }

    /**
     * Devuelve progreso del counter (o estado de la tarea si no es COUNTER).
     */
    public TaskProgressDto getCounterProgress(String taskId) {
        ProjectGenerationTask t = get(taskId);
        return toProgressDto(t);
    }

    /**
     * Cancela el counter si está en ejecución.
     */
    public TaskProgressDto cancelCounter(String taskId) {
        ProjectGenerationTask t = get(taskId);
        if (t.getStatus() == TaskStatus.RUNNING) {
            cancelFuture(taskId);
            t.setStatus(TaskStatus.CANCELED);
            projectGenerationTaskRepository.save(t);
        }
        return toProgressDto(t);
    }

    // ===================== Helpers =====================

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

    // DTO interno del servicio para desacoplar de modelos OpenAPI
    public static class TaskProgressDto {
        public String status;
        public int progress;
        public int current;
        public int target;
        public String message;
    }

    private TaskProgressDto toProgressDto(ProjectGenerationTask t) {
        TaskProgressDto dto = new TaskProgressDto();
        dto.status  = t.getStatus() == null ? TaskStatus.PENDING.name() : t.getStatus().name();
        dto.progress = t.getProgress() == null ? 0 : t.getProgress();
        dto.current  = t.getCurrentValue() == null ? 0 : t.getCurrentValue();
        dto.target   = t.getTargetValue() == null ? 0 : t.getTargetValue();
        dto.message  = null;
        return dto;
    }
}
