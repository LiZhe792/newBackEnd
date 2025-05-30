package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseChoose;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseChooseRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/score")
public class ScoreController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseChooseRepository courseChooseRepository;
    //public ScoreController(ScoreService scoreService) {
    //    this.scoreService = scoreService;
    //}
    @PostMapping("/getStudentItemOptionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getCourseItemOptionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Course> sList = courseRepository.findAll();  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getScoreList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse getScoreList(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<Score> sList = scoreRepository.findByStudentCourse(studentId, courseId);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (Score s : sList) {
            m = new HashMap();
            m.put("scoreId", s.getScoreId()+"");
            m.put("studentId",s.getStudent().getPersonId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseNum",s.getCourse().getNum());
            m.put("courseName",s.getCourse().getName());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("marks",""+s.getMarks());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("getScoreListByNumName")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse getScoreListByNumName(@Valid@RequestBody DataRequest dataRequest){
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
        List<Score> scoreList=scoreRepository.findByStudentNumName(num,name,courseId);
        List dataList = new ArrayList();
        Map m;
        for (Score s : scoreList) {
            m = new HashMap();
            m.put("scoreId", s.getScoreId()+"");
            m.put("studentId",s.getStudent().getPersonId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseNum",s.getCourse().getNum());
            m.put("courseName",s.getCourse().getName());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("marks",""+s.getMarks());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/scoreSave")
    @PreAuthorize("hasRole('ADMIN')  or hasRole('TEACHER')")
    public DataResponse scoreSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer marks = dataRequest.getInteger("marks");
        Integer scoreId = dataRequest.getInteger("scoreId");

        Optional<Score> op;
        Score s = null;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            Optional<CourseChoose> optional= courseChooseRepository.findByStudentOrCourse(studentId,courseId);
            if(!optional.isPresent()){
                return CommonMethod.getReturnMessageError("该学生没有选这门课，无法添加");
            }
            s = new Score();
            s.setStudent(studentRepository.findById(studentId).get());
            s.setCourse(courseRepository.findById(courseId).get());
        }
        if (marks==null || marks<0 || marks>100){
            return CommonMethod.getReturnMessageError("分数格式错误");
        }
        s.setMarks(marks);
        scoreRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/scoreNew")
    @PreAuthorize("hasRole('ADMIN')  or hasRole('TEACHER')")
    public DataResponse scoreNew(@Valid@RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer marks = dataRequest.getInteger("marks");
        Integer scoreId = dataRequest.getInteger("scoreId");

        Optional<Score> op;
        op=scoreRepository.findOpByStudentCourse(studentId,courseId);
        if(op.isPresent()){
            return CommonMethod.getReturnMessageError("该学生这门课程已经存在，请勿重复添加");
        }
        Score s = null;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            Optional<CourseChoose> optional= courseChooseRepository.findByStudentOrCourse(studentId,courseId);
            if(!optional.isPresent()){
                return CommonMethod.getReturnMessageError("该学生没有选这门课，无法添加");
            }
            s = new Score();
            s.setStudent(studentRepository.findById(studentId).get());
            s.setCourse(courseRepository.findById(courseId).get());
        }
        if (marks==null || marks<0 || marks>100){
            return CommonMethod.getReturnMessageError("分数格式错误");
        }
        s.setMarks(marks);
        scoreRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/scoreDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op;
        Score s = null;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);
            if(op.isPresent()) {
                s = op.get();
                scoreRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    public void localDeleteCourse(Integer courseId){
        List<Score> scoreList=scoreRepository.findByCourse_CourseId(courseId);
        scoreRepository.deleteAll(scoreList);
    }

    public void localDeleteStudent(Integer courseId){
        List<Score> scoreList=scoreRepository.findByStudentPersonId(courseId);
        scoreRepository.deleteAll(scoreList);
    }

    public void localDeleteChoose(Integer personId,Integer courseId){
        Optional<Score> op=scoreRepository.findOpByStudentCourse(personId,courseId);
        op.ifPresent(score -> scoreRepository.delete(score));
    }
}
