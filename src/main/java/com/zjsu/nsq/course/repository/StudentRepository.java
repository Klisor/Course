package com.zjsu.nsq.course.repository;

import com.zjsu.nsq.course.model.Student;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StudentRepository {
    private final Map<String, Student> map = new ConcurrentHashMap<>();

    public List<Student> findAll() {
        return new ArrayList<>(map.values());
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public Optional<Student> findByStudentId(String studentId) {
        return map.values().stream()
                .filter(s -> studentId.equals(s.getStudentId()))
                .findFirst();
    }

    public Student save(Student student) {
        map.put(student.getId(), student);
        return student;
    }

    public void deleteById(String id) {
        map.remove(id);
    }
}