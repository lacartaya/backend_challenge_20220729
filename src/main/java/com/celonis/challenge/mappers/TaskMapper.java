package com.celonis.challenge.mappers;

import com.celonis.challenge.api.model.Task;
import com.celonis.challenge.api.model.TaskCreate;
import com.celonis.challenge.api.model.TaskUpdate;
import com.celonis.challenge.model.ProjectGenerationTask;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class TaskMapper {
    private TaskMapper() {}

    // ===== Entity -> DTO (response)
    public static Task toDto(ProjectGenerationTask e) {
        if (e == null) return null;
        Task t = new Task();
        t.setId(e.getId());
        t.setName(e.getName());
        if (e.getCreationDate() != null) {
            t.setCreationDate(OffsetDateTime.ofInstant(e.getCreationDate().toInstant(), ZoneOffset.UTC));
        }
        // storageLocation es readOnly; lo exponemos si existe
        t.setStorageLocation(e.getStorageLocation());
        return t;
    }

    // ===== Create request -> Entity base
    public static ProjectGenerationTask fromCreate(TaskCreate c) {
        ProjectGenerationTask e = new ProjectGenerationTask();
        if (c != null) {
            e.setName(c.getName());
        }
        // id y creationDate los asigna el servicio
        return e;
    }

    // ===== Update request -> aplicar campos editables
    public static void applyUpdate(TaskUpdate u, ProjectGenerationTask target) {
        if (u == null || target == null) return;
        target.setName(u.getName());
        // No permitimos tocar id/creationDate/storageLocation desde la API
    }

    // ===== Util por si necesitas convertir Date -> OffsetDateTime en otro lado
    public static OffsetDateTime toOffset(Date date) {
        return date == null ? null : OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
