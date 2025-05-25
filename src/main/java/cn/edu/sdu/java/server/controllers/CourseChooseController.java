package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseChoose;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseChooseRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courseChoose")
public class CourseChooseController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseChooseRepository courseChooseRepository;
    @Autowired
    private MarksController marksController;
    @Autowired
    private HomeWorkController homeWorkController;
    @Autowired
    private CourseAttendanceController courseAttendanceController;
    @Autowired
    private ScoreController scoreController;

    @RequestMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Course> sList = courseRepository.findAll();
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/courseSelectForStudent")
    public DataResponse courseSelectForStudent(@Valid @RequestBody DataRequest dataRequest) {
        String courseNum= dataRequest.getString("courseNum");
        String courseName= dataRequest.getString("courseName");
        String studentNum= dataRequest.getString("studentNum");
        String studentName= dataRequest.getString("studentName");
        Course c;
        Student s;
        Optional<Course> op1=courseRepository.findByNumAndName(courseNum,courseName);
        if(!op1.isPresent()){
            return CommonMethod.getReturnMessageError("找不到该课程");
        }
        Optional<Student> op2=studentRepository.getByNumAndName(studentNum,studentName);
        if(!op2.isPresent()){
            return CommonMethod.getReturnMessageError("没有该学生");
        }
        Optional<CourseChoose> sOp=courseChooseRepository.totalSearch(courseNum,studentNum);
        if(sOp.isPresent()){
            return CommonMethod.getReturnMessageError("已经选了这门课");
        }
        c=op1.get();
        s=op2.get();
        CourseChoose courseChoose=new CourseChoose();
        courseChoose.setStudent(s);
        courseChoose.setCourse(c);
        courseChoose.setTeacher("李学庆");
        courseChooseRepository.save(courseChoose);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/courseDeleteForStudent")
    public DataResponse DeleteCourseChoose(@Valid @RequestBody DataRequest dataRequest) {
        String courseNum= dataRequest.getString("courseNum");
        String courseName= dataRequest.getString("courseName");
        String num= dataRequest.getString("studentNum");
        String name= dataRequest.getString("studentName");
        if(courseNum==null){
            courseNum="";
        }
        if(courseName==null){
            courseName="";
        }
        if(num==null){
            num="";
        }
        if(name==null){
            name="";
        }
        Optional<Course> op1=courseRepository.findByNumAndName(courseNum,courseName);
        Optional<Student> op2=studentRepository.getByNumAndName(num,name);
        if(!op1.isPresent() || !op2.isPresent()){
            return CommonMethod.getReturnMessageError("学生或课程不存在");
        }
        Optional<CourseChoose> op=courseChooseRepository.findOpByStudentCourse(op2.get().getPersonId(),op1.get().getCourseId());
        if(!op.isPresent()){
            return CommonMethod.getReturnMessageError("没有这条选课记录");
        }
        Optional<CourseChoose> sOp=courseChooseRepository.totalSearch(courseNum,num);
        if(!sOp.isPresent()){
            return CommonMethod.getReturnMessageError("没有选这门课哦");
        }
        CourseChoose courseChoose=op.get();
        courseChooseRepository.delete(courseChoose);

        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/getCourseChooseList")
    public DataResponse getCourseAttendanceList(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<CourseChoose> cList=courseChooseRepository.findByStudentCourse(studentId,courseId);
        List dataList=new ArrayList<>();
        Map m;
        for(CourseChoose s:cList){
            if (s != null) {
                m = new HashMap();
                m.put("id", s.getId()+"");
                m.put("studentId",s.getStudent().getPersonId()+"");
                m.put("courseId",s.getCourse().getCourseId()+"");
                m.put("courseName",s.getCourse().getName());
                m.put("credit",s.getCourse().getCredit());
                m.put("studentNum",s.getStudent().getPerson().getNum());
                m.put("studentName",s.getStudent().getPerson().getName());
                m.put("className",s.getStudent().getClassName());
                m.put("teacher",s.getTeacher());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/courseChooseEditSave")
    public DataResponse courseChooseEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer id=dataRequest.getInteger("id");
        String teacher=dataRequest.getString("teacher");
        Optional<CourseChoose> op;
        CourseChoose s=null;
        if(id!=null){
            op=courseChooseRepository.findById(id);
            if(op.isPresent()){
                s=op.get();

            }
        }
        if(s == null) {
            s = new CourseChoose();
            s.setStudent(studentRepository.findById(studentId).get());
            s.setCourse(courseRepository.findById(courseId).get());
        }
        s.setTeacher(teacher);
        courseChooseRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/deleteCourseChoose")
    public DataResponse deleteCourseChoose(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Optional<CourseChoose> op ;
        CourseChoose c=null;
        if(id != null){
            op  = courseChooseRepository.findById(id);
            if(op.isPresent()) {
                c=op.get();
                homeWorkController.localCourseChooseDelete(id);
                marksController.localChooseDelete(id);
                courseAttendanceController.chooseDelete(c.getStudent().getPersonId(),c.getCourse().getCourseId());
                scoreController.localDeleteChoose(c.getStudent().getPersonId(),c.getCourse().getCourseId());
                courseChooseRepository.delete(c);
            } else {
                return CommonMethod.getReturnMessageError("没有此记录");
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    @Transactional
    @PostMapping("/newCourseChoose")
    public DataResponse newCourseChoose(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        String teacher = dataRequest.getString("teacher");

        Optional<CourseChoose> op=courseChooseRepository.findByStudentOrCourse(studentId,courseId);
        if(op.isPresent() ){
            return CommonMethod.getReturnMessageError("该学生对应课程记录已经存在，请选择修改而不要重复添加");
        }

        CourseChoose c=new CourseChoose();
        c.setStudent(studentRepository.findById(studentId).get());
        c.setCourse(courseRepository.findById(courseId).get());
        c.setTeacher(teacher);
        //c.setId(courseChooseRepository.getMaxId()+1);
        courseChooseRepository.save(c);

        return CommonMethod.getReturnMessageOK();
    }
    public void localChooseDelete(Integer courseId){
        List<CourseChoose> courseChooseList=courseChooseRepository.findByCourse_CourseId(courseId);
        courseChooseRepository.deleteAll(courseChooseList);
    }
    public void localStudentDelete(Integer studentId){
        List<CourseChoose> studentList=courseChooseRepository.findByStudentPersonId(studentId);
        courseChooseRepository.deleteAll(studentList);
    }
    @PostMapping("/getCourseSelectedList")
    public DataResponse getCourseSelectedList(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("studentNum");
        String name=dataRequest.getString("name");
        List<Course> all=courseRepository.findAll();
        List<CourseChoose> selectedList=courseChooseRepository.getByStudent(num,name);
        List<Course> selectedCourses=new ArrayList<>();
        for(CourseChoose ch:selectedList){
            if(!selectedCourses.contains(ch.getCourse())){
                selectedCourses.add(ch.getCourse());
            }
        }
        List dataList = new ArrayList();
        Map m;
        Course c;
        for (Course course:all) {
            if (!selectedCourses.contains(course)) {
                m = new HashMap();
                c = course;
                m.put("courseId", c.getCourseId() + "");
                m.put("num", c.getNum());
                m.put("name", c.getName());
                m.put("credit", c.getCredit());
                m.put("time", c.getTime());
                m.put("position", c.getPosition());
                m.put("resource", c.getResource());
                m.put("preCourse", c.getPreCourse());
                m.put("type", c.getType());
                m.put("exam", c.getExam());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/getNoSCourseForStudentList")
    public DataResponse courseNotSelectedForStudent(@Valid @RequestBody DataRequest dataRequest){
        String num= dataRequest.getString("studentNum");
        String name= dataRequest.getString("name");
        List<Course> all=courseRepository.findAll();
        List<CourseChoose> selectedList=courseChooseRepository.getByStudent(num,name);
        List<Course> selectedCourses=new ArrayList<>();
        for(CourseChoose ch:selectedList){
            if(!selectedCourses.contains(ch.getCourse())){
                selectedCourses.add(ch.getCourse());
            }
        }
        List dataList = new ArrayList();
        Map m;
        Course c;
        for (Course course:all) {
            if(!selectedCourses.contains(course)){
                m = new HashMap();
                c=course;
                m.put("courseId", c.getCourseId()+"");
                m.put("num",c.getNum());
                m.put("name",c.getName());
                m.put("credit",c.getCredit());
                m.put("time",c.getTime());
                m.put("position",c.getPosition());
                m.put("resource",c.getResource());
                m.put("preCourse",c.getPreCourse());
                m.put("type",c.getType());
                m.put("exam",c.getExam());
                dataList.add(m);
            }



        }
        return CommonMethod.getReturnData(dataList);
    }
}
