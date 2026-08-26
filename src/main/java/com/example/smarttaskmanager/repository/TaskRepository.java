package com.example.smarttaskmanager.repository;

import com.example.smarttaskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
