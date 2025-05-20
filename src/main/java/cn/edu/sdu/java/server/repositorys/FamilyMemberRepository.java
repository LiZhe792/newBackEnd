package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
    List<FamilyMember> findByStudentPersonId(Integer personId);

    /**
     * 通过姓名和学号进行模糊查询familyMember对象
     *
     * @param numName 学号或者姓名
     * @return 查询到的内容
     */
    @Query(value = "from FamilyMember fm where ?1='' or fm.student.person.num like %?1% or fm.student.person.name like %?1% ")
    List<FamilyMember> findFamilyMemberListByNumName(String numName);
}
