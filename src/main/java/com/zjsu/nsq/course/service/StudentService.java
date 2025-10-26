package com.zjsu.nsq.course.service;

import com.zjsu.nsq.course.model.Student;
import com.zjsu.nsq.course.model.Enrollment;
import com.zjsu.nsq.course.repository.StudentRepository;
import com.zjsu.nsq.course.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class StudentService {
    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;

    // 邮箱格式正则表达式
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public StudentService(StudentRepository studentRepo, EnrollmentRepository enrollmentRepo) {
        this.studentRepo = studentRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    public List<Student> findAll() {
        return studentRepo.findAll();
    }

    public Optional<Student> findById(String id) {
        return studentRepo.findById(id);
    }

    public Student create(Student student) {
        // 验证学号
        if (student.getStudentId() == null || student.getStudentId().isEmpty())
            throw new RuntimeException("学号不能为空");
        if (studentRepo.findByStudentId(student.getStudentId()).isPresent())
            throw new RuntimeException("学号已存在");

        // 验证姓名
        if (student.getName() == null || student.getName().isEmpty())
            throw new RuntimeException("学生姓名不能为空");

        // 验证邮箱格式
        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            if (!isValidEmail(student.getEmail())) {
                throw new RuntimeException("邮箱格式不正确");
            }
        }

        student.setId(UUID.randomUUID().toString());
        student.setCreatedAt(LocalDateTime.now());
        return studentRepo.save(student);
    }

    public Student update(String id, Student newStu) {
        Student old = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        // 验证学号唯一性
        studentRepo.findByStudentId(newStu.getStudentId())
                .ifPresent(s -> { if (!s.getId().equals(id)) throw new RuntimeException("学号重复"); });

        // 验证邮箱格式
        if (newStu.getEmail() != null && !newStu.getEmail().isEmpty()) {
            if (!isValidEmail(newStu.getEmail())) {
                throw new RuntimeException("邮箱格式不正确");
            }
        }

        old.setStudentId(newStu.getStudentId());
        old.setName(newStu.getName());
        old.setMajor(newStu.getMajor());
        old.setGrade(newStu.getGrade());
        old.setEmail(newStu.getEmail());
        return studentRepo.save(old);
    }

    public void delete(String id) {
        // 检查学生是否存在
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        // 检查学生是否有选课记录
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(student.getStudentId());
        if (!enrollments.isEmpty()) {
            throw new RuntimeException("该学生存在选课记录，无法删除");
        }

        // 如果没有选课记录，则删除学生
        studentRepo.deleteById(id);
    }

    // 邮箱格式验证
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}