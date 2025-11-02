package com.zjsu.nsq.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());

        try {
            // 测试数据库连接
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("database", "UP");
            health.put("databaseTest", result);
            health.put("status", "healthy");

            // 添加数据库信息
            String dbInfo = jdbcTemplate.queryForObject("SELECT H2VERSION() FROM DUAL", String.class);
            health.put("databaseVersion", dbInfo);

        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("error", e.getMessage());
            health.put("status", "unhealthy");
        }

        return health;
    }

    @GetMapping("/info")
    public Map<String, Object> applicationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Course Management System");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "running");
        info.put("environment", "DEV");
        return info;
    }
}