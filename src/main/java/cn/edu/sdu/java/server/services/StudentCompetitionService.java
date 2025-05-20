package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.StudentCompetition;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentCompetitionRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentCompetitionService {
    private final StudentCompetitionRepository competitionRepository;
    private final PersonRepository personRepository;

    public StudentCompetitionService(StudentCompetitionRepository competitionRepository, PersonRepository personRepository) {
        this.competitionRepository = competitionRepository;
        this.personRepository = personRepository;
    }

    // 查询竞赛列表，支持学号/姓名模糊查询
    public DataResponse getCompetitionList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<StudentCompetition> list = competitionRepository.findByNumName(numName == null ? "" : numName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (StudentCompetition sc : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", sc.getId());
            m.put("personId", sc.getPersonId());
            m.put("competitionName", sc.getCompetitionName());
            m.put("awardLevel", sc.getAwardLevel());
            m.put("awardDate", sc.getAwardDate());
            // 关联学生学号、姓名
            Optional<Person> op = personRepository.findById(sc.getPersonId());
            if (op.isPresent()) {
                Person p = op.get();
                m.put("num", p.getNum());
                m.put("name", p.getName());
            } else {
                m.put("num", "");
                m.put("name", "");
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 保存竞赛信息
    public DataResponse saveCompetition(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer id = CommonMethod.getInteger(form, "id");
        StudentCompetition sc = null;
        if (id != null) {
            Optional<StudentCompetition> op = competitionRepository.findById(id);
            if (op.isPresent()) {
                sc = op.get();
            }
        }
        if (sc == null) {
            sc = new StudentCompetition();
        }
        sc.setPersonId(CommonMethod.getInteger(form, "personId"));
        sc.setCompetitionName(CommonMethod.getString(form, "competitionName"));
        sc.setAwardLevel(CommonMethod.getString(form, "awardLevel"));
        sc.setAwardDate(CommonMethod.getString(form, "awardDate"));
        competitionRepository.save(sc);
        return CommonMethod.getReturnMessageOK();
    }

    // 删除竞赛信息
    public DataResponse deleteCompetition(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id != null) {
            competitionRepository.deleteById(id);
        }
        return CommonMethod.getReturnMessageOK();
    }
}