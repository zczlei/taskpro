package com.example.prj;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class PrjApplication {
    public static void main(String[] args) {
        // 在启动应用前先加载环境变量
        loadEnvVariables();
        
        // 启动应用
        SpringApplication.run(PrjApplication.class, args);
    }
    
    private static void loadEnvVariables() {
        try {
            // 检查.env文件是否存在
            File envFile = new File(".env");
            if (envFile.exists()) {
                System.out.println("找到.env文件，正在加载环境变量...");
                Dotenv dotenv = Dotenv.configure()
                    .directory("/Users/a1-6/PycharmProjects/prj")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
                
                // 设置环境变量
                String url = dotenv.get("MYSQL_URL");
                if (url != null && !url.isEmpty()) {
                    System.setProperty("spring.datasource.url", url);
                    System.out.println("已设置spring.datasource.url: " + url);
                }
                
                String username = dotenv.get("MYSQL_USERNAME");
                if (username != null && !username.isEmpty()) {
                    System.setProperty("spring.datasource.username", username);
                    System.out.println("已设置spring.datasource.username: " + username);
                }
                
                String password = dotenv.get("MYSQL_PASSWORD");
                if (password != null && !password.isEmpty()) {
                    System.setProperty("spring.datasource.password", password);
                    System.out.println("已设置spring.datasource.password: " + password);
                }
                
                System.out.println("环境变量已从.env文件加载完成");
            } else {
                System.out.println(".env文件不存在，将使用命令行参数或系统环境变量");
            }
        } catch (Exception e) {
            System.out.println("加载.env文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 