package com.example.smarttaskmanager.controller;

import com.example.smarttaskmanager.exception.TaskNotFoundException;
import com.example.smarttaskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.smarttaskmanager.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.never;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private TaskService taskService;

    @Test
    void testGetAllTasks() throws Exception {
        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/tasks")
                )
                .andExpect(
                        status().isOk()
                );
    }
    @Test
    void testCreateTask() throws Exception {
        Task task = new Task(
                1L,
                "Study",
                "Learn Spring Boot",
                false
        );
        when(taskService.saveTask(any(Task.class)))
                .thenReturn(task);
        mockMvc.perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(task))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task(
                1L,
                "Study",
                "Learn Spring Boot",
                false
        );

        when(taskService.getTaskById(1L))
                .thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Study"))
                .andExpect(jsonPath("$.description").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task updatedTask = new Task(
                1L,
                "Updated Study",
                "Learn Spring Boot Testing",
                true
        );
        when(taskService.updateTask(eq(1L), any(Task.class)))
                .thenReturn(updatedTask);
        mockMvc.perform(
                        put("/tasks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Study"))
                .andExpect(jsonPath("$.description").value("Learn Spring Boot Testing"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testDeleteTask() throws Exception {

        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void testCreateTaskValidationFails() throws Exception {

        Task task = new Task();
        task.setTitle("");
        task.setDescription("Learn Spring Boot");

        mockMvc.perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(task))
                )
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveTask(any(Task.class));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {

        when(taskService.getTaskById(999L))
                .thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}