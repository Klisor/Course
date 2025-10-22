package com.zjsu.nsq.course.service;

import com.zjsu.nsq.course.model.Course;
import com.zjsu.nsq.course.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {
    private final CourseRepository repo;

    public CourseService(CourseRepository repo) {
        this.repo = repo;
    }

    public List<Course> findAll() {
        return repo.findAll();
    }

    public Optional<Course> findById(String id) {
        return repo.findById(id);
    }

    public Course create(Course course) {
        course.setId(UUID.randomUUID().toString());
        course.setEnrolled(0);
        return repo.save(course);
    }

    public Course update(String id, Course newCourse) {
        newCourse.setId(id);
        return repo.save(newCourse);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}