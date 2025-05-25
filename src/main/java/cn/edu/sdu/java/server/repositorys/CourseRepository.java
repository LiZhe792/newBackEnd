package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1% ")
    List<Course> findCourseListByNumName(String numName);
    @Query(value = "from  Course where num=?1 or name=?2")
    Optional<Course> findByNumOrName(String num,String name);
    @Query(value = "from Course where num=?1 and name=?2")
    Optional<Course> findByNumAndName(String num,String name);


    Optional<Course> findByNum(String num);
    Optional<Course> findByName(String name);
}
