package com.example.prj.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "requirements")
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String projectName;
    
    @Column(nullable = false)
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(nullable = false)
    private String assignee;
    
    @Column(nullable = false)
    private String status;
    
    @Column(nullable = false)
    private String priority;
    
    private String developer;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDate estimatedCompletionDate;
} 