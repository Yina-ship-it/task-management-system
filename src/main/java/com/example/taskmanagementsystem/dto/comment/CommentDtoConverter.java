package com.example.taskmanagementsystem.dto.comment;

import com.example.taskmanagementsystem.dto.DtoConverter;
import com.example.taskmanagementsystem.dto.user.UserResponseConverter;
import com.example.taskmanagementsystem.models.Comment;
import com.example.taskmanagementsystem.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
@Component
public class CommentDtoConverter implements DtoConverter<Comment, CommentDto, CommentRequest, CommentResponse> {

    @Autowired
    private UserResponseConverter userResponseConverter;

    @Override
    public CommentDto convertEntityToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .task(comment.getTask())
                .commentator(comment.getCommentator())
                .build();
    }

    @Override
    public Comment convertDtoToEntity(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .commentator(commentDto.getCommentator())
                .task(commentDto.getTask())
                .build();
    }

    @Override
    public CommentDto convertRequestToDto(CommentRequest commentRequest) {
        return CommentDto.builder()
                .text(getNonBlankString(commentRequest.getText()))
                .task(Task.builder().id(commentRequest.getTaskId()).build())
                .build();
    }

    @Override
    public CommentResponse convertDtoToResponse(CommentDto commentDto) {
        return CommentResponse.builder()
                .id(commentDto.getId())
                .commentator(userResponseConverter.convertUserToResponse(commentDto.getCommentator()))
                .text(commentDto.getText())
                .dateTime(commentDto.getDateTime())
                .build();
    }

    private String getNonBlankString(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }

}
