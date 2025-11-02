package com.zjsu.nsq.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/pool/health")
    public Map<String, Object> getPoolHealth() {
        Map<String, Object> health = new HashMap<>();

        try {
            // 测试数据库连接
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();

                health.put("status", "HEALTHY");
                health.put("timestamp", LocalDateTime.now());
                health.put("database", metaData.getDatabaseProductName());
                health.put("connectionPool", "HikariCP");
                health.put("message", "连接池运行正常");
            }

        } catch (Exception e) {
            health.put("status", "UNHEALTHY");
            health.put("timestamp", LocalDateTime.now());
            health.put("error", e.getMessage());
            health.put("message", "连接池可能存在问题");
        }

        return health;
    }

    @GetMapping("/pool/status")
    public Map<String, Object> getPoolStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now());

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            status.put("database", metaData.getDatabaseProductName());
            status.put("version", metaData.getDatabaseProductVersion());
            status.put("url", metaData.getURL().replaceAll("password=[^&]*", "password=***"));

            // 连接池信息
            Map<String, Object> poolInfo = new HashMap<>();
            poolInfo.put("connectionPool", "HikariCP");
            poolInfo.put("status", "ACTIVE");

            status.put("connectionPool", poolInfo);
            status.put("status", "HEALTHY");

        } catch (Exception e) {
            status.put("status", "UNHEALTHY");
            status.put("error", e.getMessage());
        }

        return status;
    }
}