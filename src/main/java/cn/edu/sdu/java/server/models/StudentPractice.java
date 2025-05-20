package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * StudentPractice学生社会实践表实体类 保存每个学生的社会实践信息，
 * Integer practiceId 社会实践表 student_practice 主键 practice_id
 * Student student 关联到该社会实践的学生对象，practice_id 关联 student 表主键 person_id
 * String practiceName 实践名称
 * String practiceType 实践类型
 * Date startTime 开始时间
 * Date endTime 结束时间
 * String organization 组织单位
 * String practiceContent 实践内容
 * String practiceResult 实践成果
 * String practiceSummary 实践总结
 * String practicePlace 实践地点
 */
@Getter
@Setter
@Entity
@Table(name = "student_practice",
        uniqueConstraints = {
        })
public class StudentPractice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer practiceId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;

    @Size(max = 50)
    private String practiceName;


    @Temporal(TemporalType.DATE)
    private Date startTime;

    @Temporal(TemporalType.DATE)
    private Date endTime;

    @Size(max = 50)
    private String organization;

    @Size(max = 255)
    private String practiceContent;

    @Size(max = 255)
    private String practiceResult;

    @Size(max = 255)
    private String practiceSummary;

    @Size(max = 50)
    private String practicePlace;
}