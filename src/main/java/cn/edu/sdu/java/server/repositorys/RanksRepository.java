package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Ranks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RanksRepository extends JpaRepository<Ranks,Integer> {
    @Query(value = "from Ranks where student.personId=?1")
    Optional<Ranks> getByStudentId(Integer studentId);

    @Query(value = "from Ranks where ?1='' or student.person.num like %?1% or student.person.name like %?1% ORDER BY totalRanks")
    List<Ranks> getByStudentNumName(String numName);

    @Query(value = "select max(ranksId) from Ranks  ")
    Integer getMaxId();

    @Query(value = "from Ranks where totalRanks=?1 or ?1=''")
    List<Ranks> getByTotalRanks(String ranks);
}
