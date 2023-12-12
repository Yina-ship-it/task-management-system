package com.example.taskmanagementsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    private LocalDateTime dateTime;


    @ManyToOne
    @JoinColumn(name = "commentator_id")
    private User commentator;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
