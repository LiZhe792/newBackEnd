package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 学生学科竞赛表实体类
 * Integer id 主键
 * Integer personId 学生personId
 * String competitionName 竞赛名称
 * String awardLevel 获奖等级
 * String awardDate 获奖日期
 */
@Getter
@Setter
@Entity
@Table(name = "student_competition")
public class StudentCompetition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer personId;

    private String competitionName;

    private String awardLevel;

    private String awardDate;
}