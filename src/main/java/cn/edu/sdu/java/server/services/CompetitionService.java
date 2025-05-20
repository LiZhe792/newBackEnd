package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CompetitionRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    // 添加竞赛
    public DataResponse competitionAdd(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, Object> competitionMap = dataRequest.getMap("competitionMap");
        String name = CommonMethod.getString(competitionMap, "name");
        String subject = CommonMethod.getString(competitionMap, "subject");
        String level = CommonMethod.getString(competitionMap, "level");

        Competition competition = new Competition();
        competition.setName(name);
        competition.setLevel(level);
        competition.setSubject(subject);

        competitionRepository.save(competition);
        return CommonMethod.getReturnMessageOK();
    }

    // 删除竞赛
    @Transactional
    public DataResponse competitionDelete(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        if (competitionId == null) {
            return DataResponse.error("竞赛ID不能为空");
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (!competitionOptional.isPresent()) {
            return DataResponse.error("竞赛不存在");
        }

        competitionRepository.deleteById(competitionId);
        return CommonMethod.getReturnMessageOK();
    }

    // 修改竞赛
    @Transactional
    public DataResponse competitionUpdate(DataRequest dataRequest) {
        Map<String, Object> competitionMap = dataRequest.getMap("competitionMap");
        Integer competitionId = CommonMethod.getInteger(competitionMap, "competitionId");

        if (competitionId == null) {
            return DataResponse.error("竞赛ID不能为空");
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (!competitionOptional.isPresent()) {
            return DataResponse.error("竞赛不存在");
        }

        Competition competition = competitionOptional.get();

        // 更新字段
        String name = CommonMethod.getString(competitionMap, "name");
        String subject = CommonMethod.getString(competitionMap, "subject");
        String level = CommonMethod.getString(competitionMap, "level");

        if (name != null) competition.setName(name);
        if (subject != null) competition.setSubject(subject);
        if (level != null) competition.setLevel(level);

        competitionRepository.save(competition);
        return CommonMethod.getReturnMessageOK();
    }

    // 查询单个竞赛
    public DataResponse competitionGetById(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        if (competitionId == null) {
            return DataResponse.error("竞赛ID不能为空");
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (!competitionOptional.isPresent()) {
            return DataResponse.error("竞赛不存在");
        }

        return DataResponse.success(competitionOptional.get());
    }

    // 查询所有竞赛
    public DataResponse competitionList() {
        List<Competition> competitions = competitionRepository.findAll();
        return DataResponse.success(competitions);
    }
}