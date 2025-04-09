package com.example.prj.controller;

import com.example.prj.entity.Requirement;
import com.example.prj.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requirements")
public class RequirementController {

    @Autowired
    private RequirementRepository requirementRepository;

    @GetMapping
    public List<Requirement> getAllRequirements() {
        return requirementRepository.findAll();
    }

    @PostMapping
    public Requirement createRequirement(@RequestBody Requirement requirement) {
        return requirementRepository.save(requirement);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Requirement> getRequirementById(@PathVariable Long id) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found with id: " + id));
        return ResponseEntity.ok(requirement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Requirement> updateRequirement(@PathVariable Long id, @RequestBody Requirement requirementDetails) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found with id: " + id));

        requirement.setProjectName(requirementDetails.getProjectName());
        requirement.setDescription(requirementDetails.getDescription());
        requirement.setDetails(requirementDetails.getDetails());
        requirement.setAssignee(requirementDetails.getAssignee());
        requirement.setStatus(requirementDetails.getStatus());
        requirement.setPriority(requirementDetails.getPriority());
        requirement.setDueDate(requirementDetails.getDueDate());
        requirement.setEstimatedCompletionDate(requirementDetails.getEstimatedCompletionDate());

        Requirement updatedRequirement = requirementRepository.save(requirement);
        return ResponseEntity.ok(updatedRequirement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequirement(@PathVariable Long id) {
        requirementRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 