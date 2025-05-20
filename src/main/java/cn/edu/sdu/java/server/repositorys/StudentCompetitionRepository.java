package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentCompetition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentCompetitionRepository extends JpaRepository<StudentCompetition, Integer> {
    // 通过personId查找
    List<StudentCompetition> findByPersonId(Integer personId);

    // 通过学号/姓名模糊查找（join Person表）
    @Query("SELECT sc FROM StudentCompetition sc, Person p WHERE sc.personId = p.personId AND (?1 = '' OR p.num LIKE %?1% OR p.name LIKE %?1%)")
    List<StudentCompetition> findByNumName(String numName);
}