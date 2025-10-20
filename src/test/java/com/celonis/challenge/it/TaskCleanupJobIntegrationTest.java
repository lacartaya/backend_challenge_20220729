package com.celonis.challenge.it;

import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.jobs.TaskCleanupJob;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "tasks.cleanup.enabled=true",
        "tasks.cleanup.retention-days=7",
        "tasks.cleanup.cron=*/30 * * * * *"
})
class TaskCleanupJobIntegrationTest {

    @Autowired
    private TaskCleanupJob job;

    @MockBean
    private ProjectGenerationTaskRepository repo;

    @Test
    void cleanup_removesExistingTempFile_andDeletesTasks() throws IOException {
        //Given
        ReflectionTestUtils.setField(job, "enabled", true);
        ReflectionTestUtils.setField(job, "retentionDays", 7);
        var tmp = Files.createTempFile("cleanup-int-", ".zip");
        assertThat(Files.exists(tmp)).isTrue();

        final var t1 = new ProjectGenerationTask();
        t1.setId("p1");
        t1.setCreationDate(new Date());
        t1.setStorageLocation(tmp.toAbsolutePath().toString());

        final var t2 = new ProjectGenerationTask();
        t2.setId("p2");
        t2.setCreationDate(new Date());
        t2.setStorageLocation(null);

        when(repo.findByStatusAndCreationDateBefore(eq(TaskStatusEnum.PENDING), any(Date.class)))
                .thenReturn(Arrays.asList(t1, t2));

        //When
        job.cleanupOldPendingTasks();

        //Then
        assertThat(Files.exists(tmp)).isFalse();
    }
}
