package com.zjsu.nsq.course.controller;

import com.zjsu.nsq.course.model.Course;
import com.zjsu.nsq.course.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Course> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Course get(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @PostMapping
    public Course create(@RequestBody Course course) {
        return service.create(course);
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable String id, @RequestBody Course course) {
        return service.update(id, course);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}