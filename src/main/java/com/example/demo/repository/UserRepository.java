package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 根据名字模糊查询
    List<User> findByNameContaining(String name);
    
    // 根据用户名精确查询
    Optional<User> findByUsername(String username);
} 