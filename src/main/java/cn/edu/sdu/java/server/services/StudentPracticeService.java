package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.*;

import cn.edu.sdu.java.server.models.FamilyMember;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.FamilyMemberRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class StudentPracticeService {
    private static final Logger log = LoggerFactory.getLogger(StudentPracticeService.class);
    private final StudentPracticeRepository practiceRepository;
    private final StudentRepository studentRepository;

    public StudentPracticeService(StudentPracticeRepository practiceRepository, StudentRepository studentRepository) {
        this.practiceRepository = practiceRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String, Object> getMapFromPractice(StudentPractice p) {
        Map<String, Object> m = new HashMap<>();
        if (p == null)
            return m;

        m.put("practiceId", p.getPracticeId());
        m.put("practiceName", p.getPracticeName());
        m.put("startTime", p.getStartTime());
        m.put("endTime", p.getEndTime());
        m.put("organization", p.getOrganization());
        m.put("practiceContent", p.getPracticeContent());
        m.put("practiceResult", p.getPracticeResult());
        m.put("practiceSummary", p.getPracticeSummary());
        m.put("practicePlace", p.getPracticePlace());

        Student student = p.getStudent();
        if (student != null) {
            m.put("studentPersonId", student.getPersonId());
            Person person = student.getPerson();
            if (person != null) {
                m.put("studentNum", person.getNum());
                m.put("studentName", person.getName());
                m.put("studentDept", person.getDept());
            }
        }
        return m;
    }

    public List<Map<String, Object>> getPracticeMapList(String keyword) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<StudentPractice> pList = practiceRepository.searchStudentPractice(keyword);
        if (pList == null || pList.isEmpty())
            return dataList;

        for (StudentPractice practice : pList) {
            dataList.add(getMapFromPractice(practice));
        }
        return dataList;
    }

    public DataResponse getPracticeList(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        List<Map<String, Object>> dataList = getPracticeMapList(keyword);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse practiceDelete(DataRequest dataRequest) {
        Integer practiceId = dataRequest.getInteger("practiceId");
        if (practiceId != null && practiceId > 0) {
            practiceRepository.deleteById(practiceId);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getPracticeInfo(DataRequest dataRequest) {
        Integer practiceId = dataRequest.getInteger("practiceId");
        StudentPractice practice = null;
        Optional<StudentPractice> op;
        if (practiceId != null) {
            op = practiceRepository.findById(practiceId);
            if (op.isPresent()) {
                practice = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromPractice(practice));
    }

    public DataResponse practiceEditSave(DataRequest dataRequest) {
        Integer practiceId = dataRequest.getInteger("practiceId");
        Map<String, Object> form = dataRequest.getMap("form");
        StudentPractice practice = null;


        if (practiceId != null) {
            Optional<StudentPractice> op = practiceRepository.findById(practiceId);
            if (op.isPresent()) {
                practice = op.get();
            }
        }

        if (practice == null) {
            practice = new StudentPractice();
        }

        // 设置基本信息
        practice.setPracticeName(CommonMethod.getString(form, "practiceName"));
        practice.setStartTime(CommonMethod.getDate(form, "startTime"));
        practice.setEndTime(CommonMethod.getDate(form, "endTime"));
        practice.setOrganization(CommonMethod.getString(form, "organization"));
        practice.setPracticeContent(CommonMethod.getString(form, "practiceContent"));
        practice.setPracticeResult(CommonMethod.getString(form, "practiceResult"));
        practice.setPracticeSummary(CommonMethod.getString(form, "practiceSummary"));
        practice.setPracticePlace(CommonMethod.getString(form, "practicePlace"));

        // 设置关联的学生
        String studentNum = CommonMethod.getString(form, "studentNum");
        if (studentNum != null) {
            Optional<Student> studentOp = studentRepository.findByPersonNum(studentNum);
            if (studentOp.isEmpty()) {
                return CommonMethod.getReturnMessageError("学生不存在");
            }
            practice.setStudent(studentOp.get());
        }

        practiceRepository.save(practice);
        return CommonMethod.getReturnData(getMapFromPractice(practice));
    }

    public DataResponse getPracticeListByStudent(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        List<StudentPractice> practiceList = practiceRepository.findByStudentPersonId(personId);
        List<Map<String, Object>> dataList = new ArrayList<>();

        if (practiceList != null && !practiceList.isEmpty()) {
            for (StudentPractice practice : practiceList) {
                dataList.add(getMapFromPractice(practice));
            }
        }

        return CommonMethod.getReturnData(dataList);
    }
}