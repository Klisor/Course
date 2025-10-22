package com.zjsu.nsq.course.controller;

import com.zjsu.nsq.course.model.Enrollment;
import com.zjsu.nsq.course.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Enrollment> list() {
        return service.findAll();
    }

    @GetMapping("/course/{courseId}")
    public List<Enrollment> byCourse(@PathVariable String courseId) {
        return service.findByCourseId(courseId);
    }

    @GetMapping("/student/{studentId}")
    public List<Enrollment> byStudent(@PathVariable String studentId) {
        return service.findByStudentId(studentId);
    }

    @PostMapping
    public Enrollment enroll(@RequestBody Enrollment enrollment) {
        return service.enroll(enrollment);
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable String id) {
        service.delete(id);
    }
}