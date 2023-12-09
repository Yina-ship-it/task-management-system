package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
