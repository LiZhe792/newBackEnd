package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentPracticeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/practice")
public class StudentPracticeController {
    private final StudentPracticeService practiceService;

    public StudentPracticeController(StudentPracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping("/list")
    public DataResponse getPracticeList(@RequestBody DataRequest dataRequest) {
        return practiceService.getPracticeList(dataRequest);
    }

    @PostMapping("/delete")
    public DataResponse practiceDelete(@RequestBody DataRequest dataRequest) {
        return practiceService.practiceDelete(dataRequest);
    }

    @PostMapping("/info")
    public DataResponse getPracticeInfo(@RequestBody DataRequest dataRequest) {
        return practiceService.getPracticeInfo(dataRequest);
    }

    @PostMapping("/editSave")
    public DataResponse practiceEditSave(@RequestBody DataRequest dataRequest) {
        return practiceService.practiceEditSave(dataRequest);
    }



    @PostMapping("/listByStudent")
    public DataResponse getPracticeListByStudent(@RequestBody DataRequest dataRequest) {
        return practiceService.getPracticeListByStudent(dataRequest);
    }
}