package com.celonis.challenge.jobs;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.celonis.challenge.enums.TaskStatusEnum.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskCleanupJobTest {

    @Mock
    private ProjectGenerationTaskRepository repo;

    private TaskCleanupJob newJobEnabled() {
        final var job = new TaskCleanupJob(repo);
        ReflectionTestUtils.setField(job, "enabled", true);
        ReflectionTestUtils.setField(job, "retentionDays", 7);
        return job;
    }

    @Test
    void whenDisabled_thenDoesNothing() {
        //Given
        final var job = new TaskCleanupJob(repo);
        ReflectionTestUtils.setField(job, "enabled", false); // <- deshabilitado
        ReflectionTestUtils.setField(job, "retentionDays", 7);

        //When
        job.cleanupOldPendingTasks();

        //Then
        verify(repo, never()).findByStatusAndCreationDateBefore(any(), any());
        verify(repo, never()).deleteAll(any());
    }

    @Test
    void whenEnabled_andNoCandidates_thenNoDelete() {
        //Given
        final var job = newJobEnabled();
        when(repo.findByStatusAndCreationDateBefore(eq(PENDING), any(Date.class)))
                .thenReturn(Collections.emptyList());

        //When
        job.cleanupOldPendingTasks();

        //Then
        verify(repo, times(1)).findByStatusAndCreationDateBefore(eq(PENDING), any(Date.class));
        verify(repo, never()).deleteAll(any());
    }

    @Test
    void whenEnabled_andCandidates_thenDeletesFilesAndTasks() throws IOException {
        //Given
        final var job = newJobEnabled();
        final var tmp = Files.createTempFile("cleanup-job-", ".zip");
        assertThat(Files.exists(tmp)).isTrue();

        final var t1 = new ProjectGenerationTask();
        t1.setId("t1");
        t1.setCreationDate(new Date());
        t1.setStorageLocation(tmp.toAbsolutePath().toString());

        final var t2 = new ProjectGenerationTask();
        t2.setId("t2");
        t2.setCreationDate(new Date());
        t2.setStorageLocation("/this/path/does/not/exist.zip");

        final var t3 = new ProjectGenerationTask();
        t3.setId("t3");
        t3.setCreationDate(new Date());
        t3.setStorageLocation(null);

        when(repo.findByStatusAndCreationDateBefore(eq(PENDING), any(Date.class)))
                .thenReturn(Arrays.asList(t1, t2, t3));

        //When
        job.cleanupOldPendingTasks();

        //Then
        assertThat(Files.exists(tmp)).isFalse();
    }

    @Test
    @DisplayName("Candidato con storageLocation vacío: no borra fichero pero sí elimina la task")
    void candidateWithBlankStorageLocation_isDeleted_noFileDeletion() {
        //Given
        final var job = newJobEnabled();
        var tBlank = new ProjectGenerationTask();
        tBlank.setId("tb");
        tBlank.setCreationDate(new java.util.Date());
        tBlank.setStorageLocation("   ");

        when(repo.findByStatusAndCreationDateBefore(
                ArgumentMatchers.eq(PENDING),
                ArgumentMatchers.any(java.util.Date.class)))
                .thenReturn(Collections.singletonList(tBlank));

        //When
        job.cleanupOldPendingTasks();

        //Then
        ArgumentCaptor<Iterable<ProjectGenerationTask>> captor = ArgumentCaptor.forClass((Class) Iterable.class);
        verify(repo, times(1)).deleteAll(captor.capture());
        final var captured = StreamSupport.stream(captor.getValue().spliterator(), false)
                .collect(Collectors.toList());
        Assertions.assertThat(captured).hasSize(1).contains(tBlank);
    }

}
