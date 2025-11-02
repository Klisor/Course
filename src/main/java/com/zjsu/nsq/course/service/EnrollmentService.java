package com.zjsu.nsq.course.service;

import com.zjsu.nsq.course.model.Course;
import com.zjsu.nsq.course.model.Enrollment;
import com.zjsu.nsq.course.model.EnrollmentStatus;
import com.zjsu.nsq.course.model.Student;
import com.zjsu.nsq.course.repository.CourseRepository;
import com.zjsu.nsq.course.repository.EnrollmentRepository;
import com.zjsu.nsq.course.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseService.CourseNotFoundException("课程不存在，ID: " + courseId));
        return enrollmentRepository.findByCourse(course);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentService.StudentNotFoundException("学生不存在，ID: " + studentId));
        return enrollmentRepository.findByStudent(student);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStatus(EnrollmentStatus status) {
        return enrollmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findActiveByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseService.CourseNotFoundException("课程不存在，ID: " + courseId));
        return enrollmentRepository.findByCourseAndStatus(course, EnrollmentStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findActiveByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentService.StudentNotFoundException("学生不存在，ID: " + studentId));
        return enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.ACTIVE);
    }

    public Enrollment enroll(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseService.CourseNotFoundException("课程不存在，ID: " + courseId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentService.StudentNotFoundException("学生不存在，ID: " + studentId));

        // 检查是否已经选过该课程（活跃状态）
        if (enrollmentRepository.existsActiveEnrollment(course, student)) {
            throw new DuplicateEnrollmentException("学生已选该课程");
        }

        // 检查课程容量
        Long activeEnrollments = enrollmentRepository.countActiveEnrollmentsByCourse(course);
        if (activeEnrollments >= course.getCapacity()) {
            throw new CourseFullException("课程已满，无法选课");
        }

        // 创建选课记录
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment drop(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("选课记录不存在，ID: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new InvalidEnrollmentOperationException("只能退选活跃状态的选课");
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment dropByStudentAndCourse(Long studentId, Long courseId) {
        // 查找活跃的选课记录
        Enrollment enrollment = enrollmentRepository
                .findByCourseIdAndStudentIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE)
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        "未找到该学生的活跃选课记录 - 学生ID: " + studentId + ", 课程ID: " + courseId));

        enrollment.setStatus(EnrollmentStatus.DROPPED);

        // 更新课程已选人数
        Course course = enrollment.getCourse();
        course.setEnrolled(course.getEnrolled() - 1);
        courseRepository.save(course);

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment complete(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("选课记录不存在，ID: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new InvalidEnrollmentOperationException("只能完成活跃状态的选课");
        }

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        return enrollmentRepository.save(enrollment);
    }

    public void delete(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new EnrollmentNotFoundException("选课记录不存在，ID: " + enrollmentId);
        }
        enrollmentRepository.deleteById(enrollmentId);
    }

    @Transactional(readOnly = true)
    public Long countActiveEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseService.CourseNotFoundException("课程不存在，ID: " + courseId));
        return enrollmentRepository.countActiveEnrollmentsByCourse(course);
    }

    @Transactional(readOnly = true)
    public Long countActiveEnrollmentsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentService.StudentNotFoundException("学生不存在，ID: " + studentId));
        return enrollmentRepository.countActiveEnrollmentsByStudent(student);
    }

    // 自定义异常
    public static class EnrollmentNotFoundException extends RuntimeException {
        public EnrollmentNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateEnrollmentException extends RuntimeException {
        public DuplicateEnrollmentException(String message) {
            super(message);
        }
    }

    public static class CourseFullException extends RuntimeException {
        public CourseFullException(String message) {
            super(message);
        }
    }

    public static class InvalidEnrollmentOperationException extends RuntimeException {
        public InvalidEnrollmentOperationException(String message) {
            super(message);
        }
    }
}