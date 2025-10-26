package com.zjsu.nsq.course.service;

import com.zjsu.nsq.course.model.Enrollment;
import com.zjsu.nsq.course.repository.CourseRepository;
import com.zjsu.nsq.course.repository.EnrollmentRepository;
import com.zjsu.nsq.course.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepo;
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;

    public EnrollmentService(EnrollmentRepository enrollmentRepo,
                             CourseRepository courseRepo,
                             StudentRepository studentRepo) {
        this.enrollmentRepo = enrollmentRepo;
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
    }

    public List<Enrollment> findAll() {
        return enrollmentRepo.findAll();
    }

    public List<Enrollment> findByCourseId(String courseId) {
        return enrollmentRepo.findByCourseId(courseId);
    }

    public List<Enrollment> findByStudentId(String studentId) {
        return enrollmentRepo.findByStudentId(studentId);
    }

    public Enrollment enroll(Enrollment enrollment) {
        studentRepo.findByStudentId(enrollment.getStudentId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        var course = courseRepo.findById(enrollment.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (enrollmentRepo.existsByCourseIdAndStudentId(enrollment.getCourseId(), enrollment.getStudentId())) {
            throw new RuntimeException("重复选课");
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            throw new RuntimeException("课程已满");
        }

        course.setEnrolled(course.getEnrolled() + 1);
        courseRepo.save(course);

        enrollment.setId(UUID.randomUUID().toString());
        return enrollmentRepo.save(enrollment);
    }

    public void delete(String id) {
        Enrollment e = enrollmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("选课记录不存在"));

        courseRepo.findById(e.getCourseId()).ifPresent(c -> {
            c.setEnrolled(c.getEnrolled() - 1);
            courseRepo.save(c);
        });
        enrollmentRepo.deleteById(id);
    }
}