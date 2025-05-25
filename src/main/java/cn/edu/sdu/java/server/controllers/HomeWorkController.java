package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseChoose;
import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseChooseRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homework")
public class HomeWorkController {
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private CourseChooseRepository courseChooseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<Student> sList=studentRepository.findStudentListByNumName("");
        OptionItem item;
        List<OptionItem> itemList=new ArrayList();
        for (Student s : sList) {
            itemList.add(new OptionItem(s.getPersonId(), s.getPersonId() + "", s.getPerson().getNum() + "-" + s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<Course> sList = courseRepository.findAll();  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getCourseId() + "", c.getNum() + "-" + c.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getWorkList")
    public DataResponse getWorkList(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null)
            courseId = 0;
        List<Homework> homeworkList = homeworkRepository.findByStudentCourse(studentId, courseId);
        List dataList = new ArrayList<>();
        Map m;
        for (Homework s : homeworkList) {
            if (s != null) {
                m = new HashMap();
                m.put("workId", s.getWorkId() + "");
                m.put("studentId", s.getCourseChoose().getStudent().getPersonId() + "");
                m.put("courseId", s.getCourseChoose().getCourse().getCourseId() + "");
                m.put("courseNum", s.getCourseChoose().getCourse().getNum());
                m.put("courseName", s.getCourseChoose().getCourse().getName());
                m.put("studentNum", s.getCourseChoose().getStudent().getPerson().getNum());
                m.put("studentName", s.getCourseChoose().getStudent().getPerson().getName());
                //m.put("className",s.getStudent().getClassName());
                m.put("time", s.getTime());
                m.put("marks", s.getWorkMarks());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/getWorkListByNumName")
    public DataResponse getByNumName(@Valid @RequestBody DataRequest dataRequest){
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
        List<Homework> homeworkList=homeworkRepository.findByStudentNumAndName(num,name,courseId);
        List dataList = new ArrayList<>();
        Map m;
        for (Homework s : homeworkList) {
            if (s != null) {
                m = new HashMap();
                m.put("workId", s.getWorkId() + "");
                m.put("studentId", s.getCourseChoose().getStudent().getPersonId() + "");
                m.put("courseId", s.getCourseChoose().getCourse().getCourseId() + "");
                m.put("courseNum", s.getCourseChoose().getCourse().getNum());
                m.put("courseName", s.getCourseChoose().getCourse().getName());
                m.put("studentNum", s.getCourseChoose().getStudent().getPerson().getNum());
                m.put("studentName", s.getCourseChoose().getStudent().getPerson().getName());
                m.put("time", s.getTime());
                m.put("marks", s.getWorkMarks());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/homeworkEditSave")
    public DataResponse courseAttendanceEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer attendanceId=dataRequest.getInteger("workId");
        String time = dataRequest.getString("time");
        String flag=dataRequest.getString("marks");
        Optional<Homework> op;
        Homework s=null;
        if(attendanceId!=null){
            op=homeworkRepository.findById(attendanceId);
            if(op.isPresent()){
                s=op.get();

            }
        }
        if(s == null) {
            s = new Homework();
            s.getCourseChoose().setStudent(studentRepository.findById(studentId).get());
            s.getCourseChoose().setCourse(courseRepository.findById(courseId).get());
        }
        s.setTime(time);
        s.setWorkMarks(flag);
        homeworkRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/deleteHomework")
    public DataResponse deleteCourseAttendance(@Valid @RequestBody DataRequest dataRequest){
        Integer homeworkId=dataRequest.getInteger("workId");
        Optional<Homework> op;
        Homework c=null;
        if(homeworkId!=null){
            op=homeworkRepository.findById(homeworkId);
            if(op.isPresent()){
                c=op.get();
                homeworkRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/newHomework")
    public DataResponse newCourseAttendance(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId=dataRequest.getInteger("studentId");
        Integer courseId= dataRequest.getInteger("courseId");
        String time= dataRequest.getString("time");
        String marks= dataRequest.getString("marks");
        Optional<CourseChoose> chooseOp=courseChooseRepository.findOpByStudentCourse(studentId,courseId);
        if(!chooseOp.isPresent()){
            return CommonMethod.getReturnMessageError("该学生没有选这门课，请再仔细核对一下。");
        }
        Optional<Homework> op=homeworkRepository.findOpByStudentCourse(studentId,courseId);
        if(op.isPresent()  && op.get().getTime().equals(time)){
            return CommonMethod.getReturnMessageError("该学生对应作业记录已经存在，请选择修改而不要重复添加");
        }

        Homework c=new Homework();
        c.setCourseChoose(courseChooseRepository.findOpByStudentCourse(studentId,courseId).get());
        c.getCourseChoose().setStudent(studentRepository.findById(studentId).get());
        c.getCourseChoose().setCourse(courseRepository.findById(courseId).get());
        c.setTime(time);
        c.setWorkMarks(marks);
        homeworkRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    public void localStudentDelete(Integer studentId){
        List<Homework> list=homeworkRepository.findByCourseChooseStudentId(studentId);
        homeworkRepository.deleteAll(list);
    }
    public void localCourseDelete(Integer courseId){
        List<Homework> list=homeworkRepository.findByCourseChooseCourseId(courseId);
        homeworkRepository.deleteAll(list);
    }
    public void localCourseChooseDelete(Integer courseChooseId){
        List<Homework> list=homeworkRepository.findByCourseChooseCourseId(courseChooseId);
        homeworkRepository.deleteAll(list);
    }
}
