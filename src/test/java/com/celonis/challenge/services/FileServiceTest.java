package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.repository.ProjectGenerationTaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    public static final String HELLO_ZIP = "hello-zip";
    public static final String CHALLENGE_ZIP = "challenge.zip";
    public static final String TASK_ID = "t123";
    @Mock
    private ProjectGenerationTaskRepository repository;

    private FileService service(ProjectGenerationTaskRepository repo) {
        return new FileService(repo);
    }

    private static ProjectGenerationTask taskWithPath(String id, String path) {
        return new ProjectGenerationTask().builder()
                .id(id)
                .storageLocation(path)
                .build();
    }

    @Test
    @DisplayName("getTaskResult: devuelve 200, headers y recurso cuando el archivo existe")
    void getTaskResult_ok() throws Exception {
        //Given
        final var tmp = File.createTempFile("fs-test-", ".zip");
        final var body = "zip-bytes".getBytes(UTF_8);
        tmp.deleteOnExit();
        Files.write(tmp.toPath(), body);
        final var task = taskWithPath("t1", tmp.getAbsolutePath());
        when(repository.findById("t1")).thenReturn(Optional.of(task));

        //When
        final var fileService = service(repository);
        final var resp = fileService.getTaskResult("t1");

        //Then
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        assertThat(resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .contains("attachment").contains("challenge.zip");

        assertThat(resp.getBody()).isNotNull();
    }

    @Test
    @DisplayName("getTaskResult: task no encontrada -> NotFoundException")
    void getTaskResult_notFound() {
        //Given
        when(repository.findById("missing")).thenReturn(Optional.empty());
        //When
        final var fileService = service(repository);
        //Then
        assertThatThrownBy(() -> fileService.getTaskResult("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getTaskResult: archivo inexistente -> InternalException('File not generated yet')")
    void getTaskResult_fileMissing() {
        //Given
        final var task = taskWithPath("t2", "/path/no/existe-" + System.nanoTime());
        when(repository.findById("t2")).thenReturn(Optional.of(task));

        //When
        FileService fileService = service(repository);

        //Then
        assertThatThrownBy(() -> fileService.getTaskResult("t2"))
                .isInstanceOf(InternalException.class)
                .hasMessageContaining("File not generated yet");
    }

    @Test
    @DisplayName("storeResult: copia el InputStream a un temp file, actualiza storageLocation y guarda")
    void storeResult_ok() throws Exception {
        //Given
        final var task = new ProjectGenerationTask().builder().id(TASK_ID).build();
        final var content = HELLO_ZIP.getBytes(UTF_8);
        final var captor = ArgumentCaptor.forClass(ProjectGenerationTask.class);
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(repository.save(any(ProjectGenerationTask.class))).thenAnswer(inv -> inv.getArgument(0));

        //When
        final var fileService = service(repository);
        try (final var is = new ByteArrayInputStream(content)) {
            fileService.storeResult(TASK_ID, is, CHALLENGE_ZIP);
        }

        //Then
        assertThat(task.getStorageLocation()).isNotBlank();
        final var stored = new File(task.getStorageLocation());
        assertThat(stored).exists();
        assertThat(Files.readAllBytes(stored.toPath())).isEqualTo(content);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(TASK_ID);
        assertThat(captor.getValue().getStorageLocation()).isEqualTo(task.getStorageLocation());
    }

    @Test
    @DisplayName("storeResult: task no encontrada -> NotFoundException (no guarda)")
    void storeResult_notFound() {
        //Given
        final var fileService = service(repository);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        //When
        assertThatThrownBy(() -> {
            try (final var is = new ByteArrayInputStream(new byte[]{1, 2, 3})) {
                fileService.storeResult("missing", is, "x.zip");
            }
        }).isInstanceOf(NotFoundException.class);

        //Then
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("storeResult: IOException al copiar -> InternalException y no save")
    void storeResult_ioFailure_wrappedAsInternalException() {
        //Given
        final var task = new ProjectGenerationTask().builder().id(TASK_ID).build();
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));

        //When
        final var fileService = service(repository);
        final var failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };

        //Then
        assertThatThrownBy(() -> fileService.storeResult(TASK_ID, failing, "bad.zip"))
                .isInstanceOf(InternalException.class);
        verify(repository, never()).save(any());
        assertThat(task.getStorageLocation()).isNull();
    }

    @Test
    @DisplayName("getTaskResult: el recurso contiene exactamente los bytes del archivo")
    void getTaskResult_bytesMatch() throws Exception {
        //Given
        final var tmp = File.createTempFile("fs-test-bytes-", ".zip");
        tmp.deleteOnExit();
        final var task = taskWithPath("tb", tmp.getAbsolutePath());
        final var body = "zip-bytes-123".getBytes(UTF_8);
        Files.write(tmp.toPath(), body);
        when(repository.findById("tb")).thenReturn(java.util.Optional.of(task));

        //When
        final var fileService = service(repository);
        final var resp = fileService.getTaskResult("tb");

        //Then
        assertThat(resp.getBody()).isNotNull();
        final var returned = resp.getBody().getFile();
        assertThat(returned).exists();
        assertThat(Files.readAllBytes(returned.toPath())).isEqualTo(body);
    }

    @Test
    @DisplayName("storeResult: InputStream vacío -> crea archivo de 0 bytes y guarda")
    void storeResult_emptyStream_createsZeroLengthFile() throws Exception {
        //Given
        final var task = new ProjectGenerationTask().builder().id("empty").build();
        when(repository.findById("empty")).thenReturn(java.util.Optional.of(task));
        when(repository.save(any(ProjectGenerationTask.class))).thenAnswer(inv -> inv.getArgument(0));

        //When
        final var fileService = service(repository);
        try (final var is = new ByteArrayInputStream(new byte[0])) {
            fileService.storeResult("empty", is, "challenge.zip");
        }

        //Then
        assertThat(task.getStorageLocation()).isNotBlank();
        final var stored = new File(task.getStorageLocation());
        assertThat(stored).exists();
        assertThat(stored.length()).isEqualTo(0L);
    }
}
