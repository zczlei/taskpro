package com.example.prj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db-test")
public class DbTestController {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("连接状态", "成功");
            result.put("数据库URL", metaData.getURL());
            result.put("数据库产品名称", metaData.getDatabaseProductName());
            result.put("数据库产品版本", metaData.getDatabaseProductVersion());
            result.put("驱动名称", metaData.getDriverName());
            result.put("驱动版本", metaData.getDriverVersion());
            
        } catch (Exception e) {
            result.put("连接状态", "失败");
            result.put("错误信息", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/tables")
    public List<Map<String, Object>> listTables() {
        return jdbcTemplate.queryForList("SHOW TABLES");
    }
} 