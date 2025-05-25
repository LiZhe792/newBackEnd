package cn.edu.sdu.java.server.init;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Marks;
import cn.edu.sdu.java.server.models.Ranks;
import cn.edu.sdu.java.server.models.Student;

import cn.edu.sdu.java.server.repositorys.MarksRepository;
import cn.edu.sdu.java.server.repositorys.RanksRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RanksInit {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MarksRepository marksRepository;
    @Autowired
    private RanksRepository ranksRepository;

    private Integer studentId;
    int maxId=1;
    public void initialize(){
        List<Student> studentList=studentRepository.findAll();
        for(Student s:studentList){
            studentId=s.getPersonId();
            Optional<Ranks> ranksOp=ranksRepository.getByStudentId(studentId);
            List<Marks> marksList=marksRepository.getByStudentId(studentId);
            if(ranksOp.isPresent()){
                reConstruct(marksList,s, ranksOp.get());
            }else {
                construct(marksList,s);
            }
            //ranksRepository.save(r);
        }
        List<Ranks> ranksList=ranksRepository.findAll();
        for(Ranks r:ranksList){
            if(!studentList.contains(r.getStudent())){
                ranksRepository.delete(r);

            }
        }
        setOrder();
    }
    private void reConstruct(List<Marks> marksList,Student s,Ranks r){
        Double credit;
        Course c;
        Double multiSum=0.0;
        Double creditSum=0.0;
        if(marksList.isEmpty()){
            r.setAvgMarks(0.00);
            r.setAvgGPA(0.00);
        }
        else{
            for(Marks m:marksList){
                c=m.getCourseChoose().getCourse();
                credit= Double.valueOf(c.getCredit());
                creditSum+=credit;
                Double marks=m.getMarks();
                if (marks==null){
                    marks=0.0;
                }
                multiSum+=marks*credit;
            }
            r.setAvgMarks(multiSum/creditSum);
            double avgGPA=(r.getAvgMarks()-50)/10;
            if(avgGPA<0){
                avgGPA=0.0;
            }
            r.setAvgGPA(avgGPA);
        }

        ranksRepository.save(r);
    }
    private void construct(List<Marks> marksList,Student s){
        Double credit;//每一门课学分
        Course c;
        Ranks r=new Ranks();
        Double multiSum=0.0;//课程总数
        Double creditSum=0.0;//总学分

        if(marksList.isEmpty()){
            r.setStudent(s);
            r.setAvgMarks(0.00);
            r.setAvgGPA(0.00);
        }
        else{
            for(Marks m:marksList){
                c=m.getCourseChoose().getCourse();
                credit= Double.valueOf(c.getCredit());
                creditSum+=credit;
                Double marks=m.getMarks();
                if (marks==null){
                    marks=0.0;
                }
                multiSum+=marks*credit;
            }

            r.setStudent(s);
            r.setAvgMarks(multiSum/creditSum);
            double avgGPA=(r.getAvgMarks()-50)/10;
            if(avgGPA<0){
                avgGPA=0.0;
            }
            r.setAvgGPA(avgGPA);
        }

        ranksRepository.save(r);
    }
    private void setOrder(){
        List<Ranks> ranksList=ranksRepository.findAll();
        Ranks[] newList=getSortedList(ranksList);
        for(int i=0;i< newList.length;i++){
            newList[i].setTotalRanks(String.valueOf(i+1));
            ranksRepository.save(newList[i]);
        }
    }
    Ranks[] getSortedList(List<Ranks> mapList){
        Ranks[] ranks= mapList.toArray(new Ranks[0]);
        Arrays.sort(ranks, new Comparator<Ranks>() {
            @Override
            public int compare(Ranks o1, Ranks o2) {
                return o2.getAvgMarks().compareTo(o1.getAvgMarks());
            }


        });
        return ranks;
    }
}
