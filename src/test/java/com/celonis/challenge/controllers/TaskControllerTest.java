package com.celonis.challenge.controllers;

import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private FileService fileService;

    private static ProjectGenerationTask pgt(String id, String name, String status) {

        return ProjectGenerationTask.builder()
                .id(id)
                .name(name)
                .status(TaskStatusEnum.valueOf(status))
                .build();
    }

    private static TaskService.TaskProgressDto progress(String status, int progress, int current, int target, String message) {
        final var dto = new TaskService.TaskProgressDto();
        dto.status = status;
        dto.progress = progress;
        dto.current = current;
        dto.target = target;
        dto.message = message;
        return dto;
    }

    @Test
    @DisplayName("GET /api/tasks -> 200 con mapeo a DTO")
    void listTasks_ok() throws Exception {
        //Given
        final var entities = Arrays.asList(
                pgt("t1", "Task 1", "PENDING"),
                pgt("t2", "Task 2", "COMPLETED")
        );
        when(taskService.listTasks()).thenReturn(entities);

        //When //Then
        mockMvc.perform(get("/api/tasks").header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("t1")))
                .andExpect(jsonPath("$[0].name", is("Task 1")))
                .andExpect(jsonPath("$[1].id", is("t2")))
                .andExpect(jsonPath("$[1].name", is("Task 2")));
    }

    @Test
    @DisplayName("POST /api/tasks -> 201 Created")
    void createTask_created() throws Exception {
        //Given
        when(taskService.createTask(any()))
                .thenReturn(pgt("abc123", "My Task", "PENDING"));
        final var body = "{"
                + "\"name\":\"My Task\","
                + "\"description\":\"Desc\""
                + "}";

        //When //Then
        mockMvc.perform(post("/api/tasks")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.name", is("My Task")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} -> 200")
    void getTask_ok() throws Exception {
        //Given
        when(taskService.getTask(eq("t42"))).thenReturn(pgt("t42", "Read Me", "RUNNING"));

        //When //Then
        mockMvc.perform(get("/api/tasks/{taskId}", "t42")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("t42")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} -> 200")
    void updateTask_ok() throws Exception {
        //Given
        when(taskService.getTask(eq("t9"))).thenReturn(pgt("t9", "Old", "PENDING"));
        when(taskService.update(eq("t9"), any())).thenAnswer(inv -> {
            ProjectGenerationTask updated = inv.getArgument(1);
            updated.setId("t9");
            return updated;
        });

        final var body = "{ \"name\": \"New Name\", \"description\": \"d\" }";

        //When //Then
        mockMvc.perform(put("/api/tasks/{taskId}", "t9")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("t9")))
                .andExpect(jsonPath("$.name", is("New Name")));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} -> 204")
    void deleteTask_noContent() throws Exception {
        //Given //When //Then
        mockMvc.perform(delete("/api/tasks/{taskId}", "deadbeef")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/tasks/{id}/execute -> 204")
    void executeTask_noContent() throws Exception {
        //Given //When //Then
        mockMvc.perform(post("/api/tasks/{taskId}/execute", "z1")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /counter/start -> 202 con progreso")
    void startCounter_accepted() throws Exception {
        //Given
        when(taskService.startCounter(eq("c1"), eq(1), eq(10)))
                .thenReturn(progress("RUNNING", 10, 1, 10, "Started"));

        final var body = "{\"x\":1,\"y\":10}";

        //When //Then
        mockMvc.perform(post("/api/tasks/{taskId}/counter/start", "c1")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status", is("RUNNING")))
                .andExpect(jsonPath("$.progress", is(10)))
                .andExpect(jsonPath("$.current", is(1)))
                .andExpect(jsonPath("$.target", is(10)))
                .andExpect(jsonPath("$.message", is("Started")));
    }

    @Test
    @DisplayName("GET /counter/progress -> 200")
    void getCounterProgress_ok() throws Exception {
        //Given
        when(taskService.getCounterProgress(eq("c2")))
                .thenReturn(progress("RUNNING", 30, 3, 10, "Counting"));
        //When //Then
        mockMvc.perform(get("/api/tasks/{taskId}/counter/progress", "c2")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RUNNING")))
                .andExpect(jsonPath("$.progress", is(30)))
                .andExpect(jsonPath("$.current", is(3)))
                .andExpect(jsonPath("$.target", is(10)));
    }

    @Test
    @DisplayName("POST /counter/cancel -> 202")
    void cancelCounter_accepted() throws Exception {
        //Given
        when(taskService.cancelCounter(eq("c3")))
                .thenReturn(progress("CANCELED", 30, 3, 10, "Canceled"));

        //When //Then
        mockMvc.perform(post("/api/tasks/{taskId}/counter/cancel", "c3")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status", is("CANCELED")))
                .andExpect(jsonPath("$.message", is("Canceled")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} -> 404 cuando TaskService lanza NotFoundException")
    void getTask_notFound() throws Exception {
        //Given
        when(taskService.getTask(eq("missing"))).thenThrow(new NotFoundException());

        //When //Then
        mockMvc.perform(get("/api/tasks/{taskId}", "missing")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks/{id}/execute -> 404 cuando TaskService lanza NotFoundException")
    void executeTask_notFound() throws Exception {
        //Given
        doThrow(new NotFoundException()).when(taskService).executeTask(eq("missing"));

        //When //Then
        mockMvc.perform(post("/api/tasks/{taskId}/execute", "missing")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /counter/start sin body -> 500 (mapeado por ErrorController)")
    void startCounter_nullBody_internalError() throws Exception {
        //Given //When //Then
        mockMvc.perform(post("/api/tasks/{taskId}/counter/start", "c500")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
