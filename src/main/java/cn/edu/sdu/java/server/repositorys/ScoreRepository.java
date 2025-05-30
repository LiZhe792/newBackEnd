package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/*
 * Score 数据操作接口，主要实现Score数据的查询操作
 * List<Score> findByStudentPersonId(Integer personId);  根据关联的Student的student_id查询获得List<Score>对象集合,  命名规范
 */

@Repository
public interface ScoreRepository extends JpaRepository<Score,Integer> {
    @Query(value = "from Score where student.personId=?1")
    List<Score> findByStudentPersonId(Integer personId);
    @Query(value="from Score where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
    List<Score> findByStudentCourse(Integer personId, Integer courseId);

    @Query(value = "from Score where course.courseId=?1")
    List<Score> findByCourse_CourseId(Integer courseId);

    @Query(value="from Score where student.personId=?1 and (?2=0 or course.name like %?2%)" )
    List<Score> findByStudentCourse(Integer personId, String courseName);

    @Query("from Score where student.personId=?1 and course.courseId=?2")
    Optional<Score> findOpByStudentCourse(Integer studentId, Integer courseId);

    @Query(value = "from Score where student.person.num=?1 and student.person.name=?2 and (course.courseId=?3 or ?3=0)")
    List<Score> findByStudentNumName(String num,String name,Integer courseId);

}
