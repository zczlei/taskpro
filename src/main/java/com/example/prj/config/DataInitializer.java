package com.example.prj.config;

import com.example.prj.entity.User;
import com.example.prj.repository.UserRepository;
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
            adminUser.setPhone("13800138000");
            adminUser.setDepartment("技术部");
            adminUser.setPosition("开发工程师");
            userRepository.save(adminUser);
            
            System.out.println("Created default admin user: admin/123456");
        } else {
            // 更新已存在的admin用户的部门和职位
            User adminUser = userRepository.findByUsername("admin").get();
            if (adminUser.getDepartment() == null || adminUser.getPosition() == null) {
                adminUser.setDepartment("技术部");
                adminUser.setPosition("开发工程师");
                userRepository.save(adminUser);
                System.out.println("Updated admin user with department and position");
            }
        }
        
        // 添加其他用户
        if (userRepository.findByUsername("zhaolei@umeweb.com").isEmpty()) {
            User user = new User();
            user.setName("赵磊");
            user.setUsername("zhaolei@umeweb.com");
            user.setPassword("123456");
            user.setEmail("zhaolei@umeweb.com");
            user.setPhone("13900139000");
            user.setDepartment("产品部");
            user.setPosition("产品经理");
            userRepository.save(user);
            
            System.out.println("Created user: zhaolei@umeweb.com/123456");
        } else {
            // 更新已存在的zhaolei用户的部门和职位
            User user = userRepository.findByUsername("zhaolei@umeweb.com").get();
            if (user.getDepartment() == null || user.getPosition() == null) {
                user.setDepartment("产品部");
                user.setPosition("产品经理");
                userRepository.save(user);
                System.out.println("Updated zhaolei user with department and position");
            }
        }
        
        // 列出所有用户
        System.out.println("Current users in database:");
        userRepository.findAll().forEach(user -> {
            System.out.println("User: " + user.getUsername() + 
                               ", Name: " + user.getName() + 
                               ", Department: " + user.getDepartment() + 
                               ", Position: " + user.getPosition());
        });
    }
} 