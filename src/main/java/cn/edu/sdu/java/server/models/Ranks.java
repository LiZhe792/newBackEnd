package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ranks",uniqueConstraints = {})
@Getter
@Setter
public class Ranks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//说明主键是ID
    private Integer ranksId;

    @OneToOne
    @JoinColumn(name = "studentId")
    private Student student;

    private Double avgMarks;

    private Double avgGPA;

    private String totalRanks;
}
