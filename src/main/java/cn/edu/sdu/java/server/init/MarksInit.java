package cn.edu.sdu.java.server.init;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.repositorys.CourseChooseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.repositorys.MarksRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarksInit {
    @Autowired
    private CourseChooseRepository courseChooseRepository;
    @Autowired
    private MarksRepository marksRepository;
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    int maxId=1;
    public void initialize(){
        List<CourseChoose> courseChooseList=courseChooseRepository.findByStudentCourse(0,0);
        for(CourseChoose courseChoose:courseChooseList){
            Marks marks=new Marks();
            marks.setCourseChoose(courseChoose);
            Optional<Marks> op=marksRepository.getByCourseChooseId(courseChoose.getId());
            if(!op.isPresent()){
                marksRepository.save(marks);
            }

        }
        operate();
    }
    void operate(){
        List<Marks> marksList=marksRepository.findAll();
        for(Marks info:marksList){
            // List<CourseChoose> chooseList=courseChooseRepository.
            CourseChoose c= info.getCourseChoose();
            List<Homework> homeworkList=homeworkRepository.findByStudentCourse(info.getCourseChoose().getStudent().getPersonId(),info.getCourseChoose().getCourse().getCourseId());
            Optional<Score> score=scoreRepository.findOpByStudentCourse(info.getCourseChoose().getStudent().getPersonId(),info.getCourseChoose().getCourse().getCourseId());
            Double homeworkMarksAvg;
            Integer marks=0;
            if(!homeworkList.isEmpty()){
                homeworkMarksAvg=getAvg(homeworkList);
            }else{
                homeworkMarksAvg=0.0;
            }
            if(score.isPresent()){
                marks=score.get().getMarks();
            }
            if(homeworkMarksAvg==null){
                homeworkMarksAvg=0.0;

            }
            if(marks==null){
                marks=0;
            }
            Double totalMarks=homeworkMarksAvg*0.05+marks*0.95;
            info.setMarks(totalMarks);
            Double GPA=(totalMarks-50)/10;
            if(totalMarks<60){
                GPA=0.0;
            }
            info.setGPA(GPA);
            marksRepository.save(info);


        }
        List<CourseChoose> courseChooseList=courseChooseRepository.findAll();
        List<Course> courseList=new ArrayList<>();
        for(CourseChoose c:courseChooseList){
            if(!courseList.contains(c.getCourse()) ){
                courseList.add(c.getCourse());
            }
        }//从数据库中获取所有选课记录，遍历这些记录，提取出唯一的Course，存入一个新的 courseList 中，用于后续对每门课程的成绩进行处理。

        for(Course course:courseList){
            List<Marks> marks=marksRepository.getByCourseId(course.getCourseId());
            Marks[] ranksList=getSortedList(marks);//排序
            for(int i=0;i< ranksList.length;i++){
                ranksList[i].setRanks(String.valueOf(i+1));//设置名次
                marksRepository.save(ranksList[i]);
            }

        }
    }
    Double getAvg(List<Homework> arr){//计算一个 Homework 对象列表中所有作业的平均成绩
        Integer n=arr.size();
        Double sum=0.0;
        for(Homework homework:arr){
            String m= homework.getWorkMarks();
            if(m==null){
                m="0.0";
            }
            sum+=Double.parseDouble(homework.getWorkMarks());
        }
        return sum/n;
    }
    Marks[] getSortedList(List<Marks> mapList){//对一个 Marks 对象列表从高到低进行排序
        Marks[] marks= mapList.toArray(new Marks[0]);
        Arrays.sort(marks, new Comparator<Marks>() {
            @Override
            public int compare(Marks o1, Marks o2) {
                return o2.getMarks().compareTo(o1.getMarks());
            }
        });
        return marks;
    }
}
