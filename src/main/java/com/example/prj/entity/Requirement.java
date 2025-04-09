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
    
    private String projectName;
    private String description;
    private String details;
    private String assignee;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private LocalDate estimatedCompletionDate;
} 