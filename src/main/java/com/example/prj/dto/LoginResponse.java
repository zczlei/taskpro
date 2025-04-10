package com.example.prj.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String name;
    
    // 构造函数
    public LoginResponse(String token, String username, String name) {
        this.token = token;
        this.username = username;
        this.name = name;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
} 