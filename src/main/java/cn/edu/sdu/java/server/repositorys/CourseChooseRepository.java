package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.CourseChoose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseChooseRepository extends JpaRepository<CourseChoose,Integer> {
    @Query(value = "from CourseChoose where student.person.num=?2 and course.num=?1")
    Optional<CourseChoose> totalSearch(String courseNum, String studentNum);

    @Query(value = "select max(id) from CourseChoose  ")
    Integer getMaxId();

    @Query(value="from CourseChoose where ( student.personId=?1) and ( course.courseId=?2)" )
    Optional<CourseChoose> findByStudentOrCourse(Integer studentId, Integer courseId);

    List<CourseChoose> findByStudentPersonId(Integer studentId);

    @Query(value = "from CourseChoose where course.courseId=?1")
    List<CourseChoose> findByCourse_CourseId(Integer courseId);

    @Query(value="from CourseChoose where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
    List<CourseChoose> findByStudentCourse(Integer studentId, Integer courseId);

    @Query(value = "from CourseChoose where student.personId=?1 and course.courseId=?2")
    Optional<CourseChoose> findOpByStudentCourse(Integer studentId,Integer courseId);

    @Query(value = "from CourseChoose where student.person.num=?1 and student.person.name=?2")
    List<CourseChoose> getByStudent(String num,String name);
}
