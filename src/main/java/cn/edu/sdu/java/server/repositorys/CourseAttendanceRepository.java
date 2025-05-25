package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.CourseAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseAttendanceRepository extends JpaRepository<CourseAttendance,Integer> {
    @Query(value="from CourseAttendance where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
    List<CourseAttendance> findByStudentCourse(Integer studentId, Integer courseId);
    @Query(value = "from CourseAttendance where (student.personId=?1) and (course.courseId=?2)")
    List<CourseAttendance> findByCourseAndStudent(Integer studentId,Integer courseId);
    List<CourseAttendance> findByStudentPersonId(Integer studentId);
    @Query(value = "from CourseAttendance where student.person.num=?1 and student.person.name=?2 and (course.courseId=?3 or ?3=0)")
    List<CourseAttendance> findByNumName(String num,String name,Integer courseId);

    List<CourseAttendance> findByCourse_CourseId(Integer courseId);
}
