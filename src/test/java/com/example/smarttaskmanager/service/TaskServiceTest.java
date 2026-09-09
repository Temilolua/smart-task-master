package com.example.smarttaskmanager.service;

import com.example.smarttaskmanager.model.Task;
import com.example.smarttaskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import com.example.smarttaskmanager.exception.TaskNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testGetAllTasks() {

        Task task1 = new Task(1L, "Task 1", "Description 1", false);
        Task task2 = new Task(2L, "Task 2", "Description 2", true);

        Pageable pageable = PageRequest.of(0, 2);

        Page<Task> page = new PageImpl<>(List.of(task1, task2));

        when(taskRepository.findAll(pageable))
                .thenReturn(page);

        Page<Task> tasks = taskService.getAllTasks(pageable);

        assertEquals(2, tasks.getContent().size());
        assertEquals("Task 1", tasks.getContent().get(0).getTitle());
        assertEquals("Task 2", tasks.getContent().get(1).getTitle());

        verify(taskRepository).findAll(pageable);
    }

    @Test
    void testSaveTask() {
        Task task = new Task();
        task.setTitle("Learn Testing");
        task.setDescription("Practice Mockito");
        task.setCompleted(false);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.saveTask(task);

        assertNotNull(savedTask);
        assertEquals("Learn Testing", savedTask.getTitle());

        verify(taskRepository).save(task);
    }

    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Spring Boot");
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        Task foundTask = taskService.getTaskById(1L);
        assertNotNull(foundTask);
        assertEquals("Spring Boot", foundTask.getTitle());

        verify(taskRepository).findById(1L);
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(1L)
        );
        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository).findById(1L);
    }

    @Test
    void testUpdateTask() {

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setCompleted(false);

        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setCompleted(true);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existingTask));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(existingTask);

        Task result = taskService.updateTask(1L, updatedTask);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.isCompleted());

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void testDeleteTask() {

        when(taskRepository.existsById(1L))
                .thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void testDeleteTaskNotFound() {

        when(taskRepository.existsById(1L))
                .thenReturn(false);

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(1L)
        );

        assertEquals("Task not found", exception.getMessage());

        verify(taskRepository).existsById(1L);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}