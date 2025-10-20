package com.celonis.challenge.it;

import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private FileService fileService;

    private static ProjectGenerationTask pgt(String id, String name, String status) {
        final var e = ProjectGenerationTask.builder()
                .id(id)
                .name(name)
                .status(TaskStatusEnum.valueOf(status))
                .build();
        return e;
    }

    @Test
    @DisplayName("POST /api/tasks -> 201 (integration)")
    void createTask_integration() throws Exception {
        //Given
        when(taskService.createTask(any())).thenReturn(pgt("int-1", "Integration Task", "PENDING"));
        final var body = "{"
                + "\"name\":\"Integration Task\","
                + "\"description\":\"desc\","
                + "\"status\":\"PENDING\""
                + "}";

        //When //Then
        mockMvc.perform(post("/api/tasks")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("int-1")))
                .andExpect(jsonPath("$.name", is("Integration Task")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} -> 200 (integration)")
    void updateTask_integration() throws Exception {
        //Given
        when(taskService.getTask(eq("int-1"))).thenReturn(pgt("int-1", "Integration Task", "PENDING"));
        when(taskService.update(eq("int-1"), any())).thenAnswer(inv -> {
            ProjectGenerationTask updated = inv.getArgument(1);
            updated.setId("int-1");
            return updated;
        });
        final var body = "{\"name\":\"Renamed\",\"description\":\"d\"}";

        //When//Then
        mockMvc.perform(put("/api/tasks/{taskId}", "int-1")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("int-1")))
                .andExpect(jsonPath("$.name", is("Renamed")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} -> 200 (integration)")
    void getTask_integration() throws Exception {
        //Given
        when(taskService.getTask(eq("int-1"))).thenReturn(pgt("int-1", "Integration Task", "PENDING"));

        //When //Then
        mockMvc.perform(get("/api/tasks/{taskId}", "int-1")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("int-1")))
                .andExpect(jsonPath("$.name", is("Integration Task")));
    }
}
