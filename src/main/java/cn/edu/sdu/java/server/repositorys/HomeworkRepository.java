package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HomeworkRepository extends JpaRepository<Homework,Integer> {
    @Query(value = "from Homework where (?1=0 or ?1=courseChoose.student.personId) and (?2=0 or ?2=courseChoose.course.courseId)")
    List<Homework> findByStudentCourse(Integer studentId, Integer courseId);

    @Query(value = "from Homework  where courseChoose.student.person.num=?1 and courseChoose.student.person.name=?2 and (?3=courseChoose.course.courseId or ?3=0)")
    List<Homework> findByStudentNumAndName(String num,String name,Integer courseId);

    @Query(value = "from Homework where courseChoose.student.personId=?1 and courseChoose.course.courseId=?2")
    Optional<Homework> findOpByStudentCourse(Integer studentId, Integer courseId);

    @Query(value = "from Homework where courseChoose.student.personId=?1")
    List<Homework> findByCourseChooseStudentId(Integer studentId);

    @Query(value = "from Homework where  courseChoose.course.courseId=?1")
    List<Homework> findByCourseChooseCourseId(Integer courseId);
}
