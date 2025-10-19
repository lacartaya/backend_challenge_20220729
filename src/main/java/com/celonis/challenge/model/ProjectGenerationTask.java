package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "project_generation_task")
public class ProjectGenerationTask {

    // ========= Identidad =========
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    // ========= Metadatos básicos =========
    @Column(length = 255)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * Ruta absoluta del archivo generado (ZIP). No se expone por JSON.
     */
    @JsonIgnore
    @Column(length = 2048)
    private String storageLocation;

    // ========= Soporte de tipos/estados =========
    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private TaskType type;    // ZIP_GENERATION | COUNTER

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private TaskStatus status; // PENDING | RUNNING | COMPLETED | CANCELED | FAILED

    // ========= Campos del contador =========
    private Integer startValue;   // x
    private Integer targetValue;  // y
    private Integer currentValue; // valor actual
    private Integer progress;     // 0..100

    // ========= Ciclo de vida =========
    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
        if (status == null) {
            status = TaskStatus.PENDING;
        }
        if (progress == null) {
            progress = 0;
        }
        if (type == null) {
            type = TaskType.ZIP_GENERATION; // por defecto comportamiento legacy
        }
    }

    // ========= Getters / Setters =========
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Integer getStartValue() { return startValue; }
    public void setStartValue(Integer startValue) { this.startValue = startValue; }

    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }

    public Integer getCurrentValue() { return currentValue; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    // ========= utilitarios opcionales =========
    @Override
    public String toString() {
        return "ProjectGenerationTask{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", type=" + type +
                ", status=" + status +
                ", startValue=" + startValue +
                ", targetValue=" + targetValue +
                ", currentValue=" + currentValue +
                ", progress=" + progress +
                '}';
    }
}
