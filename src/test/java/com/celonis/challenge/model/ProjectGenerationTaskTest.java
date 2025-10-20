package com.celonis.challenge.model;

import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.enums.TaskTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectGenerationTaskTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Builder defaults: type=ZIP_GENERATION, status=PENDING, progress=0")
    void builderDefaults() {
        //Given //When
        final var t = ProjectGenerationTask.builder()
                .name("Example")
                .build();

        //Then
        assertThat(t.getType()).isEqualTo(TaskTypeEnum.ZIP_GENERATION);
        assertThat(t.getStatus()).isEqualTo(TaskStatusEnum.PENDING);
        assertThat(t.getProgress()).isEqualTo(0);
        assertThat(t.getId()).isNull();
        assertThat(t.getCreationDate()).isNull();
        assertThat(t.getStorageLocation()).isNull();
        assertThat(t.getCurrentValue()).isNull();
        assertThat(t.getStartValue()).isNull();
        assertThat(t.getTargetValue()).isNull();
    }

    @Test
    @DisplayName("onCreate() inicializa creationDate/status/progress/type si están null")
    void onCreate_initializesNulls() {
        //Given //When
        final var t = new ProjectGenerationTask();
        t.setName("Init onCreate test");
        t.setType(null);
        t.setStatus(null);
        t.setProgress(null);
        t.setCreationDate(null);
        t.onCreate();

        //Then
        assertThat(t.getCreationDate()).isNotNull();
        assertThat(t.getStatus()).isEqualTo(TaskStatusEnum.PENDING);
        assertThat(t.getType()).isEqualTo(TaskTypeEnum.ZIP_GENERATION);
        assertThat(t.getProgress()).isEqualTo(0);
    }

    @Test
    @DisplayName("onCreate() NO sobrescribe valores ya establecidos")
    void onCreate_doesNotOverrideExistingValues() {
        //Given
        final var fixed = new Date(1700000000000L);
        final var t = new ProjectGenerationTask();
        t.setName("No override");
        t.setCreationDate(fixed);
        t.setStatus(TaskStatusEnum.RUNNING);
        t.setType(TaskTypeEnum.COUNTER);
        t.setProgress(33);

        //When
        t.onCreate();

        //Then
        assertThat(t.getCreationDate()).isEqualTo(fixed);
        assertThat(t.getStatus()).isEqualTo(TaskStatusEnum.RUNNING);
        assertThat(t.getType()).isEqualTo(TaskTypeEnum.COUNTER);
        assertThat(t.getProgress()).isEqualTo(33);
    }

    @Test
    @DisplayName("@JsonIgnore en storageLocation: no aparece en JSON")
    void jsonIgnore_onStorageLocation() throws Exception {
        //Given
        final var t = ProjectGenerationTask.builder()
                .id("abc-123")
                .name("Json test")
                .build();
        t.setStorageLocation("/tmp/secret.zip");
        t.onCreate();

        //When
        final var json = objectMapper.writeValueAsString(t);
        final var node = objectMapper.readTree(json);

        //Then
        assertThat(node.has("storageLocation")).isFalse();
        assertThat(node.get("id").asText()).isEqualTo("abc-123");
        assertThat(node.get("name").asText()).isEqualTo("Json test");
        assertThat(node.get("creationDate")).isNotNull();
        assertThat(node.get("type").asText()).isEqualTo("ZIP_GENERATION");
        assertThat(node.get("status").asText()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("@ToString incluye solo los campos anotados con @ToString.Include")
    void toString_includesOnlyIncludedFields() {
        //Given
        final var t = ProjectGenerationTask.builder()
                .id("id-1")
                .name("Visible name")
                .type(TaskTypeEnum.ZIP_GENERATION)
                .status(TaskStatusEnum.PENDING)
                .build();
        t.setCreationDate(new Date());
        t.setStorageLocation("/should/not/appear");
        t.setCurrentValue(5);
        t.setProgress(42);

        //When
        final var s = t.toString();

        //Then
        assertThat(s).contains("id-1");
        assertThat(s).contains("Visible name");
        assertThat(s).contains("ZIP_GENERATION");
        assertThat(s).contains("PENDING");
        assertThat(s).contains("creationDate");
        assertThat(s).doesNotContain("storageLocation");
        assertThat(s).doesNotContain("/should/not/appear");
        assertThat(s).doesNotContain("currentValue");
        assertThat(s).doesNotContain("targetValue");
        assertThat(s).doesNotContain("startValue");
        assertThat(s).doesNotContain("progress=");
    }
}
