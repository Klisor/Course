package com.zjsu.nsq.course.controller;

import com.zjsu.nsq.course.repository.CourseRepository;
import com.zjsu.nsq.course.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dev")
public class DevTestController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/test")
    public Map<String, Object> devTest() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 测试数据库连接和基本查询
            long courseCount = courseRepository.count();
            long studentCount = studentRepository.count();

            result.put("status", "SUCCESS");
            result.put("environment", "DEV (H2)");
            result.put("courseCount", courseCount);
            result.put("studentCount", studentCount);
            result.put("h2Console", "http://localhost:8080/h2-console");
            result.put("message", "开发环境运行正常");

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "开发环境测试失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/h2-info")
    public Map<String, Object> h2Info() {
        Map<String, Object> info = new HashMap<>();
        info.put("h2ConsoleUrl", "http://localhost:8080/h2-console");
        info.put("jdbcUrl", "jdbc:h2:mem:course_dev");
        info.put("username", "sa");
        info.put("password", "");
        info.put("driverClass", "org.h2.Driver");
        return info;
    }
}