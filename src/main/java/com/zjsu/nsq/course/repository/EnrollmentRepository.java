package com.zjsu.nsq.course.repository;

import com.zjsu.nsq.course.model.Course;
import com.zjsu.nsq.course.model.Enrollment;
import com.zjsu.nsq.course.model.EnrollmentStatus;
import com.zjsu.nsq.course.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // 按课程、学生、状态组合查询
    List<Enrollment> findByCourseAndStudentAndStatus(Course course, Student student, EnrollmentStatus status);

    // 按课程查询
    List<Enrollment> findByCourse(Course course);

    // 按学生查询
    List<Enrollment> findByStudent(Student student);

    // 按状态查询
    List<Enrollment> findByStatus(EnrollmentStatus status);

    // 按课程和状态查询
    List<Enrollment> findByCourseAndStatus(Course course, EnrollmentStatus status);

    // 按学生和状态查询
    List<Enrollment> findByStudentAndStatus(Student student, EnrollmentStatus status);

    // 统计课程活跃人数
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course AND e.status = 'ACTIVE'")
    Long countActiveEnrollmentsByCourse(@Param("course") Course course);

    // 判断学生是否已选课（活跃状态）
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e WHERE e.course = :course AND e.student = :student AND e.status = 'ACTIVE'")
    boolean existsActiveEnrollment(@Param("course") Course course, @Param("student") Student student);

    // 查找特定课程和学生的选课记录
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.student.id = :studentId AND e.status = :status")
    Optional<Enrollment> findByCourseIdAndStudentIdAndStatus(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId,
            @Param("status") EnrollmentStatus status);
    // 统计学生的活跃选课数量
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student = :student AND e.status = 'ACTIVE'")
    Long countActiveEnrollmentsByStudent(@Param("student") Student student);

}