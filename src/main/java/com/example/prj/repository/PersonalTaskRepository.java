package com.example.prj.repository;

import com.example.prj.entity.PersonalTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalTaskRepository extends JpaRepository<PersonalTask, Long> {
} 