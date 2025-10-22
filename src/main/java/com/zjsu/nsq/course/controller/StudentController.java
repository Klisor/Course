package com.zjsu.nsq.course.controller;

import com.zjsu.nsq.course.model.Student;
import com.zjsu.nsq.course.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }
}