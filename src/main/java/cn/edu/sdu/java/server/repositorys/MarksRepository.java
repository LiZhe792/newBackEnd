package cn.edu.sdu.java.server.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import cn.edu.sdu.java.server.models.Marks;

import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<Marks,Integer> {
    @Query(value = "from  Marks where (?1=0 or courseChoose.student.personId=?1) and (?2=0 or courseChoose.course.courseId=?2) ORDER BY ranks")
    List<Marks> getByCourseOrStudent(Integer studentId, Integer courseId);

    @Query(value = "from Marks where courseChoose.id=?1")
    Optional<Marks> getByCourseChooseId(Integer id);

    @Query(value = "from Marks  where courseChoose.course.courseId=?1")
    List<Marks> getByCourseId(Integer courseId);

    @Query(value = "from Marks where courseChoose.student.personId=?1")
    List<Marks> getByStudentId(Integer studentId);

    @Query(value = "from Marks where (?1=courseChoose.student.person.num) and (?2=courseChoose.student.person.name) and (?3=courseChoose.course.courseId or ?3=0)")
    List<Marks> getByStudentNumName(String num,String name,Integer courseId);
}
