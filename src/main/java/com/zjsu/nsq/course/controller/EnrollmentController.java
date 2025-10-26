package com.zjsu.nsq.course.controller;

import com.zjsu.nsq.course.model.Enrollment;
import com.zjsu.nsq.course.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        try {
            List<Enrollment> enrollments = service.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", enrollments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Internal server error: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> byCourse(@PathVariable String courseId) {
        try {
            List<Enrollment> enrollments = service.findByCourseId(courseId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", enrollments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Internal server error: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> byStudent(@PathVariable String studentId) {
        try {
            List<Enrollment> enrollments = service.findByStudentId(studentId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", enrollments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Internal server error: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enroll(@RequestBody Enrollment enrollment) {
        try {
            // 验证请求参数
            if (enrollment.getStudentId() == null || enrollment.getStudentId().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "studentId 不能为空");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }

            if (enrollment.getCourseId() == null || enrollment.getCourseId().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "courseId 不能为空");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }

            Enrollment result = service.enroll(enrollment);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "选课成功");
            response.put("data", result);
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            // 根据异常消息确定具体的错误类型和状态码
            Map<String, Object> response = new HashMap<>();
            String errorMessage = e.getMessage();
            int errorCode;

            if (errorMessage.contains("学生不存在") || errorMessage.contains("课程不存在")) {
                errorCode = 404; // 资源不存在
            } else if (errorMessage.contains("重复选课") || errorMessage.contains("课程已满")) {
                errorCode = 409; // 冲突
            } else if (errorMessage.contains("不能为空")) {
                errorCode = 400; // 请求参数错误
            } else {
                errorCode = 400; // 其他业务错误
            }

            response.put("code", errorCode);
            response.put("message", errorMessage);
            response.put("data", null);
            return ResponseEntity.status(errorCode).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Internal server error: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable String id) {
        try {
            service.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "取消选课成功");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Internal server error: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
}