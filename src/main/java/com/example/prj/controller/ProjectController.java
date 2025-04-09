package com.example.prj.controller;

import com.example.prj.entity.Project;
import com.example.prj.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<List<Project>> saveProjects(@RequestBody List<Project> projects) {
        projectRepository.deleteAll(); // 清除所有现有项目
        List<Project> savedProjects = projectRepository.saveAll(projects);
        return ResponseEntity.ok(savedProjects);
    }
} 