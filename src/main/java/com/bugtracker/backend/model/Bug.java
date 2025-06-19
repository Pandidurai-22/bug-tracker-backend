package com.bugtracker.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bugs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Bug{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String priority;
    private String status;

    private String assignee;
    private String reporter;

    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}