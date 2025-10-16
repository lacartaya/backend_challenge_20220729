package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
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
        ProjectGenerationTask task = projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);

        String path = task.getStorageLocation();
        if (path == null) {
            throw new NotFoundException();
        }

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new NotFoundException();
        }

        FileSystemResource body = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.length());
        headers.setContentDispositionFormData("attachment", file.getName());

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    public void storeResult(String taskId, URL url) throws IOException {
        ProjectGenerationTask task = projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);

        File outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();

        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }

        task.setStorageLocation(outputFile.getAbsolutePath());
        projectGenerationTaskRepository.save(task);
    }
}
