package com.example.prj.controller;

import com.example.prj.dto.LoginRequest;
import com.example.prj.dto.LoginResponse;
import com.example.prj.entity.User;
import com.example.prj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 验证用户名和密码
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(loginRequest.getPassword())) {
            // 登录成功，生成一个简单的token
            String token = UUID.randomUUID().toString();
            
            // 创建响应
            LoginResponse response = new LoginResponse(
                token,
                userOptional.get().getUsername(),
                userOptional.get().getName()
            );
            
            return ResponseEntity.ok(response);
        } else {
            // 登录失败
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("用户名或密码错误");
        }
    }
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("用户名已存在");
        }
        
        // 保存新用户
        User savedUser = userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    if (userDetails.getPhone() != null) {
                        user.setPhone(userDetails.getPhone());
                    }
                    if (userDetails.getDepartment() != null) {
                        user.setDepartment(userDetails.getDepartment());
                    }
                    if (userDetails.getPosition() != null) {
                        user.setPosition(userDetails.getPosition());
                    }
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(Map.of("deleted", true));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(request.get("password"));
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("success", true));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 