package com.example.smarttaskmanager.service;

import com.example.smarttaskmanager.model.Task;
import com.example.smarttaskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.smarttaskmanager.exception.TaskNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }



    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found"));
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByCompleted(boolean completed, Pageable pageable) {
        return taskRepository.findByCompleted(completed, pageable);
    }

    public Page<Task> searchTasks(String title, Pageable pageable) {
        return taskRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

}
