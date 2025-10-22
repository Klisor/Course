package com.zjsu.nsq.course.repository;

import com.zjsu.nsq.course.model.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EnrollmentRepository {
    private final Map<String, Enrollment> map = new ConcurrentHashMap<>();

    public List<Enrollment> findAll() {
        return new ArrayList<>(map.values());
    }

    public List<Enrollment> findByCourseId(String courseId) {
        return map.values().stream()
                .filter(e -> courseId.equals(e.getCourseId()))
                .collect(Collectors.toList());
    }

    public List<Enrollment> findByStudentId(String studentId) {
        return map.values().stream()
                .filter(e -> studentId.equals(e.getStudentId()))
                .collect(Collectors.toList());
    }

    public Optional<Enrollment> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public boolean existsByCourseIdAndStudentId(String courseId, String studentId) {
        return map.values().stream()
                .anyMatch(e -> courseId.equals(e.getCourseId()) && studentId.equals(e.getStudentId()));
    }

    public Enrollment save(Enrollment enrollment) {
        map.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    public void deleteById(String id) {
        map.remove(id);
    }
}