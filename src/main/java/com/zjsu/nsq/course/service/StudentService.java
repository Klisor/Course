package com.zjsu.nsq.course.service;

import com.zjsu.nsq.course.model.Student;
import com.zjsu.nsq.course.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> findAll() {
        return repo.findAll();
    }

    public Optional<Student> findById(String id) {
        return repo.findById(id);
    }

    public Student create(Student student) {
        if (student.getStudentId() == null || student.getStudentId().isEmpty())
            throw new RuntimeException("学号不能为空");
        if (repo.findByStudentId(student.getStudentId()).isPresent())
            throw new RuntimeException("学号已存在");
        student.setId(UUID.randomUUID().toString());
        student.setCreatedAt(LocalDateTime.now());
        return repo.save(student);
    }

    public Student update(String id, Student newStu) {
        Student old = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        repo.findByStudentId(newStu.getStudentId())
                .ifPresent(s -> { if (!s.getId().equals(id)) throw new RuntimeException("学号重复"); });
        old.setStudentId(newStu.getStudentId());
        old.setName(newStu.getName());
        old.setMajor(newStu.getMajor());
        old.setGrade(newStu.getGrade());
        old.setEmail(newStu.getEmail());
        return repo.save(old);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}