package com.zjsu.nsq.course.repository;

import com.zjsu.nsq.course.model.Course;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CourseRepository {
    private final Map<String, Course> map = new ConcurrentHashMap<>();

    public List<Course> findAll() {
        return new ArrayList<>(map.values());
    }

    public Optional<Course> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public Course save(Course course) {
        map.put(course.getId(), course);
        return course;
    }

    public void deleteById(String id) {
        map.remove(id);
    }
}