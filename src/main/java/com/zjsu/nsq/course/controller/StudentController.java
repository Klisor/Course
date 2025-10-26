package com.zjsu.nsq.course.controller;

import com.zjsu.nsq.course.model.Student;
import com.zjsu.nsq.course.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        try {
            List<Student> students = service.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", students);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Internal server error: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id) {
        try {
            Student student = service.findById(id)
                    .orElseThrow(() -> new RuntimeException("学生不存在"));

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", student);
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Student student) {
        try {
            // 添加基本参数验证
            if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "学号不能为空");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }

            if (student.getName() == null || student.getName().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "学生姓名不能为空");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }

            Student createdStudent = service.create(student);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "学生创建成功");
            response.put("data", createdStudent);
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();

            // 根据异常消息确定具体的错误类型和状态码
            String errorMessage = e.getMessage();
            int errorCode;

            if (errorMessage.contains("学号不能为空") ||
                    errorMessage.contains("学生姓名不能为空") ||
                    errorMessage.contains("邮箱格式不正确")) {
                errorCode = 400; // 请求参数错误
            } else if (errorMessage.contains("学号已存在") ||
                    errorMessage.contains("学号重复")) {
                errorCode = 409; // 冲突
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

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String id, @RequestBody Student student) {
        try {
            Student updatedStudent = service.update(id, student);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "学生信息更新成功");
            response.put("data", updatedStudent);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();

            // 根据异常消息确定具体的错误类型和状态码
            String errorMessage = e.getMessage();
            int errorCode;

            if (errorMessage.contains("学生不存在")) {
                errorCode = 404; // 资源不存在
            } else if (errorMessage.contains("学号重复") ||
                    errorMessage.contains("邮箱格式不正确")) {
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
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        try {
            service.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
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