package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员用户
        if (userRepository.findByUsername("admin").isEmpty()) {
            // 创建管理员用户
            User adminUser = new User();
            adminUser.setName("管理员");
            adminUser.setUsername("admin");
            adminUser.setPassword("123456");
            adminUser.setEmail("admin@example.com");
            userRepository.save(adminUser);
            
            System.out.println("Created default admin user: admin/123456");
        }
        
        // 添加其他用户
        if (userRepository.findByUsername("zhaolei@umeweb.com").isEmpty()) {
            User user = new User();
            user.setName("赵磊");
            user.setUsername("zhaolei@umeweb.com");
            user.setPassword("123456");
            user.setEmail("zhaolei@umeweb.com");
            userRepository.save(user);
            
            System.out.println("Created user: zhaolei@umeweb.com/123456");
        }
    }
} 