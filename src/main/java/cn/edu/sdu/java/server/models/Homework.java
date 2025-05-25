package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "homework")
@Getter
@Setter
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workId;

    @ManyToOne
    @JoinColumn(name="id")
    private CourseChoose courseChoose;

    @Size(max=25)
    private String time;

    @Size(max=12)
    private String workMarks="0.0";
}
