package com.example.prj.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import javax.annotation.PostConstruct;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void init() {
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
                if (url != null) {
                    System.setProperty("MYSQL_URL", url);
                    System.out.println("已设置MYSQL_URL: " + url);
                }
                
                String username = dotenv.get("MYSQL_USERNAME");
                if (username != null) {
                    System.setProperty("MYSQL_USERNAME", username);
                    System.out.println("已设置MYSQL_USERNAME: " + username);
                }
                
                String password = dotenv.get("MYSQL_PASSWORD");
                if (password != null) {
                    System.setProperty("MYSQL_PASSWORD", password);
                    System.out.println("已设置MYSQL_PASSWORD: " + password);
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