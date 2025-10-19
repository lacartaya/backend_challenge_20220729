package com.celonis.challenge.mappers;

import com.celonis.challenge.api.model.Task;
import com.celonis.challenge.api.model.TaskCreate;
import com.celonis.challenge.api.model.TaskUpdate;
import com.celonis.challenge.model.ProjectGenerationTask;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@UtilityClass
public class TaskMapper {

    public Task toDto(ProjectGenerationTask e) {
        if (e == null) return null;
        return new Task()
                .id(e.getId())
                .name(e.getName())
                .creationDate(toOffset(e.getCreationDate()))
                .storageLocation(e.getStorageLocation());
    }

    public ProjectGenerationTask fromCreate(TaskCreate c) {
        return c == null
                ? null
                : ProjectGenerationTask.builder()
                .name(c.getName())
                .build();
    }

    public void applyUpdate(TaskUpdate u, ProjectGenerationTask target) {
        if (u != null && target != null) {
            target.setName(u.getName());
        }
    }

    public OffsetDateTime toOffset(Date date) {
        return date == null ? null : OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
