package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.init.MarksInit;
import cn.edu.sdu.java.server.init.RanksInit;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cn.edu.sdu.java.server.models.Marks;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/marks")
public class MarksController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MarksRepository marksRepository;
    @Autowired
    private CourseChooseRepository courseChooseRepository;
    @Autowired
    private MarksInit marksInit;
    @Autowired
    private RanksInit ranksInit;
    @Autowired
    private RanksRepository ranksRepository;

    @PostMapping("/getStudentItemOptionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Student s : sList) {
            itemList.add(new OptionItem(s.getPersonId(), s.getPersonId() + "", s.getPerson().getNum() + "-" + s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getCourseItemOptionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<Course> sList = courseRepository.findAll();  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getCourseId() + "", c.getNum() + "-" + c.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getMarksList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse getMarksList(@Valid @RequestBody DataRequest dataRequest){
        marksInit.initialize();
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null)
            courseId = 0;
        List<Marks> marksList = marksRepository.getByCourseOrStudent(studentId, courseId);
        List dataList = new ArrayList<>();
        Map m;
        for (Marks s : marksList) {
            if (s != null) {
                m = new HashMap();
                m.put("marksId", s.getMarksId() + "");
                m.put("studentId", s.getCourseChoose().getStudent().getPersonId() + "");
                m.put("courseId", s.getCourseChoose().getCourse().getCourseId() + "");
                m.put("courseName", s.getCourseChoose().getCourse().getName());
                m.put("credit", s.getCourseChoose().getCourse().getCredit());
                m.put("studentNum", s.getCourseChoose().getStudent().getPerson().getNum());
                m.put("studentName", s.getCourseChoose().getStudent().getPerson().getName());
                if(s.getMarks().toString().length()>4){
                    m.put("marks", s.getMarks().toString().substring(0,4));
                }else {
                    m.put("marks", s.getMarks().toString());
                }
                if(s.getGPA().toString().length()>4){
                    m.put("GPA",s.getGPA().toString().substring(0,4));
                }else{
                    m.put("GPA",s.getGPA().toString());
                }
                m.put("ranks", s.getRanks());

                m.put("teacher", s.getCourseChoose().getTeacher());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/getMarksListByNumName")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse getMarksListByNumName(@Valid @RequestBody DataRequest dataRequest){
        String num= dataRequest.getString("num");
        if(num==null){
            num="";
        }
        String name= dataRequest.getString("name");
        if(name==null){
            name="";
        }
        Integer courseId=dataRequest.getInteger("courseId");
        if(courseId==null){
            courseId=0;
        }
        List<Marks> marksList=marksRepository.getByStudentNumName(num,name,courseId);
        List dataList = new ArrayList<>();
        Map m;
        for (Marks s : marksList) {
            if (s != null) {
                m = new HashMap();
                m.put("marksId", s.getMarksId() + "");
                m.put("studentId", s.getCourseChoose().getStudent().getPersonId() + "");
                m.put("courseId", s.getCourseChoose().getCourse().getCourseId() + "");
                m.put("courseName", s.getCourseChoose().getCourse().getName());
                m.put("credit", s.getCourseChoose().getCourse().getCredit());
                m.put("studentNum", s.getCourseChoose().getStudent().getPerson().getNum());
                m.put("studentName", s.getCourseChoose().getStudent().getPerson().getName());
                if(s.getMarks().toString().length()>4){
                    m.put("marks", s.getMarks().toString().substring(0,4));
                }else {
                    m.put("marks", s.getMarks().toString());
                }
                if(s.getGPA().toString().length()>4){
                    m.put("GPA",s.getGPA().toString().substring(0,4));
                }else{
                    m.put("GPA",s.getGPA().toString());
                }
                m.put("rank", s.getRanks());

                m.put("teacher", s.getCourseChoose().getTeacher());

                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse refresh(@Valid @RequestBody DataRequest dataRequest){
        try{
            marksInit.initialize();
        }catch (Exception e){
            return CommonMethod.getReturnMessageError("刷新失败");
        }
        return CommonMethod.getReturnMessageOK();
    }
    public void localCourseDelete(Integer courseId) {
        List<Marks> marksList = marksRepository.getByCourseId(courseId);
        for (Marks marks : marksList) {
            marksRepository.delete(marks);
        }
    }
    public void localStudentDelete(Integer studentId){
        List<Marks> studentList=marksRepository.getByStudentId(studentId);
        marksRepository.deleteAll(studentList);
        ranksInit.initialize();
    }
    public void localChooseDelete(Integer courseChooseId){
        Optional<Marks> list=marksRepository.getByCourseChooseId(courseChooseId);
        list.ifPresent(marks -> marksRepository.delete(marks));
    }
}
