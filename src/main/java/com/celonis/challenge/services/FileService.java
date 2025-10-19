package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

@Component
public class FileService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public FileService(ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    public ResponseEntity<FileSystemResource> getTaskResult(String taskId) {
        final var projectGenerationTask = projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);

        final var inputFile = new File(projectGenerationTask.getStorageLocation());

        if (!inputFile.exists()) {
            throw new InternalException("File not generated yet");
        }

        final var respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }

    public void storeResult(String taskId, InputStream is, String filename) throws IOException {
        final var task = projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);

        final var outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();

        try (OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        } catch (IOException e) {
            throw new InternalException(e);
        }

        task.setStorageLocation(outputFile.getAbsolutePath());
        projectGenerationTaskRepository.save(task);
    }

}
