package com.celonis.challenge.model;

import com.celonis.challenge.api.model.Task;

import java.math.BigDecimal;

public class TaskMapper {

    public static Task toDto(ProjectGenerationTask e) {
        Task t = new Task();
        t.setId(e.getId());
        t.setType(e.getType());
        t.setStatus(e.getStatus());
        t.setCreatedAt(e.getCreatedAt());
        t.setStartedAt(e.getStartedAt());
        t.setFinishedAt(e.getFinishedAt());
        t.setCanceledAt(e.getCanceledAt());
        t.setStorageLocation(e.getStorageLocation());
        t.setX(e.getX());
        t.setY(e.getY());
        t.setCurrent(e.getCurrent());
        if (e.getProgressPct() != null) {
            t.setProgressPct(BigDecimal.valueOf(e.getProgressPct()));
        }
        return t;
    }
}