package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.models.Comment;
import com.example.taskmanagementsystem.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteAllByTask(Task task);
}
