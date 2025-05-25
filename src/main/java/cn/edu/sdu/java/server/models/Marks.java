package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "marks",uniqueConstraints = {})
@Setter
@Getter
public class Marks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer MarksId;

    @OneToOne
    @JoinColumn(name="id")
    private CourseChoose courseChoose;

    private Double Marks;

    private Double GPA;

    @Size(max=20)
    private String ranks;

}
