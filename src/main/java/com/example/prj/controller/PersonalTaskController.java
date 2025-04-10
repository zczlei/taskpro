package com.example.prj.controller;

import com.example.prj.entity.PersonalTask;
import com.example.prj.repository.PersonalTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/personal-tasks")
public class PersonalTaskController {

    @Autowired
    private PersonalTaskRepository personalTaskRepository;

    @GetMapping
    public List<PersonalTask> getAllTasks() {
        return personalTaskRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<PersonalTask> createTask(@RequestBody PersonalTask task) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        
        // 如果状态为空，则默认设置为PENDING
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        
        PersonalTask savedTask = personalTaskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalTask> updateTask(@PathVariable Long id, @RequestBody PersonalTask taskDetails) {
        PersonalTask task = personalTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setName(taskDetails.getName());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        
        // 如果提供了图片路径，则更新
        if (taskDetails.getImagePath() != null) {
            task.setImagePath(taskDetails.getImagePath());
        }

        PersonalTask updatedTask = personalTaskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        personalTaskRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<PersonalTask> startTask(@PathVariable Long id) {
        PersonalTask task = personalTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus("IN_PROGRESS");
        PersonalTask updatedTask = personalTaskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<PersonalTask> completeTask(@PathVariable Long id) {
        PersonalTask task = personalTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus("COMPLETED");
        task.setCompletedAt(LocalDateTime.now());
        PersonalTask updatedTask = personalTaskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<PersonalTask> reopenTask(@PathVariable Long id) {
        PersonalTask task = personalTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus("PENDING");
        task.setCompletedAt(null);
        PersonalTask updatedTask = personalTaskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }
} 