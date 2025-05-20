package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentPractice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


import cn.edu.sdu.java.server.models.StudentPractice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * StudentPractice 数据操作接口，主要实现学生社会实践数据的查询操作
 * Optional<StudentPractice> findByPracticeId(Integer practiceId); 根据实践ID查询社会实践
 * List<StudentPractice> findByStudentPersonId(Integer personId); 根据学生ID查询该学生的所有社会实践
 * List<StudentPractice> findByPracticeName(String practiceName); 根据实践名称查询社会实践
 * List<StudentPractice> findByPracticeType(String practiceType); 根据实践类型查询社会实践
 * Page<StudentPractice> findByPracticeType(String practiceType, Pageable pageable); 分页查询某类型的社会实践
 * List<StudentPractice> findByOrganization(String organization); 根据组织单位查询社会实践
 * List<StudentPractice> findByPracticePlace(String practicePlace); 根据实践地点查询社会实践
 * List<StudentPractice> searchStudentPractice(String keyword); 根据关键词搜索社会实践(实践名称、类型、组织单位、地点)
 * Page<StudentPractice> searchStudentPractice(String keyword, Pageable pageable); 分页搜索社会实践
 */
public interface StudentPracticeRepository extends JpaRepository<StudentPractice, Integer> {
    Optional<StudentPractice> findByPracticeId(Integer practiceId);

    List<StudentPractice> findByStudentPersonId(Integer personId);

    List<StudentPractice> findByPracticeName(String practiceName);


    List<StudentPractice> findByOrganization(String organization);

    List<StudentPractice> findByPracticePlace(String practicePlace);

    @Query("SELECT sp FROM StudentPractice sp WHERE " +
            "LOWER(sp.practiceName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(sp.organization) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(sp.practicePlace) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<StudentPractice> searchStudentPractice(String keyword);

}