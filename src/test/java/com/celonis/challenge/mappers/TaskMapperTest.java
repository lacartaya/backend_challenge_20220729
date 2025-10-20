package com.celonis.challenge.mappers;

import com.celonis.challenge.api.model.TaskCreate;
import com.celonis.challenge.api.model.TaskUpdate;
import com.celonis.challenge.model.ProjectGenerationTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    @Test
    @DisplayName("toDto(null) -> null")
    void toDto_null_returnsNull() {
        assertThat(TaskMapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("toDto() mapea id, name, creationDate (UTC) y storageLocation")
    void toDto_mapsAllFields() {
        // given
        final var entity = new ProjectGenerationTask();
        entity.setId("t-123");
        entity.setName("My Task");
        entity.setCreationDate(Date.from(Instant.parse("2024-01-02T03:04:05Z")));
        entity.setStorageLocation("/tmp/ch.zip");

        // when
        final var dto = TaskMapper.toDto(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("t-123");
        assertThat(dto.getName()).isEqualTo("My Task");
        assertThat(dto.getStorageLocation()).isEqualTo("/tmp/ch.zip");
        assertThat(dto.getCreationDate())
                .isEqualTo(OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC));
    }

    @Test
    @DisplayName("fromCreate(null) -> null")
    void fromCreate_null_returnsNull() {
        //Given //When //Then
        assertThat(TaskMapper.fromCreate(null)).isNull();
    }

    @Test
    @DisplayName("fromCreate() solo mapea name")
    void fromCreate_mapsNameOnly() {
        // given
        final var req = new TaskCreate().name("New Task");

        // when
        final var entity = TaskMapper.fromCreate(req);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("New Task");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreationDate()).isNull();
        assertThat(entity.getStorageLocation()).isNull();
    }

    @Test
    @DisplayName("applyUpdate(null, target) no cambia nada")
    void applyUpdate_nullUpdate_noChange() {
        //Given
        final var target = new ProjectGenerationTask();
        target.setName("Old name");

        //When
        TaskMapper.applyUpdate(null, target);

        //Then
        assertThat(target.getName()).isEqualTo("Old name");
    }

    @Test
    @DisplayName("applyUpdate(update, null) es no-op (sin NPE)")
    void applyUpdate_nullTarget_noop() {
        //Given
        final var update = new TaskUpdate().name("Whatever");

        //When //Then
        TaskMapper.applyUpdate(update, null);
    }

    @Test
    @DisplayName("applyUpdate(update, target) establece name")
    void applyUpdate_setsName() {
        //Given
        final var target = new ProjectGenerationTask();
        target.setName("Old name");
        final var update = new TaskUpdate().name("New name");

        //When
        TaskMapper.applyUpdate(update, target);

        //Then
        assertThat(target.getName()).isEqualTo("New name");
    }

    @Test
    @DisplayName("toOffset(null) -> null")
    void toOffset_null_returnsNull() {
        //Given //When //Then
        assertThat(TaskMapper.toOffset(null)).isNull();
    }

    @Test
    @DisplayName("toOffset(Date) convierte a OffsetDateTime en UTC")
    void toOffset_convertsToUtc() {
        //Given
        final var date = Date.from(Instant.parse("2023-12-31T23:59:59Z"));

        //When
        final var odt = TaskMapper.toOffset(date);

        //Then
        assertThat(odt).isEqualTo(OffsetDateTime.of(2023, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC));
    }
}
