package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.comment.CommentDto;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.User;

import java.util.List;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
public interface CommentService {
    CommentDto findCommentById(Long id);
    CommentDto createComment(CommentDto commentDto);
    void deleteCommentById(Long id, User commentatorOrTaskAuthor);
    CommentDto updateText(Long id, String text, User commentator);
}
