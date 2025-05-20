package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentCompetitionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studentCompetition")
public class StudentCompetitionController {
    private final StudentCompetitionService competitionService;

    public StudentCompetitionController(StudentCompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @PostMapping("/getCompetitionList")
    public DataResponse getCompetitionList(@RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionList(dataRequest);
    }

    @PostMapping("/saveCompetition")
    public DataResponse saveCompetition(@RequestBody DataRequest dataRequest) {
        return competitionService.saveCompetition(dataRequest);
    }

    @PostMapping("/deleteCompetition")
    public DataResponse deleteCompetition(@RequestBody DataRequest dataRequest) {
        return competitionService.deleteCompetition(dataRequest);
    }
}