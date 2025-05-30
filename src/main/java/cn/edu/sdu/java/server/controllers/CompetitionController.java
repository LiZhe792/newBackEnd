package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    // 添加竞赛
    @PostMapping("/competitionAdd")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') ")
    public DataResponse competitionAdd(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionAdd(dataRequest);
    }

    // 删除竞赛
    @PostMapping("/competitionDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse competitionDelete(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionDelete(dataRequest);
    }

    // 修改竞赛
    @PostMapping("/competitionUpdate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') ")
    public DataResponse competitionUpdate(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionUpdate(dataRequest);
    }

    // 查询单个竞赛
    @PostMapping("/competitionGetById")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse competitionGetById(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionGetById(dataRequest);
    }

    // 查询所有竞赛
    @PostMapping("/competitionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse competitionList() {
        return competitionService.competitionList();
    }
}